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
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class TaggableFileLoader {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public TaggableFile loadFile(File file) throws Exception {
        int extensionSeparator = file.getName().lastIndexOf(".");
        AudioFile audioFile = AudioFileIO.read(file);
        TaggableFile taggableFile = new TaggableFile(file, audioFile);
        taggableFile.setChanged(new SimpleBooleanProperty());
        taggableFile.setFileName(this.loadStringProperty(extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator), taggableFile.createUpdateChangePropertyListener()));
        taggableFile.setFileExtension(this.loadStringProperty(extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1), taggableFile.createUpdateChangePropertyListener()));
        taggableFile.setTagProperties(this.loadTagProperties(audioFile, taggableFile.createUpdateChangePropertyListener()));
        return taggableFile;
    }

    private Map<TaggableFileTag, Property<Object>> loadTagProperties(AudioFile audioFile, ChangeListener<Object> changeListener) throws Exception {
        Tag audioTag = audioFile.getTagOrCreateDefault();
        Map<TaggableFileTag, Property<Object>> fileWrapperTags = new HashMap<>();
        for (TaggableFileTag fileTag : TaggableFileTag.values()) {
            Property<Object> fileTagProperty = fileTag.getDelegate().createPropertyForTag(audioTag, fileTag.getFieldKey(), changeListener);
            fileWrapperTags.put(fileTag, fileTagProperty);
        }
        return fileWrapperTags;
    }

    private StringProperty loadStringProperty(String value, ChangeListener<String> changeListener) {
        StringProperty property = new SimpleStringProperty(value);
        property.addListener(changeListener);
        return property;
    }

}