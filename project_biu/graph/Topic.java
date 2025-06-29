package graph;
import java.util.ArrayList;
import java.util.List;

/**
 * A node in the reactive graph that holds a value and notifies subscribed {@link Agent agents} when
 * a new {@link Message} is published.
 * <p>
 * A topic keeps track of two disjoint sets:
 * <ul>
 *   <li>Subscribers – agents that listen to this topic.</li>
 *   <li>Publishers   – agents allowed to publish to this topic (not strictly enforced; documented).</li>
 * </ul>
 * Methods are <em>not</em> thread-safe; concurrent access must be externally synchronised or made
 * through higher-level helpers such as {@link TopicManagerSingleton}.
 */
public class Topic {
    public final String name;
    private final List<Agent> subs;
    private final List<Agent> pubs;
    private Message lastMessage;

    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = null;
    }

    public String getName() {
        return name;
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    /**
     * Broadcast the given message to all subscribers and remember it as the latest value.
     */
    public void publish(Message msg) {
        for (Agent subscriber : subs) {
            subscriber.callback(name, msg);
        }
        this.lastMessage=msg;
    }

    public List<Agent> getSubscribers() {
        return subs;
    }

    public List<Agent> getPublishers() {
        return pubs;
    }

    /**
     * Last message published on this topic (or {@code null} if none yet).
     */
    public Message getLastMessage() {
        return lastMessage;
    }
}
