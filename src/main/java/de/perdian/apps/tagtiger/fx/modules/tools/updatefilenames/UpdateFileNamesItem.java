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

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

class UpdateFileNamesItem {

    private TaggableFile file = null;
    private Property<String> currentFileName = null;
    private Property<String> newFileName = null;

    UpdateFileNamesItem(TaggableFile file) {
        this.setFile(file);
        this.setCurrentFileName(file.fileNameProperty());
        this.setNewFileName(new SimpleStringProperty(file.fileNameProperty().getValue()));
    }

    Property<String> getCurrentFileName() {
        return this.currentFileName;
    }
    private void setCurrentFileName(Property<String> currentFileName) {
        this.currentFileName = currentFileName;
    }

    Property<String> getNewFileName() {
        return this.newFileName;
    }
    private void setNewFileName(Property<String> newFileName) {
        this.newFileName = newFileName;
    }

    TaggableFile getFile() {
        return this.file;
    }
    private void setFile(TaggableFile file) {
        this.file = file;
    }

}
