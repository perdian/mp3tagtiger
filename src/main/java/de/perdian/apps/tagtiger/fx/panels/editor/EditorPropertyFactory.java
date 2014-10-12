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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.control.TextField;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionKeyEventHandler;

public class EditorPropertyFactory {

    private List<EditorProperty> properties = null;
    private Supplier<TaggableFile> fileSupplier = null;
    private Selection selection = null;

    EditorPropertyFactory(Selection selection, Supplier<TaggableFile> fileSupplier) {
        this.setSelection(selection);
        this.setProperties(new ArrayList<>());
        this.setFileSupplier(fileSupplier);
    }

    TextField createTextField(Function<TaggableFile, Property<String>> propertyFunction) {

        TextField textField = new TextField();
        textField.setOnKeyPressed(new SelectionKeyEventHandler(this.getSelection()));

        EditorProperty editorProperty = new EditorProperty();
        editorProperty.setControlSupplier(() -> textField.textProperty());
        editorProperty.setControlChangedListener((o, oldValue, newValue) -> {
            if (this.getFileSupplier().get() != null) {
                propertyFunction.apply(this.getFileSupplier().get()).setValue(newValue);
            }
        });
        editorProperty.setPropertyFunction(propertyFunction);
        editorProperty.setPropertyChangedListener((o, oldValue, newValue) -> {
            if (!Objects.equals(textField.getText(), newValue)) {
                Platform.runLater(() -> textField.setText(newValue));
            }
        });
        this.getProperties().add(editorProperty);

        return textField;

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    List<EditorProperty> getProperties() {
        return this.properties;
    }
    private void setProperties(List<EditorProperty> properties) {
        this.properties = properties;
    }

    Supplier<TaggableFile> getFileSupplier() {
        return this.fileSupplier;
    }
    private void setFileSupplier(Supplier<TaggableFile> fileSupplier) {
        this.fileSupplier = fileSupplier;
    }

}