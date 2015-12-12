package ecg;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputSignalStub implements Runnable {

    private final AtomicBoolean running;
    private BlockingQueue<Double> queue;
    private Random generator = new Random();
    private int counter = 0;

    InputSignalStub(BlockingQueue<Double> queue, AtomicBoolean running) {
        this.queue = queue;
        this.running = running;
    }


    @Override
    public void run() {
        while (running.get()) {
            try {
                int value = generator.nextInt(20);
                System.out.println("InputSignalStub generated value: " + String.valueOf(value) + "  - counter: " + String.valueOf(counter++));
//                Thread.sleep(value * 50);
                Thread.sleep(value);
                queue.put((double) value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
