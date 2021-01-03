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

import java.util.Objects;

import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

class ComputeFilenamesFromTagsItem {

    private Property<String> originalFilename = null;
    private Property<String> newFilename = null;
    private BooleanProperty dirty = null;

    ComputeFilenamesFromTagsItem(SongFile songFile) {
        BooleanProperty dirtyProperty = new SimpleBooleanProperty();
        Property<String> originalFilenameProperty = songFile.getProperties().getValue(SongProperty.FILENAME, String.class).getValue();
        Property<String> newFilenameProperty = new SimpleStringProperty(originalFilenameProperty.getValue());
        newFilenameProperty.addListener((o, oldValue, newValue) -> dirtyProperty.setValue(!Objects.equals(originalFilenameProperty.getValue(), newValue)));
        this.setOriginalFilename(originalFilenameProperty);
        this.setNewFilename(newFilenameProperty);
        this.setDirty(dirtyProperty);
    }

    Property<String> getOriginalFilename() {
        return this.originalFilename;
    }
    private void setOriginalFilename(Property<String> originalFilename) {
        this.originalFilename = originalFilename;
    }

    Property<String> getNewFilename() {
        return this.newFilename;
    }
    private void setNewFilename(Property<String> newFilename) {
        this.newFilename = newFilename;
    }

    BooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

}
