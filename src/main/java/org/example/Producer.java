package org.example;

public class Producer<T> implements Cloneable {
    private Consumer<T> consumer;

    public Producer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void send(T item) {
        this.consumer.send(item);
    }

    @Override
    public Producer<T> clone() {
        return new Producer(this.consumer);
    }
}