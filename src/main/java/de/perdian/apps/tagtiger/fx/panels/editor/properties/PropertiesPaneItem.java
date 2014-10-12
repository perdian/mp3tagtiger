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

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

class PropertiesPaneItem {

    private int colspan = 1;
    private Double width = null;
    private String title = null;
    private Function<TaggableFile, Property<String>> propertyFunction = null;
    private Supplier<Control> controlFactory = null;
    private Function<Control, Property<String>> controlTextPropertyFunction = null;
    private boolean addCopyToAll = true;

    PropertiesPaneItem(int colspan, Double width, String title, Function<TaggableFile, Property<String>> propertyFunction, Supplier<Control> controlFactory, Function<Control, Property<String>> controlTextPropertyFunction, boolean addCopyToAll) {
        this.setColspan(colspan);
        this.setWidth(width);
        this.setTitle(title);
        this.setPropertyFunction(propertyFunction);
        this.setControlFactory(controlFactory);
        this.setControlTextPropertyFunction(controlTextPropertyFunction);
        this.setAddCopyToAll(addCopyToAll);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    int getColspan() {
        return this.colspan;
    }
    private void setColspan(int colspan) {
        this.colspan = colspan;
    }

    Double getWidth() {
        return this.width;
    }
    private void setWidth(Double width) {
        this.width = width;
    }

    String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    Supplier<Control> getControlFactory() {
        return this.controlFactory;
    }
    private void setControlFactory(Supplier<Control> controlFactory) {
        this.controlFactory = controlFactory;
    }

    boolean isAddCopyToAll() {
        return this.addCopyToAll;
    }
    private void setAddCopyToAll(boolean addCopyToAll) {
        this.addCopyToAll = addCopyToAll;
    }

    Function<TaggableFile, Property<String>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<String>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    Function<Control, Property<String>> getControlTextPropertyFunction() {
        return this.controlTextPropertyFunction;
    }
    private void setControlTextPropertyFunction(Function<Control, Property<String>> controlTextPropertyFunction) {
        this.controlTextPropertyFunction = controlTextPropertyFunction;
    }

}