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

import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;

public enum TaggableFileTagGroupAction {

    COPY {

        @Override
        public void apply(TaggableFile currentFile, TagHandler tag, ObservableList<TaggableFile> otherFiles) {
            Property<Object> sourceProperty = currentFile.getTagProperty(tag);
            otherFiles.stream()
                .filter(file -> !file.equals(currentFile))
                .forEach(file -> tag.getDelegate().copyPropertyValue(sourceProperty, file.getTagProperty(tag)));
        }

    },
    GENERATE_FROM_POSITION {

        @Override
        public void apply(TaggableFile currentFile, TagHandler tag, ObservableList<TaggableFile> otherFiles) {
            AtomicInteger counter = new AtomicInteger(1);
            otherFiles.stream().forEach(file -> file.getTagProperty(tag).setValue(String.valueOf(counter.getAndIncrement())));
        }

    };

    public abstract void apply(TaggableFile currentFile, TagHandler tag, ObservableList<TaggableFile> otherFiles);

}