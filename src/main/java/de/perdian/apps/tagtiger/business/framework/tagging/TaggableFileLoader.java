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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import de.perdian.apps.tagtiger.business.framework.selection.Selection;

public class TaggableFileLoader {

    private Selection targetSelection = null;

    public TaggableFileLoader(Selection targetSelection) {
        this.setTargetSelection(targetSelection);
    }

    public TaggableFile loadFile(File file) throws Exception {
        int extensionSeparator = file.getName().lastIndexOf(".");
        TaggableFile taggableFile = new TaggableFile(file);
        taggableFile.setChanged(this.loadFileChangedProperty(taggableFile));
        taggableFile.setFileName(this.loadStringProperty(extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator), taggableFile.createUpdateChangePropertyListener()));
        taggableFile.setFileExtension(this.loadStringProperty(extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1), taggableFile.createUpdateChangePropertyListener()));
        taggableFile.setTags(this.loadFileTags(file, taggableFile.createUpdateChangePropertyListener()));
        return taggableFile;
    }

    private Map<FieldKey, StringProperty> loadFileTags(File file, ChangeListener<String> changeListener) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file);
        Tag audioTag = audioFile.getTagOrCreateDefault();
        Map<FieldKey, StringProperty> fileWrapperTags = new HashMap<>();
        for (FieldKey fieldKey : FieldKey.values()) {
            String fieldKeyValue = audioTag.getFirst(fieldKey);
            StringProperty fieldKeyProperty = new SimpleStringProperty(fieldKeyValue);
            fieldKeyProperty.addListener(changeListener);
            fileWrapperTags.put(fieldKey, fieldKeyProperty);
        }
        return fileWrapperTags;
    }

    private BooleanProperty loadFileChangedProperty(TaggableFile file) {
        BooleanProperty changedProperty = new SimpleBooleanProperty(false);
        changedProperty.addListener((o, oldValue, newValue) -> {
            Selection targetSelection = TaggableFileLoader.this.getTargetSelection();
            List<TaggableFile> targetList = targetSelection == null ? null : targetSelection.getChangedFiles();
            if (targetList != null && file != null) {
                Platform.runLater(() -> {
                    if (newValue != null && newValue.booleanValue()) {
                        targetList.add(file);
                    } else {
                        targetList.remove(file);
                    }
                });
            }
        });
        return changedProperty;
    }

    private StringProperty loadStringProperty(String value, ChangeListener<String> changeListener) {
        StringProperty property = new SimpleStringProperty(value);
        property.addListener(changeListener);
        return property;
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Selection getTargetSelection() {
        return this.targetSelection;
    }
    private void setTargetSelection(Selection targetSelection) {
        this.targetSelection = targetSelection;
    }

}