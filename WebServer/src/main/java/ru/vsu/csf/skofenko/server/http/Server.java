package ru.vsu.csf.skofenko.server.http;

import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.skofenko.server.Application;
import ru.vsu.csf.skofenko.server.http.request.HttpRequest;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.skofenko.server.http.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Server implements Runnable {

    private final Socket clientSocket;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            try (InputStream input = clientSocket.getInputStream();
                 OutputStream outputStream = clientSocket.getOutputStream()) {

                HttpRequest httpRequest = getRequest(input);
                HttpResponse httpResponse = new HttpResponse(outputStream);
                System.out.printf("INFO: New connection: %s %s %s%n",
                        httpRequest.getRequestType().name(), httpRequest.getPath(), httpRequest.getParams().toString());
                if (httpRequest.getRequestType().equals(RequestType.OTHER)) {
                    sendResponseStatus(httpResponse, HttpStatus.NOT_IMPLEMENTED);
                }
                String[] path = httpRequest.getPath().split("/");
                if (path.length < 2) {
                    sendResponseStatus(httpResponse, HttpStatus.NOT_FOUND);
                } else {
                    Optional<Servlet> servlet = Application.getServlet(path[1]);
                    if (servlet.isPresent()) {
                        servlet.get().doResponse(httpRequest, httpResponse);
                    } else {
                        sendResponseStatus(httpResponse, HttpStatus.NOT_FOUND);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResponseStatus(HttpResponse httpResponse, HttpStatus status) throws IOException {
        httpResponse.setStatus(status);
        httpResponse.send();
    }

    private HttpRequest getRequest(InputStream input) throws IOException {
        List<Integer> queue = new LinkedList<>();
        StringBuilder headers = new StringBuilder();
        StringBuilder body = new StringBuilder();
        while (true) {
            int ch = input.read();
            if (ch == -1) {
                continue;
            }
            if (queue.size() >= 4) {
                queue.remove(0);
                queue.add(ch);
                if (queue.get(0) == 13 && queue.get(1) == 10 && queue.get(2) == 13 && queue.get(3) == 10) {
                    body.append((char) ch);
                    break;
                }
            } else {
                queue.add(ch);
            }
            headers.append((char) ch);
        }
        HttpRequest httpRequest = new HttpRequest(headers.toString());

        String str = httpRequest.getHeader("Content-Length");
        if (str != null) {
            str = str.trim().replace("\r", "");
            int contentLength = Integer.parseInt(str);

            for (int i = 0; i < contentLength; i++) {
                char c = (char) input.read();
                body.append(c);
            }
            if (body.length() > 1) httpRequest.setBody(body.toString().getBytes(StandardCharsets.UTF_8));
        }
        return httpRequest;
    }
}