package views;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import configs.*;

/**
 * Utility class that takes an in-memory {@link Graph} and converts it into an HTML
 * representation based on the template found in <code>html_files/graph.html</code>.
 * The produced HTML can then be served to a browser to visualise the graph on the client side.
 */
public class HtmlGraphWriter {

    /**
     * Generate the HTML snippet for the given graph.
     *
     * @param graph the graph to serialise
     * @return a singleton list whose first element is the fully-rendered HTML string
     * @throws IOException if the template file cannot be read
     */
    public static List<String> getGraphHTML(Graph graph) throws IOException {

        // Read the template that contains the placeholder where the graph JS will be injected.
        String templatePath = "html_files/graph.html";
        String template = new String(Files.readAllBytes(Paths.get(templatePath)));

        // Build a <script> block that defines the nodes and edges arrays expected by the front-end.
        StringBuilder graphData = new StringBuilder();
        graphData.append("<script>\n");

        /* ---------- Nodes ---------- */
        graphData.append("const nodes = [");
        int nodeCount = 0;
        for (Node node : graph) {
            String displayName = node.getDisplayName();
            // Heuristic: topics start with anything but 'A'; agents start with 'A'.
            String type = node.getName().startsWith("A") ? "agent" : "topic";

            // For topics, we include the latest message value; for agents leave empty.
            String value = "";
            if ("topic".equals(type) && node.getMsg() != null) {
                value = node.getMsg().getAsText();
            }
            // Get the agent's equation if it exists
            String eq = node.getEquation();
            
            graphData.append(String.format("{name: '%s', type: '%s', value: '%s', equation: '%s'},",
                    displayName, type, value, eq));
            nodeCount++;
        }
        graphData.append("]\n");

        /* ---------- Edges ---------- */
        graphData.append("const edges = [");
        int edgeCount = 0;
        for (Node node : graph) {
            for (Node edge : node.getEdges()) {
                String fromDisplay = node.getDisplayName();
                String toDisplay = edge.getDisplayName();
                graphData.append(String.format("{from: '%s', to: '%s'},", fromDisplay, toDisplay));
                edgeCount++;
            }
        }
        graphData.append("]\n</script>\n");

        // Replace the placeholder in the template with the generated script.
        String html = template.replace("<!--GRAPH_DATA-->", graphData.toString());

        List<String> result = new ArrayList<>();
        result.add(html);
        return result;
    }
} 