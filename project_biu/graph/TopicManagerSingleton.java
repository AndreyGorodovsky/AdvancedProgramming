package graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * Thread-safe global registry of topics.  Implemented as a "singleton via static inner class" to
 * guarantee lazy initialisation without explicit locks.
 * <p>
 * Usage:
 * <pre>{@code
 * Topic t = TopicManagerSingleton.get().getTopic("T1");
 * t.publish(new Message("42"));
 * }</pre>
 */
public class TopicManagerSingleton {
    private TopicManagerSingleton() { }

    /**
     * Access the singleton instance.
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }

    /**
     * Actual implementation holder; exposed methods delegate here.
     */
    public static class TopicManager {
        private static final TopicManager instance = new TopicManager();
        private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

        private TopicManager() { }

        /**
         * Get an existing topic or create a new one on-the-fly.
         */
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }

        /**
         * Immutable snapshot of all current topics.
         */
        public Collection<Topic> getTopics() {
            return Collections.unmodifiableCollection(topics.values());
        }

        /**
         * Remove all topics
         */
        public void clear() {
            topics.clear();
        }
    }
}
