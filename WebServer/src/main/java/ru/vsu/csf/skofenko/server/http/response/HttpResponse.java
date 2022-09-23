package ru.vsu.csf.skofenko.server.http.response;

import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.skofenko.server.AppProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
    HttpResponse can send response by itself
 */
public class HttpResponse {

    private static final String VERSION = AppProperties.get("protocol-version");

    private final OutputStream output;

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public String putHeader(String key, String value) {
        return headers.put(key, value);
    }

    public HttpResponse(OutputStream outputStream) {
        this.output = outputStream;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void send() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(VERSION).append(" ")
                .append(status.getCode()).append(" ")
                .append(status.getTitle()).append("\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
        }
        stringBuilder.append("\n");
        byte[] bytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] res = Arrays.copyOf(bytes, bytes.length + body.length);
        System.arraycopy(body, 0, res, bytes.length, body.length);
        output.write(res);

    }
}
