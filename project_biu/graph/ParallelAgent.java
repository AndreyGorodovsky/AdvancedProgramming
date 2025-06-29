package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent{
    private final Agent delegate;
    private final BlockingQueue<Task> queue;
    public final Thread worker;
    /** Sentinel task that signals the worker thread to terminate. */
    private static final Task POISON = new Task(null, null);


    public ParallelAgent (Agent delegate, int capacity) {
        this.delegate = delegate;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.worker = new Thread(this::processQueue);
        this.worker.start();
    }

    private void processQueue() {
        try {
            while (true) {
                Task task = queue.take();
                if (task == POISON) break;
                delegate.callback(task.topic, task.msg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final class Task {
        final String topic;
        final Message msg;

        Task(String topic, Message msg) {
            this.topic = topic;
            this.msg = msg;
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    /**
     * Shut down this agent: stop the worker thread and close the delegate.
     */
    public void close() {
        try {
            queue.put(POISON);
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        delegate.close();
    }

    @Override
    /**
     * Queue the message for asynchronous processing by the worker thread.
     */
    public void callback(String topic, Message msg) {
        try {
            queue.put(new Task(topic, msg));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String getEquation() { return delegate.getEquation(); }
}
