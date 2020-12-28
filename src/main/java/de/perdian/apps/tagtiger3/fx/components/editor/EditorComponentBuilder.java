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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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

    Label createLabel(String title, double leftBorder, double rightBorder) {
        Label label = new Label(title);
        label.setPadding(new Insets(0, rightBorder, 0, leftBorder));
        return label;
    }

    TextField createNumericTextField(SongProperty property) {
        TextField textField = this.createTextField(property);
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d+")) {
                event.consume();
            }
        });
        return textField;
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

    ComboBox<String> createComboBox(SongProperty property, List<String> values) {
        StringProperty valueProperty = this.createStringProperty(property);
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.setPrefWidth(0);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setEditable(true);
        comboBox.disableProperty().bind(this.getSelectionModel().focusFileProperty().isNull());
        comboBox.focusedProperty().addListener((o, oldValue, newValue) -> Platform.runLater(() -> comboBox.getEditor().selectAll()));
        comboBox.valueProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.getEditor().textProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
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
        valueProperty.addListener((o, oldValue, newValue) -> comboBox.setValue(newValue == null ? null : newValue.toString()));
        return comboBox;
    }

    Button createCopyToOtherSongsButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.COPY));
        button.setTooltip(new Tooltip("Copy to other songs in selection"));
        button.disableProperty().bind(Bindings.size(this.getSelectionModel().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCopyPropertyValueToSelectedSongs(property));
        return button;
    }

    Button clearForSelectionButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        button.setTooltip(new Tooltip("Clear for all songs in selection"));
        button.disableProperty().bind(Bindings.size(this.getSelectionModel().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleClearForSelection(property));
        return button;
    }

    Button enumerateWithinSelectionButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.SORT_NUMERIC_ASC));
        button.setTooltip(new Tooltip("Enumerate within selection"));
        button.disableProperty().bind(Bindings.size(this.getSelectionModel().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleEnumerateSelection(property));
        return button;
    }

    Button countSelectionButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.COPY));
        button.setTooltip(new Tooltip("Count selection"));
        button.disableProperty().bind(Bindings.size(this.getSelectionModel().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCountSelection(property));
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

    private void handleClearForSelection(SongProperty property) {
        this.getSelectionModel().getSelectedFiles().forEach(file -> {
            file.getProperties().getValue(property, Object.class).getValue().setValue(null);
        });
    }

    private void handleEnumerateSelection(SongProperty property) {
        List<SongFile> files = this.getSelectionModel().getSelectedFiles();
        for (int i=0; i < files.size(); i++) {
            files.get(i).getProperties().getValue(property, String.class).getValue().setValue(String.valueOf(i + 1));
        }
    }

    private void handleCountSelection(SongProperty property) {
        this.getSelectionModel().getSelectedFiles().forEach(file -> file.getProperties().getValue(property, String.class).getValue().setValue(String.valueOf(this.getSelectionModel().getSelectedFiles().size())));
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
