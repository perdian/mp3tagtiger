/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.editor;

import java.util.Objects;

import de.perdian.apps.tagtiger3.fx.components.selection.SelectionModel;
import de.perdian.apps.tagtiger3.model.SongProperty;
import de.perdian.commons.fx.components.ComponentBuilder;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class EditorComponentBuilder {

    private ComponentBuilder componentBuilder = null;
    private SelectionModel selectionModel = null;

    public EditorComponentBuilder(SelectionModel selectionModel) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.addListener(component -> component.setOnKeyPressed(event -> this.handleOnKeyPressedEvent(event, selectionModel)));
        this.setComponentBuilder(componentBuilder);
        this.setSelectionModel(selectionModel);
    }

    private void handleOnKeyPressedEvent(KeyEvent event, SelectionModel selectionModel) {
        if (!selectionModel.getAvailableFiles().isEmpty()) {
            int currentIndex = selectionModel.getAvailableFiles().indexOf(selectionModel.focusFileProperty().getValue());
            if (event.getCode() == KeyCode.PAGE_UP && currentIndex > 0) {
                selectionModel.focusFileProperty().setValue(selectionModel.getAvailableFiles().get(currentIndex - 1));
            } else if (event.getCode() == KeyCode.PAGE_DOWN && currentIndex < selectionModel.getAvailableFiles().size() - 1) {
                selectionModel.focusFileProperty().setValue(selectionModel.getAvailableFiles().get(currentIndex + 1));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.HOME) {
                selectionModel.focusFileProperty().setValue(selectionModel.getAvailableFiles().get(0));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.END) {
                selectionModel.focusFileProperty().setValue(selectionModel.getAvailableFiles().get(selectionModel.getAvailableFiles().size() - 1));
            }
        }
    }

    Label createLabel(String title) {
        Label label = new Label(title);
        label.setPadding(new Insets(0, 5, 0, 0));
        return label;
    }

    TextField createTextField(SongProperty property) {
        StringProperty textFieldProperty = this.createStringProperty(property);
        TextField textField = this.getComponentBuilder().createTextField(textFieldProperty).get();
        textField.disableProperty().bind(this.getSelectionModel().focusFileProperty().isNull());
        textFieldProperty.addListener((o, oldValue, newValue) -> {
            if (textField.focusedProperty().get()) {
                textField.selectAll();
            }
        });
        return textField;
    }

    private StringProperty createStringProperty(SongProperty property) {
       StringProperty stringProperty = new SimpleStringProperty();
       this.getSelectionModel().focusFileProperty().addListener((o, oldFocusFile, newFocusFile) -> {
           if (newFocusFile != null) {
               stringProperty.setValue(newFocusFile.getProperties().getValue(property, String.class).getValue().getValue());
           } else {
               stringProperty.setValue("");
           }
       });
       stringProperty.addListener((o, oldValue, newValue) -> {
           if (this.getSelectionModel().focusFileProperty().getValue() != null) {
               Property<String> focusFileTargetProperty = this.getSelectionModel().focusFileProperty().getValue().getProperties().getValue(property, String.class).getValue();
               if (!Objects.equals(newValue, focusFileTargetProperty.getValue())) {
                   focusFileTargetProperty.setValue(newValue);
               }
           }
       });
       return stringProperty;
    }

    private ComponentBuilder getComponentBuilder() {
        return this.componentBuilder;
    }
    private void setComponentBuilder(ComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
    }

    private SelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    private void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

}
