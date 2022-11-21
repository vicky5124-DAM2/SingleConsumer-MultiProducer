package org.example;

public class MultiProducerSingleConsumer<T> {
    private Consumer<T> consumer;
    private Producer<T> producer;
    private Thread consumerThread;

    public MultiProducerSingleConsumer(ConsumerFunction<T> consumerFunction) {
        this.consumer = new Consumer<>(consumerFunction);
        this.producer = new Producer<>(this.consumer);

        this.consumerThread = new Thread(this.consumer);
        consumerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.consumer.close();
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public Producer<T> getProducer() {
        return this.producer.clone();
    }

    public void close() {
        this.consumer.close();
    }

    public void join() {
        try {
            this.consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}