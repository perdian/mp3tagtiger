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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate.files;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import de.perdian.apps.tagtiger.core.tagging.TagImageList;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.BatchUpdateActionEventHandler;

public class CopyPropertyValueActionEventHandler<T> extends BatchUpdateActionEventHandler {

    private Function<TaggableFile, Property<T>> propertyFunction = null;
    private BiConsumer<Property<T>, T> copyValueConsumer = null;

    public CopyPropertyValueActionEventHandler(Property<TaggableFile> currentFileProperty, ObservableList<TaggableFile> otherFiles, Function<TaggableFile, Property<T>> propertyFunction, BiConsumer<Property<T>, T> copyValueConsumer) {
        super(currentFileProperty, otherFiles);
        this.setPropertyFunction(propertyFunction);
        this.setCopyValueConsumer(copyValueConsumer == null ? (property, newValue) -> property.setValue(newValue) : copyValueConsumer);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getOtherFiles().stream()
            .filter(otherFile -> !this.getCurrentFileProperty().getValue().equals(otherFile))
            .forEach(otherFile-> {

                Property<T> sourceProperty = this.getPropertyFunction().apply(this.getCurrentFileProperty().getValue());
                Property<T> targetProperty = this.getPropertyFunction().apply(otherFile);
                this.getCopyValueConsumer().accept(targetProperty, sourceProperty.getValue());

            });
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    public static class CopyImagesPropertyConsumer implements BiConsumer<Property<TagImageList>, TagImageList> {

        @Override
        public void accept(Property<TagImageList> targetProperty, TagImageList value) {
            targetProperty.getValue().updateTagImages(value.getTagImages());
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private Function<TaggableFile, Property<T>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<T>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    private BiConsumer<Property<T>, T> getCopyValueConsumer() {
        return this.copyValueConsumer;
    }
    private void setCopyValueConsumer(BiConsumer<Property<T>, T> copyValueConsumer) {
        this.copyValueConsumer = copyValueConsumer;
    }

}