/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.model;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;

public class SongProperties {

    private static final Logger log = LoggerFactory.getLogger(SongProperties.class);

    private Map<SongProperty, SongPropertyValue<?>> values = null;
    private BooleanProperty dirtyProperty = new SimpleBooleanProperty();

    SongProperties(AudioFile audioFile) throws IOException {
        Map<SongProperty, SongPropertyValue<?>> values = new EnumMap<>(SongProperty.class);
        log.trace("Reading tag information from file: {}", audioFile.getFile().getAbsolutePath());
        for (SongProperty songProperty : SongProperty.values()) {
            Object persistedValue = songProperty.getDelegate().readValue(audioFile);
            if (persistedValue != null && !songProperty.getType().isInstance(persistedValue)) {
                throw new IllegalArgumentException("Incompatible type for SongProperty '" + songProperty.name() + "'. Expected: " + songProperty.getClass().getName() + ", actual: " + persistedValue.getClass().getName());
            } else {
                SongPropertyValue<Object> songPropertyValue = new SongPropertyValue<>();
                songPropertyValue.getPersistedValue().setValue(songProperty.getType().cast(persistedValue));
                songPropertyValue.getValue().setValue(songProperty.getType().cast(persistedValue));
                songPropertyValue.getDirty().addListener((o, oldValue, newValue) -> this.recomputeDirty());
                values.put(songProperty, songPropertyValue);
            }
        }
        this.setValues(values);
    }

    public void resetValues() {
        for (SongProperty songProperty : SongProperty.values()) {
            this.getValues().get(songProperty).resetValue();
        }
    }

    @SuppressWarnings("unchecked")
    void resetValuesToNewValues() {
        for (SongProperty songProperty : SongProperty.values()) {
            SongPropertyValue<Object> songPropertyValue = (SongPropertyValue<Object>)this.getValues().get(songProperty);
            songPropertyValue.getPersistedValue().setValue(songPropertyValue.getValue().getValue());
        }
    }

    @SuppressWarnings("unchecked")
    boolean writeValues(AudioFile audioFile) throws IOException {
        log.trace("Writing tag information into file: {}", audioFile.getFile().getAbsolutePath());
        boolean changed = false;
        for (SongProperty songProperty : SongProperty.values()) {
            SongPropertyValue<Object> songPropertyValue = (SongPropertyValue<Object>)this.getValues().get(songProperty);
            if (songPropertyValue.getDirty().getValue()) {
                Object newValue = songPropertyValue.getValue().getValue();
                SongPropertyDelegate<Object> propertyDelegate = (SongPropertyDelegate<Object>)songProperty.getDelegate();
                propertyDelegate.writeValue(audioFile, newValue);
                changed = true;
            }
        }
        return changed;
    }

    @SuppressWarnings("unchecked")
    public <T> SongPropertyValue<T> getValue(SongProperty songProperty, Class<T> targetClass) {
        if (!targetClass.isAssignableFrom(songProperty.getType())) {
            throw new IllegalArgumentException("Incompatible type for SongProperty '" + songProperty.name() + "'. Expected: " + songProperty.getClass().getName() + ", requested: " + targetClass.getName());
        } else {
            return (SongPropertyValue<T>)this.getValues().get(songProperty);
        }
    }

    private void recomputeDirty() {
        boolean newDirty = this.getValues().values().stream()
            .map(value -> value.getDirty().getValue())
            .filter(value -> Boolean.TRUE.equals(value))
            .findAny()
            .orElse(false);

        if (newDirty != this.getDirtyProperty().getValue().booleanValue()) {
            this.getDirtyProperty().setValue(newDirty);
        }
    }

    Map<SongProperty, SongPropertyValue<?>> getValues() {
        return this.values;
    }
    private void setValues(Map<SongProperty, SongPropertyValue<?>> values) {
        this.values = values;
    }

    public ObservableBooleanValue getDirty() {
        return this.getDirtyProperty();
    }
    private BooleanProperty getDirtyProperty() {
        return this.dirtyProperty;
    }

}
