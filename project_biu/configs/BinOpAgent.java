package configs;

import java.util.function.BinaryOperator;
import graph.*;

public class BinOpAgent implements Agent {
    private final String name;
    private final String inputTopic1;
    private final String inputTopic2;
    private final String outputTopic;
    private final BinaryOperator<Double> op;

    private double value1;
    private double value2;
    private boolean received1;
    private boolean received2;

    public BinOpAgent(
            String name,
            String inputTopic1,
            String inputTopic2,
            String outputTopic,
            BinaryOperator<Double> op
    ) {
        this.name = name;
        this.inputTopic1 = inputTopic1;
        this.inputTopic2 = inputTopic2;
        this.outputTopic = outputTopic;
        this.op = op;

        TopicManagerSingleton.get().getTopic(inputTopic1).subscribe(this);
        TopicManagerSingleton.get().getTopic(inputTopic2).subscribe(this);
        TopicManagerSingleton.get().getTopic(outputTopic).addPublisher(this);

        // Initialize state
        this.value1 = 0.0;
        this.value2 = 0.0;
        this.received1 = false;
        this.received2 = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        this.value1 = 0.0;
        this.value2 = 0.0;
        this.received1 = false;
        this.received2 = false;
    }

    @Override
    public void callback(String topic, Message msg) {
        double v = msg.getAsDouble();
        if (topic.equals(inputTopic1)) {
            value1 = v;
            received1 = true;
        } else if (topic.equals(inputTopic2)) {
            value2 = v;
            received2 = true;
        }

        if (received1 && received2) {
            double result = op.apply(value1, value2);
            TopicManagerSingleton.get()
                    .getTopic(outputTopic)
                    .publish(new Message(result));
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String getEquation() { return inputTopic1 + " ? " + inputTopic2; }
}
