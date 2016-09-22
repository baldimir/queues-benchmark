package org.drools.queues;

import java.util.concurrent.atomic.AtomicInteger;

public class Callback implements Runnable {

    private final AtomicInteger flushedCounter;

    public Callback(AtomicInteger flushedCounter) {
        this.flushedCounter = flushedCounter;
    }

    @Override
    public void run() {
        flushedCounter.incrementAndGet();
    }
}
