/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.queues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Harness {

    private static final int BENCHMARK_DURATION_IN_SECONDS = 10;

    private static final int PRODUCERS = 3;

    private static final DroolsQueue queue = new SynchronizedPropagationQueue();
    // private static final DroolsQueue queue = new MPSCQueue();

    private static final AtomicInteger insertedCounter = new AtomicInteger();
    private static final AtomicInteger flushedCounter = new AtomicInteger();

    private static final ExecutorService executor = Executors.newFixedThreadPool( PRODUCERS );

    public static void main( String[] args ) {
        Producer[] producers = new Producer[PRODUCERS];
        for (int i = 0; i < PRODUCERS; i++) {
            producers[i] = new Producer();
        }

        final long end = System.nanoTime() + ( BENCHMARK_DURATION_IN_SECONDS * 1_000_000_000L );

        for (int i = 0; i < PRODUCERS; i++) {
            executor.execute(producers[i]);
        }

        while (System.nanoTime() < end) {
            try {
                Thread.sleep( 1L );
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }
            queue.flush();
        }

        for (int i = 0; i < PRODUCERS; i++) {
            producers[i].finished = true;
        }

        executor.shutdownNow();

        System.out.println( "inserted: " + insertedCounter.get() );
        System.out.println( "flushed: " + flushedCounter.get() );
    }

    public static class Producer implements Runnable {
        volatile boolean finished = false;

        @Override
        public void run() {
            while (!finished) {
                queue.addEntry( new QueueEntry( CALLBACK ) );
                insertedCounter.incrementAndGet();
            }
        }

        private static final Callback CALLBACK = new Callback();
        private static class Callback implements Runnable {
            @Override
            public void run() {
                flushedCounter.incrementAndGet();
            }
        }
    }
}
