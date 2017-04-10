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
package de.perdian.apps.tagtiger.fx.handlers.files;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface Action {

    void execute(Property<TaggableFile> sourceFileProperty, ObservableList<TaggableFile> targetFiles);

    default EventHandler<ActionEvent> asEventHandler(Property<TaggableFile> sourceFileProperty, ObservableList<TaggableFile> targetFiles) {
        return event -> this.execute(sourceFileProperty, targetFiles);
    }

}
