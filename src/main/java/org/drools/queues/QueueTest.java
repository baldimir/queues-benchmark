package org.drools.queues;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 20, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Group)
@Threads(4)
public class QueueTest {

    @Param({"SYNC_PROPAGATION_QUEUE", "MPSC_QUEUE", "MPSC_QUEUE_WITH_FLUSH"})
    private QueueType queueType;

    private DroolsQueue queue;

    private Callback callback;
    private AtomicInteger flushedCounter;

    @Setup(Level.Iteration)
    public void setup() {
        flushedCounter = new AtomicInteger(0);
        callback = Callback.getCallback(flushedCounter);

        queue = createQueueInstance(queueType);
    }

    @Benchmark
    @Group("queueBenchmark")
    @GroupThreads(1)
    public void flushQueue() {
        try {
            Thread.sleep(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        queue.flush();
    }

    @Benchmark
    @Group("queueBenchmark")
    @GroupThreads(3)
    public void produceEvents() {
        queue.addEntry(new QueueEntry(callback));
    }

    private DroolsQueue createQueueInstance(final QueueType queueType) {
        switch (queueType) {
            case SYNC_PROPAGATION_QUEUE:
                return new SynchronizedPropagationQueue();
            case MPSC_QUEUE:
                return new MPSCQueue();
            case MPSC_QUEUE_WITH_FLUSH:
                return new MPSCQueueWithFlush();
            default:
                throw new IllegalArgumentException("Unsupported queue type: " + queueType + "!");
        }
    }
}
