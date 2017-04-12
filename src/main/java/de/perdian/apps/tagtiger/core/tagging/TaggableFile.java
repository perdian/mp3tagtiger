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
package de.perdian.apps.tagtiger.core.tagging;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

public class TaggableFile {

    private static final Logger log = LoggerFactory.getLogger(TaggableFile.class);
    private File systemFile = null;
    private AudioFile audioFile = null;
    private Property<Boolean> dirty = new SimpleBooleanProperty();
    private Property<Boolean> active = new SimpleBooleanProperty();
    private Map<TaggableProperty, Property<?>> properties = new EnumMap<>(TaggableProperty.class);
    private Property<String> fileName = new SimpleStringProperty();
    private Property<String> fileExtension = new SimpleStringProperty();

    @SuppressWarnings("unchecked")
    public TaggableFile(File systemFile) throws IOException {
        try {

            log.trace("Reading tag information from file: {}", systemFile.getAbsolutePath());
            AudioFile audioFile = AudioFileIO.read(systemFile);
            Tag audioFileTag = audioFile.getTagOrCreateDefault();

            ChangeListener<Object> changeListener = (o, oldValue, newValue) -> {
                if (!Objects.equals(oldValue, newValue)) {
                    this.dirtyProperty().setValue(Boolean.TRUE);
                }
            };

            for (TaggableProperty taggableProperty : TaggableProperty.values()) {
                TaggablePropertyAccessor<Object> taggablePropertyAccessor = (TaggablePropertyAccessor<Object>)taggableProperty.getPropertyAccessor();
                Property<Object> taggablePropertyValue = new SimpleObjectProperty<>();
                taggablePropertyValue.setValue(taggablePropertyAccessor.readTagValue(audioFileTag));
                taggablePropertyAccessor.installChangeListener(taggablePropertyValue, changeListener);
                this.getProperties().put(taggableProperty, taggablePropertyValue);
            }

            this.setSystemFile(systemFile);
            this.setAudioFile(audioFile);

            // Finally we extract the file name and extension information from the
            // file so that the user may change this as well
            int extensionSeparator = systemFile.getName().lastIndexOf(".");
            this.fileNameProperty().setValue(extensionSeparator < 0 ? systemFile.getName() : systemFile.getName().substring(0, extensionSeparator));
            this.fileNameProperty().addListener(changeListener);
            this.fileExtensionProperty().setValue(extensionSeparator < 0 || extensionSeparator >= systemFile.getName().length() - 1 ? null : systemFile.getName().substring(extensionSeparator + 1));
            this.fileExtensionProperty().addListener(changeListener);

        } catch (Exception e) {
            throw new IOException("Cannot read MP3 content from file at: " + systemFile.getAbsolutePath(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public void writeIntoFile() throws IOException {

        StringBuilder newFileName = new StringBuilder();
        newFileName.append(this.fileNameProperty().getValue());
        newFileName.append(".").append(this.fileExtensionProperty().getValue());

        AudioFile audioFile = this.getAudioFile();
        Tag targetTag = audioFile.getTagOrCreateAndSetDefault();
        for (TaggableProperty taggableProperty : TaggableProperty.values()) {
            TaggablePropertyAccessor<Object> taggablePropertyAccessor = (TaggablePropertyAccessor<Object>)taggableProperty.getPropertyAccessor();
            Property<?> taggablePropertyValue = this.getProperties().get(taggableProperty);
            taggablePropertyAccessor.writeValue(targetTag, taggablePropertyValue.getValue());
        }

        audioFile.setTag(targetTag);
        try {
            AudioFileIO.write(audioFile);
        } catch (Exception e) {
            throw new IOException("Cannot write MP3 tag into file at: " + audioFile.getFile().getAbsolutePath(), e);
        }

        File currentSystemFile = this.getSystemFile().getCanonicalFile();
        long originalTimestamp = currentSystemFile.lastModified();
        File newSystemFile = new File(currentSystemFile.getParentFile(), newFileName.toString());
        if (!newSystemFile.equals(currentSystemFile)) {
            currentSystemFile.renameTo(newSystemFile);
        }
        newSystemFile.setLastModified(originalTimestamp);
        audioFile.setFile(newSystemFile);
        this.dirtyProperty().setValue(false);

    }

    @Override
    public String toString() {
        return this.getSystemFile().getName();
    }

    public String toExtendedString() {
        return this.getSystemFile().getAbsolutePath();
    }

    File getSystemFile() {
        return this.systemFile;
    }
    void setSystemFile(File systemFile) {
        this.systemFile = systemFile;
    }

    AudioFile getAudioFile() {
        return this.audioFile;
    }
    void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public Property<?> property(TaggableProperty property) {
        return this.getProperties().get(property);
    }
    Map<TaggableProperty, Property<?>> getProperties() {
        return this.properties;
    }
    void setProperties(Map<TaggableProperty, Property<?>> properties) {
        this.properties = properties;
    }

    public Property<Boolean> activeProperty() {
        return this.active;
    }
    public Property<Boolean> dirtyProperty() {
        return this.dirty;
    }
    public Property<String> fileNameProperty() {
        return this.fileName;
    }
    public Property<String> fileExtensionProperty() {
        return this.fileExtension;
    }

}
