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

import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpscArrayQueue;

public class MPSCQueue implements DroolsQueue {

    // private final MessagePassingQueue<QueueEntry> delegate = new MpscLinkedQueue8<QueueEntry>();
    private final MessagePassingQueue<QueueEntry> delegate = new MpscArrayQueue<QueueEntry>(200_000_000);

    public void addEntry( QueueEntry entry ) {
        delegate.offer( entry );
    }

    public void flush() {
        for (QueueEntry entry = delegate.poll(); entry != null; entry = entry.getNext()) {
            entry.execute();
        }
    }
}
