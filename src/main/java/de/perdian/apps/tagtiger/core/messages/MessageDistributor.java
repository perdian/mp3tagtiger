/*
 * Copyright 2014 Christian Robert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.tagtiger.core.messages;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Sends messages through the system
 *
 * @author Christian Robert
 */

public class MessageDistributor {

    private List<Consumer<Message>> consumers = new CopyOnWriteArrayList<>();

    public MessageDistributor(Collection<Consumer<Message>> consumers) {
        this.getConsumers().addAll(consumers);
    }

    public void distributeMessage(Message message) {
        this.getConsumers().forEach(consumer -> consumer.accept(message));
    }

    public void addConsumer(Consumer<Message> consumer) {
        this.getConsumers().add(consumer);
    }
    List<Consumer<Message>> getConsumers() {
        return this.consumers;
    }
    void setConsumers(List<Consumer<Message>> consumers) {
        this.consumers = consumers;
    }

}