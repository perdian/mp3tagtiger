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

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public class SongFile {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final Logger log = LoggerFactory.getLogger(Logger.class);
    private AudioFile audioFile = null;
    private BooleanProperty focus = null;
    private Map<SongAttribute, SongAttributeValue<?>> attributeValues = null;
    private BooleanProperty dirty = null;

    @SuppressWarnings("unchecked")
    public SongFile(File osFile) throws IOException {
        try {
            AudioFile audioFile = AudioFileIO.read(Objects.requireNonNull(osFile, "File must not be null"));
            log.trace("Reading tag information from file: {}", audioFile.getFile().getAbsolutePath());

            Map<SongAttribute, SongAttributeValue<?>> attributeValues = new EnumMap<>(SongAttribute.class);
            for (SongAttribute songAttribute : SongAttribute.values()) {
                SongAttributeConverter<Object> attributeConverter = (SongAttributeConverter<Object>)songAttribute.getConverter();
                Object persistedValue = attributeConverter.readValue(audioFile);
                SongAttributeValue<Object> attributeValue = new SongAttributeValue<>(persistedValue);
                attributeValue.getDirty().addListener((o, oldValue, newValue) -> this.recomputeDirty());
                attributeValue.setClearSupplier(attributeConverter.createClearSupplier(attributeValue.getPersistedValue()));
                attributeValues.put(songAttribute, attributeValue);
            }

            this.setAudioFile(audioFile);
            this.setAttributeValues(attributeValues);
            this.setFocus(new SimpleBooleanProperty());
            this.setDirty(new SimpleBooleanProperty());
        } catch (InvalidAudioFrameException | ReadOnlyFileException | TagException | CannotReadException e) {
            throw new IOException("Invalid MP3 file: " + osFile.getAbsolutePath(), e);
        }
    }

    @Override
    public String toString() {
        return this.getAudioFile().getFile().getAbsolutePath();
    }

    @SuppressWarnings("unchecked")
    public boolean persistChanges() throws IOException {

        log.trace("Writing tag information into file: {}", this.getAudioFile().getFile().getAbsolutePath());
        boolean changed = false;
        for (SongAttribute attribute : SongAttribute.values()) {
            SongAttributeValue<Object> songAttributeValue = this.getAttributeValue(attribute, Object.class);
            if (songAttributeValue.getDirty().getValue()) {
                Object newAttributeValue = songAttributeValue.getValue().getValue();
                SongAttributeConverter<Object> attributeConverter = (SongAttributeConverter<Object>)attribute.getConverter();
                attributeConverter.writeValue(this.getAudioFile(), newAttributeValue);
                changed = true;
            }
        }

        if (changed) {
            try {
                AudioFileIO.write(this.getAudioFile());
            } catch (CannotWriteException e) {
                throw new IOException("Cannot write MP3 tags into target file: " + this.getAudioFile().getFile().getAbsolutePath(), e);
            }
            for (SongAttribute attribute : SongAttribute.values()) {
                SongAttributeValue<Object> songAttributeValue = this.getAttributeValue(attribute, Object.class);
                songAttributeValue.getPersistedValue().setValue(songAttributeValue.getValue().getValue());
            }
            return true;
        } else {
            return false;
        }

    }

    public void resetChanges() {
        for (SongAttribute attribute : SongAttribute.values()) {
            this.getAttributeValues().get(attribute).reset();
        }
    }

    private void recomputeDirty() {
        boolean newDirty = this.getAttributeValues().values().stream()
            .map(value -> value.getDirty().getValue())
            .filter(value -> Boolean.TRUE.equals(value))
            .findAny()
            .orElse(false);

        if (newDirty != this.getDirty().getValue().booleanValue()) {
            this.getDirty().setValue(newDirty);
        }
    }

    AudioFile getAudioFile() {
        return this.audioFile;
    }
    private void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public void clearAttributeValue(SongAttribute attribute) {
        this.getAttributeValue(attribute, Object.class).clear();
    }
    public void resetAttributeValue(SongAttribute attribute) {
        this.getAttributeValue(attribute, Object.class).reset();
    }
    public <T> Property<T> getAttributeValueProperty(SongAttribute attribute, Class<T> targetClass) {
        return this.getAttributeValue(attribute, targetClass).getValue();
    }
    @SuppressWarnings("unchecked")
    <T> SongAttributeValue<T> getAttributeValue(SongAttribute attribute, Class<T> valueClass) {
        SongAttributeValue<T> attributeValue = (SongAttributeValue<T>)this.getAttributeValues().get(attribute);
        if (attributeValue == null) {
            throw new IllegalArgumentException("No attribute value found for attribute: " + attribute);
        } else {
            return attributeValue;
        }
    }
    private Map<SongAttribute, SongAttributeValue<?>> getAttributeValues() {
        return this.attributeValues;
    }
    private void setAttributeValues(Map<SongAttribute, SongAttributeValue<?>> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public BooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

    public BooleanProperty getFocus() {
        return this.focus;
    }
    private void setFocus(BooleanProperty focus) {
        this.focus = focus;
    }

}
