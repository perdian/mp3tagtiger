/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.actions.batchactions;

import java.util.List;

import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

class AbstractBatchActionEventHandler implements EventHandler<ActionEvent> {

    private List<SongFile> files = null;

    protected AbstractBatchActionEventHandler(List<SongFile> files) {
        this.setFiles(files);
    }

    @Override
    public void handle(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    private List<SongFile> getFiles() {
        return this.files;
    }
    private void setFiles(List<SongFile> files) {
        this.files = files;
    }

}
