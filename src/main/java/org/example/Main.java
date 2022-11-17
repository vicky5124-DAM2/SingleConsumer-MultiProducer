package org.example;

public class Main {
    public static void main(String[] args) {
        Consumer<String> consumer = new Consumer<>(new Chef());
        Producer<String> producer = new Producer<>(consumer);

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        for (int i = 0; i < 5; i++) {
            Runnable runnable = new Runnable() {
                Producer<String> producer;
                int waiterId;

                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        this.producer.send("Order number " + j + " from waiter " + this.waiterId);
                    }
                }

                public Runnable init(Producer<String> producer, int waiterId) {
                    this.producer = producer;
                    this.waiterId = waiterId;
                    return this;
                }
            }.init(producer.clone(), i);

            new Thread(runnable).start();
        }

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumer.close();
        }).start();

        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Chef implements ConsumerFunction<String> {
    @Override
    public void execute(String item) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            if (Thread.interrupted()) {
                return;
            }
        }

        System.out.println("Chef cooked: " + item);
    }
}