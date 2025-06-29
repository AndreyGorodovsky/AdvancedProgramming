package graph;

import java.util.Arrays;

public class MulAgent implements Agent {
    private final String[] subs, pubs;
    private double x, y;
    private boolean gotX, gotY;

    public MulAgent(String[] subs, String[] pubs) {
        this.subs = Arrays.copyOf(subs, subs.length);
        this.pubs = Arrays.copyOf(pubs, pubs.length);
        for (String in : this.subs) {
            TopicManagerSingleton.get().getTopic(in).subscribe(this);
        }
        TopicManagerSingleton.get().getTopic(this.pubs[0]).addPublisher(this);
    }

    @Override
    public String getName() {
        return "MulAgent(" + subs[0] + "," + subs[1] + "->" + pubs[0] + ")";
    }

    @Override
    public void reset() {
        x = y = 0;
        gotX = gotY = false;
    }

    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (topic.equals(subs[0])) { x = v; gotX = true; }
        else if (topic.equals(subs[1])) { y = v; gotY = true; }
        if (gotX && gotY) {
            double product = x * y;
            TopicManagerSingleton.get()
                    .getTopic(pubs[0])
                    .publish(new Message(product));
        }
    }

    @Override
    public void close() { /* no resources */ }

    @Override
    public String getEquation() { return subs[0] + " * " + subs[1]; }
} 