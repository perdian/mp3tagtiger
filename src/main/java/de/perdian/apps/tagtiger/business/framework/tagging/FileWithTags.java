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
package de.perdian.apps.tagtiger.business.framework.tagging;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileWithTags {

    private File file = null;
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileExtension = new SimpleStringProperty();
    private BooleanProperty changed = new SimpleBooleanProperty(false);

    FileWithTags(File file) {
        this.setFile(file);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public File getFile() {
        return this.file;
    }
    private void setFile(File file) {
        this.file = file;
    }

    public StringProperty getFileName() {
        return this.fileName;
    }

    public StringProperty getFileExtension() {
        return this.fileExtension;
    }

    public BooleanProperty getChanged() {
        return this.changed;
    }

}