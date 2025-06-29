package configs;

import java.util.*;
import graph.*;

public class Graph extends ArrayList<Node> {
    public Graph() {
        super();
    }

    public void createFromTopics() {
        /*
         * Build a display-only graph from the runtime topics & agents that live inside TopicManagerSingleton.
         * Convert every topic into a Node and every agent into a Node. We keep a LinkedHashMap to preserve
         * insertion order so the visual layout is stable.
         * Record the agents equation and the topics last published message.
         * Create directed edges: publisher -> topic -> subscriber.
         */
        this.clear();
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        LinkedHashMap<String, Node> nodeMap = new LinkedHashMap<>();

        // Map to store agent display names
        Map<String, String> agentDisplayNames = new HashMap<>();

        // Add topic nodes with displayName as topic name
        for (Topic t : topics) {
            String tKey = "T" + t.getName();
            nodeMap.put(tKey, new Node(tKey, t.getName()));
        }

        // Add agent nodes with displayName as simple class nam
        for (Topic t : topics) {
            for (Agent a : t.getSubscribers()) {
                String aKey = "A" + a.getName();
                String displayName = a.getName();
                // Try to extract simple class name
                int idx = displayName.indexOf('(');
                if (idx > 0) displayName = displayName.substring(0, idx);
                nodeMap.putIfAbsent(aKey, new Node(aKey, displayName));
                nodeMap.get(aKey).setEquation(a.getEquation());
            }
            for (Agent a : t.getPublishers()) {
                String aKey = "A" + a.getName();
                String displayName = a.getName();
                int idx = displayName.indexOf('(');
                if (idx > 0) displayName = displayName.substring(0, idx);
                nodeMap.putIfAbsent(aKey, new Node(aKey, displayName));
                nodeMap.get(aKey).setEquation(a.getEquation());
            }
        }

        for (Topic t : topics) {
            String tKey = "T" + t.getName();
            Node tNode = nodeMap.get(tKey);
            if (tNode != null) {
                tNode.setMsg(t.getLastMessage());
            }
            for (Agent a : t.getSubscribers()) {
                String aKey = "A" + a.getName();
                tNode.addEdge(nodeMap.get(aKey));
            }
            for (Agent a : t.getPublishers()) {
                String aKey = "A" + a.getName();
                nodeMap.get(aKey).addEdge(tNode);
            }
        }

        this.addAll(nodeMap.values());
    }

    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) {
                return true;
            }
        }
        return false;
    }
}
