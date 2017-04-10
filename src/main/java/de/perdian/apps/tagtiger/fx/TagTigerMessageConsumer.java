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
package de.perdian.apps.tagtiger.fx;

import java.util.function.Consumer;

import de.perdian.apps.tagtiger.fx.messages.Message;
import de.perdian.apps.tagtiger.fx.messages.MessageType;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

class TagTigerMessageConsumer implements Consumer<Message> {

    @Override
    public void accept(Message message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(this.resolveAlertType(message.getMessageType()));
            alert.setTitle(message.getTitle());
            alert.setHeaderText(message.getHeaderText());
            alert.setContentText(message.getContentText());
            alert.showAndWait();
        });
    }

    private AlertType resolveAlertType(MessageType messageType) {
        switch (messageType) {
            default:
                return AlertType.ERROR;
        }
    }

}