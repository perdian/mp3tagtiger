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

public class Message {

    private String title = null;
    private String headerText = null;
    private String contentText = null;
    private MessageType messageType = MessageType.ERROR;

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeaderText() {
        return this.headerText;
    }
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getContentText() {
        return this.contentText;
    }
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

}