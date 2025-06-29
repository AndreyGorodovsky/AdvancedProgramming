package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

/**
 * HTTP servlet that returns a JSON array of all topics managed by {@link TopicManagerSingleton}.
 * Each element in the array has the following structure:
 * <pre>
 *     {
 *       "name"  : "&lt;topic-name&gt;",
 *       "value" : "&lt;latest-message-value&gt;"
 *     }
 * </pre>
 * The servlet is read-only – it never modifies server state – and can therefore be safely
 * called concurrently.
 */
public class TopicsJsonServlet implements Servlet {
    /**
     * Handle an HTTP request by serialising all topics to JSON and writing the response.
     * Steps:
     * <ol>
     *     <li>Fetch the current collection of topics.</li>
     *     <li>Transform each topic into a minimal JSON object containing its name and the
     *         value of its most recent message (empty string if no messages yet).</li>
     *     <li>Join the individual JSON snippets into a JSON array.</li>
     *     <li>Write an HTTP 200 response containing the generated JSON.</li>
     * </ol>
     */
    @Override
    public void handle(server.RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        //  Retrieve the topic list – this is a snapshot; topics may change afterward.
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();

        // Stream over the topics and build a JSON array.
        String json = topics.stream()
                .map(t -> {
                    Message m = t.getLastMessage();
                    String val = m != null ? m.getAsText() : "";
                    // Build a small JSON object. We need to escape strings to keep JSON valid.
                    return "{\"name\":\"" + escape(t.getName()) + "\",\"value\":\"" + escape(val) + "\"}";
                })
                .collect(Collectors.joining(",", "[", "]"));

        // Send response
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n\r\n";
        toClient.write(header.getBytes(StandardCharsets.UTF_8));
        toClient.write(body);
    }

    /**
     * Escape backslashes and quotes so that arbitrary text can be safely embedded in JSON.
     */
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public void close() throws IOException {}
} 