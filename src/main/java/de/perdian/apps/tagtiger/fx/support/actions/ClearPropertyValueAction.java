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
package de.perdian.apps.tagtiger.fx.support.actions;

import java.util.List;
import java.util.function.Function;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.support.EditorComponentAction;
import javafx.beans.property.Property;

public class ClearPropertyValueAction<T> implements EditorComponentAction {

    private Function<TaggableFile, Property<T>> propertyFunction = null;

    public ClearPropertyValueAction(Function<TaggableFile, Property<T>> propertyFunction) {
        this.setPropertyFunction(propertyFunction);
    }

    @Override
    public void execute(Property<TaggableFile> sourceFileProperty, List<TaggableFile> targetFiles, List<TaggableFile> allFiles) {
        for (TaggableFile targetFile : targetFiles) {
            Property<T> targetFileProperty = this.getPropertyFunction().apply(targetFile);
            if (targetFileProperty.getValue() != null) {
                targetFileProperty.setValue(null);
            }
        }
    }

    private Function<TaggableFile, Property<T>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<T>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

}
