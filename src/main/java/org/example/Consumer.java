package org.example;

import java.util.ArrayDeque;
import java.util.Deque;

public class Consumer<T> implements Runnable {
    private Deque<T> queue = new ArrayDeque<T>();
    private ConsumerFunction<T> consumerFunction;
    private boolean isRunning = true;

    public Consumer(ConsumerFunction<T> consumerFunction) {
        this.consumerFunction = consumerFunction;
    }

    public void send(T item) {
        synchronized (queue) {
            queue.addLast(item);
            queue.notify();
        }
    }

    public void close() {
        isRunning = false;

        synchronized (queue) {
            queue.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();

                        if (!isRunning) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        if (Thread.interrupted()) {
                            return;
                        }
                    }
                }

                T item = queue.pollFirst();
                this.consumerFunction.execute(item);
                queue.notifyAll();

                if (!isRunning) {
                    return;
                }
            }
        }
    }
}

interface ConsumerFunction<T> {
    void execute(T item);
}