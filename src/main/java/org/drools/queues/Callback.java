package org.drools.queues;

import java.util.concurrent.atomic.AtomicInteger;

public class Callback implements Runnable {

    private static Callback CALLBACK;

    private final AtomicInteger flushedCounter;

    public Callback(AtomicInteger flushedCounter) {
        this.flushedCounter = flushedCounter;
    }

    @Override
    public void run() {
        flushedCounter.incrementAndGet();
    }

    public static synchronized Callback getCallback(final AtomicInteger flushedCounter) {
        if (CALLBACK == null) {
            CALLBACK = new Callback(flushedCounter);
        }
        return CALLBACK;
    }
}
