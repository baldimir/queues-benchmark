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

public class MPSCQueueWithFlush implements DroolsQueue {

    private final MpscArrayQueueWithFlush<QueueEntry> delegate = new MpscArrayQueueWithFlush<QueueEntry>( 200_000_000);

    public void addEntry( QueueEntry entry ) {
        delegate.offer( entry );
    }

    public void flush() {
        delegate.relaxedFlush( QueueEntry::execute );
    }
}