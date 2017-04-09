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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import java.util.function.Function;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class ClearPropertyValuesActionEventHandler extends AbstractActionEventHandler {

    private Function<TaggableFile, Property<?>> propertyFunction = null;

    public ClearPropertyValuesActionEventHandler(Property<TaggableFile> currentFileProperty, ObservableList<TaggableFile> otherFiles, Function<TaggableFile, Property<?>> propertyFunction) {
        super(currentFileProperty, otherFiles);
        this.setPropertyFunction(propertyFunction);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getOtherFiles().stream()
        .forEach(otherFile-> {
            this.getPropertyFunction().apply(otherFile).setValue(null);
        });
    }

    private Function<TaggableFile, Property<?>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<?>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

}
