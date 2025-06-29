package servlets;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import configs.*;
import views.*;
import graph.TopicManagerSingleton;

public class ConfLoader implements Servlet {
    @Override
    public void handle(server.RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        // Clear all topics and agents before loading a new config
        TopicManagerSingleton.get().clear();

        // Save uploaded config file to a temporary file
        File tempFile = File.createTempFile("uploaded", ".conf");
        String filePath = tempFile.getAbsolutePath();

        byte[] rawBody = ri.getContent();
        String bodyStr = new String(rawBody, StandardCharsets.UTF_8);

        String delimiter = "\r\n\r\n";
        int fileStart = bodyStr.indexOf(delimiter);
        if (fileStart == -1) {
            String resp = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n";
            toClient.write(resp.getBytes(StandardCharsets.UTF_8));
            return;
        }
        fileStart += delimiter.length();

        String boundary = bodyStr.substring(0, bodyStr.indexOf("\r\n"));
        int fileEnd = bodyStr.indexOf(boundary, fileStart);
        if (fileEnd < fileStart) fileEnd = bodyStr.length();

        // Extract the file content
        String fileContent = bodyStr.substring(fileStart, fileEnd);
        fileContent = fileContent.replaceAll("[\\r\\n]+$", "");

        // Save the file
        Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));

        try {
            // Load config and create graph
            GenericConfig config = new GenericConfig();
            config.setConfFile(filePath);
            config.create();

            Graph graph = new Graph();
            graph.createFromTopics();

            List<String> htmlList = HtmlGraphWriter.getGraphHTML(graph);
            String html = String.join("", htmlList);

            // After loading new config, refresh the topics table in the right iframe
            html += "<script>if (parent && parent.frames && parent.frames['right']){parent.frames['right'].location='/publish';}</script>";

            byte[] response = ("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: "
                    + html.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + html).getBytes(StandardCharsets.UTF_8);
            toClient.write(response);
        } catch (Exception e) {
            e.printStackTrace();
            String resp = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n";
            toClient.write(resp.getBytes(StandardCharsets.UTF_8));
        } finally {
            // Clean up the temp file
            tempFile.delete();
        }
    }
    @Override
    public void close() throws IOException {}
} 