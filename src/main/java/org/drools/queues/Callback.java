package org.drools.queues;

import java.util.concurrent.atomic.LongAdder;

public class Callback implements Runnable {

    private final LongAdder flushedCounter;

    public Callback(LongAdder flushedCounter) {
        this.flushedCounter = flushedCounter;
    }

    @Override
    public void run() {
        flushedCounter.add(1);
    }
}
