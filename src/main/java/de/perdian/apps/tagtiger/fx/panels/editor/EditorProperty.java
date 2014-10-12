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
package de.perdian.apps.tagtiger.fx.panels.editor;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;

class EditorProperty {

    private Function<FileWithTags, Property<String>> propertyFunction = null;
    private Supplier<Property<String>> controlSupplier = null;
    private ChangeListener<String> propertyChangedListener = null;
    private ChangeListener<String> controlChangedListener = null;

    void removeListeners(FileWithTags file) {
        this.getPropertyFunction().apply(file).removeListener(this.getPropertyChangedListener());
        this.getControlSupplier().get().removeListener(this.getControlChangedListener());
    }

    void addListeners(FileWithTags file) {
        this.getPropertyFunction().apply(file).addListener(this.getPropertyChangedListener());
        this.getControlSupplier().get().addListener(this.getControlChangedListener());
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Function<FileWithTags, Property<String>> getPropertyFunction() {
        return this.propertyFunction;
    }
    void setPropertyFunction(Function<FileWithTags, Property<String>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    ChangeListener<String> getPropertyChangedListener() {
        return this.propertyChangedListener;
    }
    void setPropertyChangedListener(ChangeListener<String> propertyChangedListener) {
        this.propertyChangedListener = propertyChangedListener;
    }

    ChangeListener<String> getControlChangedListener() {
        return this.controlChangedListener;
    }
    void setControlChangedListener(ChangeListener<String> controlChangedListener) {
        this.controlChangedListener = controlChangedListener;
    }

    Supplier<Property<String>> getControlSupplier() {
        return this.controlSupplier;
    }
    void setControlSupplier(Supplier<Property<String>> controlSupplier) {
        this.controlSupplier = controlSupplier;
    }

}