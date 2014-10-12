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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.scene.control.TextField;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;

public class EditorPropertyFactory {

    private List<EditorProperty> properties = null;
    private Supplier<FileWithTags> fileSupplier = null;

    EditorPropertyFactory(Supplier<FileWithTags> fileSupplier) {
        this.setProperties(new ArrayList<>());
        this.setFileSupplier(fileSupplier);
    }

    TextField createTextField(Function<FileWithTags, Property<String>> propertyFunction) {

        TextField textField = new TextField();

        EditorProperty editorProperty = new EditorProperty();
        editorProperty.setControlSupplier(() -> textField.textProperty());
        editorProperty.setControlChangedListener((o, oldValue, newValue) -> Optional.ofNullable(this.getFileSupplier().get()).ifPresent(file -> propertyFunction.apply(file).setValue(newValue)));
        editorProperty.setPropertyFunction(propertyFunction);
        editorProperty.setPropertyChangedListener((o, oldValue, newValue) -> textField.setText(newValue));
        this.getProperties().add(editorProperty);

        return textField;

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    List<EditorProperty> getProperties() {
        return this.properties;
    }
    void setProperties(List<EditorProperty> properties) {
        this.properties = properties;
    }

    Supplier<FileWithTags> getFileSupplier() {
        return this.fileSupplier;
    }
    void setFileSupplier(Supplier<FileWithTags> fileSupplier) {
        this.fileSupplier = fileSupplier;
    }

}