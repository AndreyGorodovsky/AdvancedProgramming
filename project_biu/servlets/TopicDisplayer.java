package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Collection;
import java.nio.charset.StandardCharsets;
import server.*;
import graph.*;

public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        Map<String, String> params = ri.getParameters();
        String topicName = params.get("topic");
        String message = params.get("message");
        String errorMsg = null;
        if (topicName != null && message != null) {
            // Check that the topic exists (do not create new one)
            Topic existing = null;
            for (Topic t : TopicManagerSingleton.get().getTopics()) {
                if (t.getName().equals(topicName)) { existing = t; break; }
            }
            try {
                Double.parseDouble(message);
            } catch (NumberFormatException ex) {
                errorMsg = "Message must be numeric";
            }
            if (existing == null) {
                errorMsg = "Unknown topic: " + topicName;
            }
            if (errorMsg == null) {
                existing.publish(new Message(message));
            }
        }
        // Build HTML table 
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Topics</title></head><body>");
        html.append("<h2>Current Topics</h2>");
        if (errorMsg != null) {
            html.append("<p style='color:red;'>" + errorMsg + "</p>");
        }
        html.append("<table border='1'><tr><th>Topic</th><th>Last Value</th></tr>");
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        for (Topic t : topics) {
            html.append("<tr><td>").append(t.getName()).append("</td><td>");
            Message last = t.getLastMessage();
            html.append(last != null ? last.getAsText() : "");
            html.append("</td></tr>");
        }
        html.append("</table>");
        // notify graph iframe to refresh
        html.append("<script>if (parent && parent.frames && parent.frames['center'] && parent.frames['center'].refreshTopics){parent.frames['center'].refreshTopics();}</script>");
        html.append("</body></html>");
        byte[] response = ("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: "
                + html.length() + "\r\n\r\n" + html).getBytes(StandardCharsets.UTF_8);
        toClient.write(response);
    }
    @Override
    public void close() throws IOException {}
} 