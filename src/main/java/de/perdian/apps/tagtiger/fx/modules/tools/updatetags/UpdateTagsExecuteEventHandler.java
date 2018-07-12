/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.stage.Stage;

class UpdateTagsExecuteEventHandler implements EventHandler<ActionEvent> {

    private List<UpdateTagsPreviewItem> previewItems = null;

    UpdateTagsExecuteEventHandler(List<UpdateTagsPreviewItem> previewItems) {
        this.setPreviewItems(previewItems);
    }

    @Override
    public void handle(ActionEvent event) {

        this.getPreviewItems().forEach(item -> item.copyValuesToSourceFile());

        Control sourceControl = (Control)event.getSource();
        ((Stage)sourceControl.getScene().getWindow()).close();

    }

    private List<UpdateTagsPreviewItem> getPreviewItems() {
        return this.previewItems;
    }
    private void setPreviewItems(List<UpdateTagsPreviewItem> previewItems) {
        this.previewItems = previewItems;
    }

}
