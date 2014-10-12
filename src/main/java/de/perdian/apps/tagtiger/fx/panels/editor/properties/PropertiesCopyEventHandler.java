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
package de.perdian.apps.tagtiger.fx.panels.editor.properties;

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

class PropertiesCopyEventHandler implements EventHandler<ActionEvent> {

    private Selection selection = null;
    private Function<TaggableFile, Property<String>> propertyFunction = null;
    private Property<String> sourceProperty = null;

    PropertiesCopyEventHandler(Property<String> sourceProperty, Function<TaggableFile, Property<String>> propertyFunction, Selection selection) {
        this.setSourceProperty(sourceProperty);
        this.setPropertyFunction(propertyFunction);
        this.setSelection(selection);
    }

    @Override
    public void handle(ActionEvent event) {
        List<TaggableFile> targetList = this.getSelection().getSelectedFiles();
        List<TaggableFile> useList = targetList.isEmpty() ? this.getSelection().getAvailableFiles() : targetList;
        String sourceValue = this.getSourceProperty().getValue();
        useList.forEach(file -> this.getPropertyFunction().apply(file).setValue(sourceValue));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Function<TaggableFile, Property<String>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<String>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    private Property<String> getSourceProperty() {
        return this.sourceProperty;
    }
    private void setSourceProperty(Property<String> sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

}