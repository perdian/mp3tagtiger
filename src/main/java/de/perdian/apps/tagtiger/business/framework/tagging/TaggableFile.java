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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class TaggableFile {

    private static final Logger log = LoggerFactory.getLogger(TaggableFile.class);

    private File file = null;
    private AudioFile audioFile = null;
    private Map<TagHandler, Property<Object>> tagProperties = null;

    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty fileExtension = new SimpleStringProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public TaggableFile(File file) throws Exception {

        ChangeListener<Object> markAsDirtyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                this.dirtyProperty().setValue(true);
            }
        };

        log.trace("Reading tag information from file: {}", file.getAbsolutePath());
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTagOrCreateDefault();
        Map<TagHandler, Property<Object>> tagProperties = new HashMap<>();
        for (TagHandler tagHandler : TagHandler.values()) {
            tagProperties.put(tagHandler, tagHandler.getDelegate().createPropertyForTag(tag, tagHandler.getFieldKey(), markAsDirtyChangeListener));
        }
        this.setFile(file);
        this.setAudioFile(audioFile);
        this.setTagProperties(tagProperties);

        int extensionSeparator = file.getName().lastIndexOf(".");
        this.fileNameProperty().setValue(extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator));
        this.fileNameProperty().addListener(markAsDirtyChangeListener);
        this.fileExtensionProperty().setValue(extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1));
        this.fileExtensionProperty().addListener(markAsDirtyChangeListener);

    }

    @Override
    public String toString() {
        return this.getFile().getName();
    }

    public void writeIntoFile(File newSystemFile) throws IOException {
        try {

            AudioFile audioFile = this.getAudioFile();
            audioFile.setFile(newSystemFile);
            audioFile.setTag(this.populateTag(audioFile.getTagOrCreateAndSetDefault()));

            AudioFileIO.write(audioFile);
            this.dirtyProperty().setValue(false);

        } catch (CannotWriteException e) {
            throw new IOException("Cannot write file: " + newSystemFile.getAbsolutePath(), e);
        } catch (TagException e) {
            throw new IOException("Cannot write file: " + newSystemFile.getAbsolutePath(), e);
        }
    }

    private Tag populateTag(Tag tag) throws TagException {
        for (TagHandler tagName : TagHandler.values()) {
            Property<Object> property = this.getTagProperty(tagName);
            try {
                tagName.getDelegate().updateTagFromProperty(tag, property, tagName.getFieldKey());
            } catch(Exception e) {
                log.warn("Cannot update tag for fieldKey '" + tagName.getFieldKey() + "' with value: " + property.getValue(), e);
            }
        }
        return tag;
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

    @SuppressWarnings("unchecked")
    public <T> Property<T> getTagProperty(TagHandler tagHandler) {
        return (Property<T>)this.getTagProperties().get(tagHandler);
    }
    private Map<TagHandler, Property<Object>> getTagProperties() {
        return this.tagProperties;
    }
    private void setTagProperties(Map<TagHandler, Property<Object>> tagProperties) {
        this.tagProperties = tagProperties;
    }

    private AudioFile getAudioFile() {
        return this.audioFile;
    }
    private void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public StringProperty fileNameProperty() {
        return this.fileName;
    }
    public StringProperty fileExtensionProperty() {
        return this.fileExtension;
    }
    public BooleanProperty dirtyProperty() {
        return this.dirty;
    }

}