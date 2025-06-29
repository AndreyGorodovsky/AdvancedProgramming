package graph;

public class DecAgent implements Agent {
    private final String inTopic, outTopic;

    public DecAgent(String[] subs, String[] pubs) {
        if (subs.length < 1 || pubs.length < 1) {
            throw new IllegalArgumentException("DecAgent needs at least 1 in + out");
        }
        this.inTopic = subs[0];
        this.outTopic = pubs[0];
        TopicManagerSingleton.get().getTopic(inTopic).subscribe(this);
        TopicManagerSingleton.get().getTopic(outTopic).addPublisher(this);
    }

    @Override
    public String getName() {
        return "DecAgent(" + inTopic + "->" + outTopic + ")";
    }

    @Override
    public void reset() { }

    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        TopicManagerSingleton.get()
                .getTopic(outTopic)
                .publish(new Message(v - 1.0));
    }

    @Override
    public void close() { }

    @Override
    public String getEquation() { return inTopic + " - 1"; }
} 