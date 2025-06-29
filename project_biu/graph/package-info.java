/**
 * Reactive computation graph:
 * <ul>
 *   <li>{@link graph.Topic} – holds a value (as {@link graph.Message}) and notifies subscribed agents.</li>
 *   <li>{@link graph.Agent} – receives messages and produces new ones, pushing the computation forward.</li>
 *   <li>{@link graph.TopicManagerSingleton} – global registry + helper methods.</li>
 * </ul>
 * An agent may subscribe to one or more topics and publish to another, forming a directed graph.
 * See {@link graph.ParallelAgent} for an example of wrapping an agent with its own thread.
 */
package graph; 