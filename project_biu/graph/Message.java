package graph;
import java.util.Date;

/**
 * Immutable payload exchanged between topics and agents.
 * <p>
 * Internally stores three representations:
 * <ul>
 *   <li>Original textual form.</li>
 *   <li>{@code byte[]} for binary transfers.</li>
 *   <li>{@code double} parsed lazily; {@link Double#NaN} if not numeric.</li>
 * </ul>
 */
public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    /**
     * Create a message from the given text.
     */
    public Message(String text) {
        this.asText = text;
        this.data = text.getBytes();

        double temp;
        try {
            temp = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            temp = Double.NaN;
        }
        this.asDouble = temp;

        this.date = new Date();
    }

    /**
     * Convenience: construct from bytes (assumes platform default charset).
     */
    public Message(byte[] data) {
        this(new String(data));
    }

    /**
     * Convenience: construct from a double, preserving full string precision.
     */
    public Message(double d) {
        this(String.valueOf(d));
    }

    /**
     * Numeric view; may be {@link Double#NaN} if the payload is not numeric.
     */
    public double getAsDouble() {
        return asDouble;
    }

    /**
     * Return the original text.
     */
    public String getAsText() {
        return asText;
    }
}
