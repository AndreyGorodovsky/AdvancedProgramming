package test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor executor;
    private final Map<String, NavigableMap<String, Servlet>> servletMap = new ConcurrentHashMap<>();
    private volatile boolean running;

    public MyHTTPServer(int port, int nThreads) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(1000);
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        Comparator<String> cmpDesc = Comparator.comparingInt(String::length).reversed();
        servletMap.put("GET",    new TreeMap<>(cmpDesc));
        servletMap.put("POST",   new TreeMap<>(cmpDesc));
        servletMap.put("DELETE", new TreeMap<>(cmpDesc));

        this.running = false;
    }

    @Override
    public void addServlet(String httpCommand, String uriPrefix, Servlet s) {
        NavigableMap<String, Servlet> map = servletMap.get(httpCommand);
        if (map != null) map.put(uriPrefix, s);
    }

    @Override
    public void removeServlet(String httpCommand, String uriPrefix) {
        NavigableMap<String, Servlet> map = servletMap.get(httpCommand);
        if (map != null) map.remove(uriPrefix);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Socket client = serverSocket.accept();
                executor.submit(() -> handleClient(client));
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket client) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out      = client.getOutputStream()
        ) {
            RequestParser.RequestInfo ri = RequestParser.parseRequest(reader);
            Servlet servlet = findServlet(ri.getHttpCommand(), ri.getUri());
            if (servlet != null) {
                servlet.handle(ri, out);
            } else {
                out.write(("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n")
                        .getBytes());
            }
        } catch (Exception e) {
            try (OutputStream out = client.getOutputStream()) {
                out.write(("HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n")
                        .getBytes());
            } catch (IOException ignored) {}
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    private Servlet findServlet(String method, String uri) {
        NavigableMap<String, Servlet> map = servletMap.get(method);
        if (map == null) return null;
        for (String prefix : map.keySet()) {
            if (uri.startsWith(prefix)) {
                return map.get(prefix);
            }
        }
        return null;
    }

    @Override
    public void close() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}

        executor.shutdown();

        for (NavigableMap<String, Servlet> m : servletMap.values()) {
            for (Servlet s : m.values()) {
                try { s.close(); } catch (IOException ignored) {}
            }
        }
    }

    public ThreadPoolExecutor getThreadPool() {
        return executor;
    }
}
