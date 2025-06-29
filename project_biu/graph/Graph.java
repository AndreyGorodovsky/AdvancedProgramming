package test;

import java.util.*;

public class Graph extends ArrayList<Node> {
    public Graph() {
        super();
    }

    public void createFromTopics() {
        this.clear();
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        LinkedHashMap<String, Node> nodeMap = new LinkedHashMap<>();

        for (Topic t : topics) {
            String tKey = "T" + t.getName();
            nodeMap.put(tKey, new Node(tKey));
        }

        for (Topic t : topics) {
            for (Agent a : t.getSubscribers()) {
                String aKey = "A" + a.getName();
                nodeMap.putIfAbsent(aKey, new Node(aKey));
            }
            for (Agent a : t.getPublishers()) {
                String aKey = "A" + a.getName();
                nodeMap.putIfAbsent(aKey, new Node(aKey));
            }
        }

        for (Topic t : topics) {
            String tKey = "T" + t.getName();
            Node tNode = nodeMap.get(tKey);
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
