/*
 * Copyright 2014-2018 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatefilenames;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.stage.Stage;

class UpdateFileNamesExecuteEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(UpdateFileNamesExecuteEventHandler.class);
    private List<UpdateFileNamesItem> items = null;

    UpdateFileNamesExecuteEventHandler(List<UpdateFileNamesItem> items) {
        this.setItems(items);
    }

    @Override
    public void handle(ActionEvent event) {

        log.debug("Executing rename of {} files", this.getItems().size());

        for (UpdateFileNamesItem item : this.getItems()) {
            TaggableFile itemFile = item.getFile();
            if (!Objects.equals(itemFile.fileNameProperty().getValue(), item.getNewFileName().getValue())) {
                itemFile.fileNameProperty().setValue(item.getNewFileName().getValue());
            }
        }

        Control sourceControl = (Control)event.getSource();
        ((Stage)sourceControl.getScene().getWindow()).close();

    }

    private List<UpdateFileNamesItem> getItems() {
        return this.items;
    }
    private void setItems(List<UpdateFileNamesItem> items) {
        this.items = items;
    }

}
