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
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;

public class TaggableFile {

    private File file = null;
    private AudioFile audioFile = null;
    private Map<FieldKey, StringProperty> tags = null;
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileExtension = new SimpleStringProperty();
    private BooleanProperty changed = new SimpleBooleanProperty(false);

    TaggableFile(File file, AudioFile audioFile) {
        this.setFile(file);
        this.setAudioFile(audioFile);
    }

    <T> ChangeListener<T> createUpdateChangePropertyListener() {
        return (o, oldValue, newValue) -> {
            if (oldValue == null && newValue == null) {
                // Do nothing - no change
            } else if (oldValue == newValue) {
                // Do nothing - no change
            } else if (oldValue == null || !oldValue.equals(newValue)) {
                new Thread(() -> TaggableFile.this.getChanged().set(true)).start();;
            }
        };
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
    void setFileName(StringProperty fileName) {
        this.fileName = fileName;
    }

    public StringProperty getFileExtension() {
        return this.fileExtension;
    }
    void setFileExtension(StringProperty fileExtension) {
        this.fileExtension = fileExtension;
    }

    public BooleanProperty getChanged() {
        return this.changed;
    }
    void setChanged(BooleanProperty changed) {
        this.changed = changed;
    }

    public StringProperty getTag(FieldKey fieldKey) {
        return this.getTags().get(fieldKey);
    }
    Map<FieldKey, StringProperty> getTags() {
        return this.tags;
    }
    void setTags(Map<FieldKey, StringProperty> tags) {
        this.tags = tags;
    }

    AudioFile getAudioFile() {
        return this.audioFile;
    }
    void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

}