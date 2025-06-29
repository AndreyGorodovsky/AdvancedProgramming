package graph;


public class IncAgent implements Agent {
    private final String inTopic, outTopic;

    public IncAgent(String[] subs, String[] pubs) {
        if (subs.length<1 || pubs.length<1)
            throw new IllegalArgumentException("IncAgent needs at least 1 in + out");
        this.inTopic = subs[0];
        this.outTopic= pubs[0];
        TopicManagerSingleton.get().getTopic(inTopic).subscribe(this);
        TopicManagerSingleton.get().getTopic(outTopic).addPublisher(this);
    }

    @Override
    public String getName() {
        return "IncAgent(" + inTopic + "->" + outTopic + ")";
    }

    @Override
    public void reset() { /* no internal state */ }

    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        TopicManagerSingleton.get()
                .getTopic(outTopic)
                .publish(new Message(v + 1.0));
    }

    @Override
    public void close() { /* no resources */ }

    @Override
    public String getEquation() { return inTopic + " + 1"; }
}
