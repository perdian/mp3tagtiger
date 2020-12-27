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

import java.util.List;
import java.util.Objects;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.fx.components.selection.SelectionModel;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongProperty;
import de.perdian.commons.fx.components.ComponentBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class EditorComponentBuilder {

    private ComponentBuilder componentBuilder = null;
    private SelectionModel selectionModel = null;

    public EditorComponentBuilder(SelectionModel selectionModel) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.addListener(component -> component.setOnKeyPressed(event -> this.handleOnKeyPressedEvent(event)));
        this.setComponentBuilder(componentBuilder);
        this.setSelectionModel(selectionModel);
    }

    Label createLabel(String title, double minWidth) {
        Label label = new Label(title);
        label.setMinWidth(minWidth);
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
        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE && event.isMetaDown()) {
                List<SongProperty> resetProperties = event.isShiftDown() ? List.of(SongProperty.values()) : List.of(property);
                SongFile focusFile = this.getSelectionModel().focusFileProperty().getValue();
                if (focusFile != null) {
                    for (SongProperty resetProperty : resetProperties) {
                        focusFile.getProperties().getValue(resetProperty, Object.class).resetValue();
                    }
                }
            } else if (event.getCode() == KeyCode.ENTER && event.isMetaDown()) {
                this.handleCopyPropertyValueToSelectedSongs(property);
            }
        });
        return textField;
    }

    Button createCopyToOtherSongsButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.COPY));
        button.disableProperty().bind(Bindings.size(this.getSelectionModel().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCopyPropertyValueToSelectedSongs(property));
        return button;
    }

    private StringProperty createStringProperty(SongProperty property) {
        StringProperty stringProperty = new SimpleStringProperty();
        ChangeListener<String> updateStringPropertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(stringProperty.getValue(), newValue)) {
                stringProperty.setValue(newValue);
            }
        };
        this.getSelectionModel().focusFileProperty().addListener((o, oldFocusFile, newFocusFile) -> {
            if (newFocusFile != null) {
                Property<String> newFocusFileProperty = newFocusFile.getProperties().getValue(property, String.class).getValue();
                stringProperty.setValue(newFocusFileProperty.getValue());
                newFocusFileProperty.addListener(updateStringPropertyChangeListener);
            } else {
                stringProperty.setValue("");
            }
            if (oldFocusFile != null) {
                Property<String> oldFocusFileProperty = oldFocusFile.getProperties().getValue(property, String.class).getValue();
                oldFocusFileProperty.removeListener(updateStringPropertyChangeListener);
            }
        });
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && this.getSelectionModel().focusFileProperty().getValue() != null) {
                Property<String> focusFileTargetProperty = this.getSelectionModel().focusFileProperty().getValue().getProperties().getValue(property, String.class).getValue();
                if (!Objects.equals(newValue, focusFileTargetProperty.getValue())) {
                    focusFileTargetProperty.setValue(newValue);
                }
            }
        });
        return stringProperty;
    }

    private void handleOnKeyPressedEvent(KeyEvent event) {
        if (!this.getSelectionModel().getAvailableFiles().isEmpty()) {
            int currentIndex = this.getSelectionModel().getAvailableFiles().indexOf(this.getSelectionModel().focusFileProperty().getValue());
            if (event.getCode() == KeyCode.PAGE_UP && currentIndex > 0) {
                this.getSelectionModel().focusFileProperty().setValue(this.getSelectionModel().getAvailableFiles().get(currentIndex - 1));
            } else if (event.getCode() == KeyCode.PAGE_DOWN && currentIndex < this.getSelectionModel().getAvailableFiles().size() - 1) {
                this.getSelectionModel().focusFileProperty().setValue(this.getSelectionModel().getAvailableFiles().get(currentIndex + 1));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.HOME) {
                this.getSelectionModel().focusFileProperty().setValue(this.getSelectionModel().getAvailableFiles().get(0));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.END) {
                this.getSelectionModel().focusFileProperty().setValue(this.getSelectionModel().getAvailableFiles().get(this.getSelectionModel().getAvailableFiles().size() - 1));
            }
        }
    }

    private void handleCopyPropertyValueToSelectedSongs(SongProperty property) {
        SongFile focusFile = this.getSelectionModel().focusFileProperty().getValue();
        if (focusFile != null) {
            Object focusFileValue = focusFile.getProperties().getValue(property, Object.class).getValue().getValue();
            this.getSelectionModel().getSelectedFiles().forEach(selectedFile -> {
                selectedFile.getProperties().getValue(property, Object.class).getValue().setValue(focusFileValue);
            });
        }
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
