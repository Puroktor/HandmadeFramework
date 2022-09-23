package ru.vsu.csf.skofenko.server;

import ru.vsu.csf.skofenko.server.http.Server;
import ru.vsu.csf.skofenko.server.http.Servlet;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarFile;

public class WebServerApplication {

    public static final int PORT = Integer.parseInt(AppProperties.get("port"));
    public static final String DOCKER_PATH = AppProperties.get("docker-path");
    private static final ConcurrentMap<String, Servlet> dispatchers = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        createServlets();
        try (ServerSocket s = new ServerSocket(PORT)) {
            printSuccess();
            while (true) {
                Socket ClientSocket = s.accept();
                try {
                    new Thread(new Server(ClientSocket)).start();
                } catch (Exception e) {
                    ClientSocket.close();
                }
            }
        }
    }

    private static void printSuccess() {
        System.out.printf("INFO: Created %d dispatcher servlets %n", dispatchers.size());
        System.out.printf("INFO: WebServer started on port %d%n", PORT);
    }

    public static Optional<Servlet> getServlet(String name) {
        return Optional.ofNullable(dispatchers.get(name));
    }

    public static File[] getAllFiles() {
        return new File(DOCKER_PATH).listFiles();
    }

    public static void createServlets() throws IOException {
        for (File file : getAllFiles()) {
            if (file.getName().endsWith(".jar")) {
                JarFile jarFile = new JarFile(file);
                dispatchers.put(jarFile.getName().substring(DOCKER_PATH.length() + 1,
                        jarFile.getName().length() - ".jar".length()), new Servlet(jarFile));
            }
        }
    }
}