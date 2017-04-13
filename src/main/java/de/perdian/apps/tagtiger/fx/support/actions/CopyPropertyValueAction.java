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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.support.EditorComponentAction;
import javafx.beans.property.Property;

public class CopyPropertyValueAction<V> implements EditorComponentAction {

    private Function<TaggableFile, Property<V>> propertyFunction = null;
    private BiConsumer<Property<V>, V> copyValueConsumer = null;

    public CopyPropertyValueAction(Function<TaggableFile, Property<V>> propertyFunction) {
        this(propertyFunction, (property, newValue) -> property.setValue(newValue));
    }

    public CopyPropertyValueAction(Function<TaggableFile, Property<V>> propertyFunction, BiConsumer<Property<V>, V> copyValueConsumer) {
        this.setPropertyFunction(propertyFunction);
        this.setCopyValueConsumer(copyValueConsumer);
    }

    @Override
    public void execute(Property<TaggableFile> sourceFileProperty, List<TaggableFile> targetFiles, List<TaggableFile> allFiles) {
        TaggableFile sourceFile = sourceFileProperty.getValue();
        for (TaggableFile targetFile : targetFiles) {
            if (!Objects.equals(sourceFile, targetFile)) {

                Property<V> sourceProperty = this.getPropertyFunction().apply(sourceFile);
                V sourceValue = sourceProperty.getValue();

                Property<V> targetProperty = this.getPropertyFunction().apply(targetFile);
                if (!Objects.equals(sourceValue, targetProperty.getValue())) {
                    this.getCopyValueConsumer().accept(targetProperty, sourceValue);
                }

            }
        }
    }

    private Function<TaggableFile, Property<V>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<V>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    private BiConsumer<Property<V>, V> getCopyValueConsumer() {
        return this.copyValueConsumer;
    }
    private void setCopyValueConsumer(BiConsumer<Property<V>, V> copyValueConsumer) {
        this.copyValueConsumer = copyValueConsumer;
    }

}
