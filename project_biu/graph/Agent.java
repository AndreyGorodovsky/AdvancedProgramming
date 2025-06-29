package graph;

/**
 * Functional building-block of the reactive graph.
 * <p>
 * An {@code Agent} subscribes to one or more {@link Topic Topics}, receives messages through
 * {@link #callback(String, Message)}, performs a computation and usually publishes the result to
 * another topic.  Implementations can be pure functions (stateless) or maintain internal state.
 *
 * <h2>Life-cycle</h2>
 * <ol>
 *   <li>{@link #reset()} – called when a run is (re)started; clear internal state.</li>
 *   <li>{@link #callback(String, Message)} – invoked by the framework whenever a subscribed topic
 *       publishes a message.</li>
 *   <li>{@link #close()} – free resources (e.g. threads, files) before program exit.</li>
 * </ol>
 */
public interface Agent {

    /**
     * Identifier used in logs, debug UIs, etc.
     */
    String getName();

    /**
     * Reset internal state so the agent can be reused in another run.
     */
    void reset();

    /**
     * Called when one of the topics this agent subscribes to publishes a message.
     * @param topic topic name that triggered the callback
     * @param msg   the message
     */
    void callback(String topic, Message msg);

    /**
     * Release resources and unsubscribe.  
     * Called once before program shutdown.
     */
    void close();

    /**
     * Optional: a short textual representation of the computation this agent performs; used by
     * {@link views.HtmlGraphWriter} when rendering the graph.
     */
    default String getEquation() { return ""; }
}
