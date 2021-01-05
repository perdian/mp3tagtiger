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
package de.perdian.apps.tagtiger3.fx.components.tools.filenames;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

class FilenamesToolItem {

    private SongFile songFile = null;
    private BooleanProperty dirty = null;
    private Map<SongAttribute, FilenamesToolItemValue> values = null;

    FilenamesToolItem(SongFile songFile) {
        BooleanProperty dirtyProperty = new SimpleBooleanProperty();
        Map<SongAttribute, FilenamesToolItemValue> values = new HashMap<>();
        for (FilenamesToolAttribute attribute : FilenamesToolAttribute.values()) {
            String attributeValue = songFile.getAttributeValueProperty(attribute.getAttribute(), String.class).getValue();
            FilenamesToolItemValue value = new FilenamesToolItemValue(attributeValue);
            value.getNewValue().addListener((o, oldValue, newValue) -> this.updateDirty(values.values()));
            values.put(attribute.getAttribute(), value);
        }
        this.setValues(values);
        this.setSongFile(songFile);
        this.setDirty(dirtyProperty);
    }

    private void updateDirty(Collection<FilenamesToolItemValue> values) {
        boolean newDirty = values.stream().filter(value -> !Objects.equals(value.getOriginalValue().getValue(), value.getNewValue().getValue())).findAny().isPresent();
        if (newDirty != this.getDirty().getValue().booleanValue()) {
            this.getDirty().setValue(newDirty);
        }
    }

    StringProperty originalValueProperty(SongAttribute attribute) {
        return this.getValues().get(attribute).getOriginalValue();
    }

    StringProperty newValueProperty(SongAttribute attribute) {
        return this.getValues().get(attribute).getNewValue();
    }

    SongFile getSongFile() {
        return this.songFile;
    }
    private void setSongFile(SongFile songFile) {
        this.songFile = songFile;
    }

    BooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

    Map<SongAttribute, FilenamesToolItemValue> getValues() {
        return this.values;
    }
    private void setValues(Map<SongAttribute, FilenamesToolItemValue> values) {
        this.values = values;
    }

}
