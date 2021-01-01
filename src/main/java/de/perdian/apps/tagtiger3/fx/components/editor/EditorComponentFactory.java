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

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.fx.model.Selection;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongImages;
import de.perdian.apps.tagtiger3.model.SongProperty;
import de.perdian.commons.fx.components.ComponentBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class EditorComponentFactory {

    private ComponentBuilder componentBuilder = null;
    private Selection selection = null;

    EditorComponentFactory(Selection selection) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        this.setComponentBuilder(componentBuilder);
        this.setSelection(selection);
    }

    Label createOuterLabel(String title) {
        Label label = new Label(title);
        label.setPadding(new Insets(0, 5, 0, 0));
        return label;
    }

    Label createInnerLabel(String title) {
        Label label = new Label(title);
        label.setPadding(new Insets(0, 5, 0, 10));
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
        StringProperty textFieldProperty = this.bindSongProperty(property, new SimpleStringProperty());
        TextField textField = this.getComponentBuilder().createTextField(textFieldProperty).get();
        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.processKeyPressedEvent(event, property));
        textField.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        textFieldProperty.addListener((o, oldValue, newValue) -> {
            if (textField.focusedProperty().getValue()) {
                textField.selectAll();
            }
        });
        return textField;
    }

    ComboBox<String> createComboBox(SongProperty property, List<String> values) {
        StringProperty valueProperty = this.bindSongProperty(property, new SimpleStringProperty());
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.setPrefWidth(0);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setEditable(true);
        comboBox.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        comboBox.focusedProperty().addListener((o, oldValue, newValue) -> Platform.runLater(() -> comboBox.getEditor().selectAll()));
        comboBox.valueProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.getEditor().textProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.processKeyPressedEvent(event, property));
        valueProperty.addListener((o, oldValue, newValue) -> comboBox.setValue(newValue == null ? null : newValue.toString()));
        return comboBox;
    }

    Label createImagesLabel() {
        Label imagesLabel = new Label("No image");
        imagesLabel.setAlignment(Pos.TOP_CENTER);
        imagesLabel.setMaxWidth(Double.MAX_VALUE);
        imagesLabel.setMinWidth(190);
        imagesLabel.setMaxWidth(190);
        ObjectProperty<SongImages> imagesProperty = this.bindSongProperty(SongProperty.IMAGES, new SimpleObjectProperty<>());
        imagesProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null || newValue.getImages().isEmpty()) {
                imagesLabel.setGraphic(null);
                imagesLabel.setText(this.getSelection().focusFileProperty().getValue() != null ? "No image" : "");
            } else {
                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(newValue.getImages().get(0).getBytes()), 180, 180, true, true));
                imageView.setPreserveRatio(true);
                imageView.fitWidthProperty().bind(imagesLabel.widthProperty());
                imagesLabel.setText(null);
                imagesLabel.setGraphic(imageView);
            }
        });
        return imagesLabel;
    }

    Button createCopyPropertyValueToSelectedSongsButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.COPY));
        button.setTooltip(new Tooltip("Copy to other songs in selection"));
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCopyPropertyValueToSelectedSongs(property));
        return button;
    }

    Button createClearPropertyForSelectedSongsButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        button.setTooltip(new Tooltip("Clear for all songs in selection"));
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleClearPropertyForSelectedSongs(property));
        return button;
    }

    Button createEnumeratePropertyForSelectedSongsButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.SORT_NUMERIC_ASC));
        button.setTooltip(new Tooltip("Enumerate within selection"));
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleEnumeratePropertyForSelectedSongs(property));
        return button;
    }

    Button createCountSelectedSongsIntoPropertyButton(SongProperty property) {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.COPY));
        button.setTooltip(new Tooltip("Count selection"));
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCountSelectedSongsIntoProperty(property));
        return button;
    }

    Button createAddImageButton() {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        button.setTooltip(new Tooltip("Add/replace image"));
        button.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        button.setOnAction(event -> this.handleAddImage());
        return button;
    }

    Button createClearImageButton() {
        BooleanProperty imagesAvailableProperty = new SimpleBooleanProperty(false);
        ChangeListener<SongImages> songImagesChangeListener = (o, oldValue, newValue) -> imagesAvailableProperty.setValue(!newValue.getImages().isEmpty());
        this.getSelection().focusFileProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                Property<SongImages> songImagesProperty = oldValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
                songImagesProperty.removeListener(songImagesChangeListener);
                imagesAvailableProperty.setValue(false);
            }
            if (newValue != null) {
                Property<SongImages> songImagesProperty = newValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
                songImagesProperty.addListener(songImagesChangeListener);
                imagesAvailableProperty.setValue(!songImagesProperty.getValue().getImages().isEmpty());
            }
        });
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        button.setTooltip(new Tooltip("Clear image"));
        button.disableProperty().bind(this.getSelection().focusFileProperty().isNull().or(imagesAvailableProperty.not()));
        button.setOnAction(event -> this.handleClearImage());
        return button;
    }

    @SuppressWarnings("unchecked")
    private <T extends Property<U>, U> T bindSongProperty(SongProperty songProperty, T fxProperty) {
        ChangeListener<U> updateFxPropertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(fxProperty.getValue(), newValue)) {
                fxProperty.setValue(newValue);
            }
        };
        this.getSelection().focusFileProperty().addListener((o, oldFocusFile, newFocusFile) -> {
            if (newFocusFile != null) {
                Property<U> newFocusFileProperty = (Property<U>)newFocusFile.getProperties().getValue(songProperty, Object.class).getValue();
                fxProperty.setValue(newFocusFileProperty.getValue());
                newFocusFileProperty.addListener(updateFxPropertyChangeListener);
            } else {
                fxProperty.setValue(null);
            }
            if (oldFocusFile != null) {
                Property<U> oldFocusFileProperty = (Property<U>)oldFocusFile.getProperties().getValue(songProperty, Object.class).getValue();
                oldFocusFileProperty.removeListener(updateFxPropertyChangeListener);
            }
        });
        fxProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && this.getSelection().focusFileProperty().getValue() != null) {
                Property<Object> focusFileTargetProperty = this.getSelection().focusFileProperty().getValue().getProperties().getValue(songProperty, Object.class).getValue();
                if (!Objects.equals(newValue, focusFileTargetProperty.getValue())) {
                    focusFileTargetProperty.setValue(newValue);
                }
            }
        });
        return fxProperty;
    }

    private void handleCopyPropertyValueToSelectedSongs(SongProperty property) {
        SongFile focusFile = this.getSelection().focusFileProperty().getValue();
        if (focusFile != null) {
            Property<Object> sourceFileProperty = focusFile.getProperties().getValue(property, Object.class).getValue();
            Object sourceFileValue = sourceFileProperty.getValue();
            for (SongFile targetFile : this.getSelection().getSelectedFiles()) {
                Property<Object> targetFileProperty = targetFile.getProperties().getValue(property, Object.class).getValue();
                targetFileProperty.setValue(sourceFileValue);
            }
        }
    }

    private void handleClearPropertyForSelectedSongs(SongProperty property) {
        for (SongFile targetFile : this.getSelection().getSelectedFiles()) {
            Property<Object> targetFileProperty = targetFile.getProperties().getValue(property, Object.class).getValue();
            targetFileProperty.setValue(null);
        }
    }

    private void handleEnumeratePropertyForSelectedSongs(SongProperty property) {
        List<SongFile> targetFiles = this.getSelection().getSelectedFiles();
        for (int i=0; i < targetFiles.size(); i++) {
            SongFile targetFile = targetFiles.get(i);
            Property<String> targetFileProperty = targetFile.getProperties().getValue(property, String.class).getValue();
            targetFileProperty.setValue(String.valueOf(i + 1));
        }
    }

    private void handleCountSelectedSongsIntoProperty(SongProperty property) {
        for (SongFile targetFile : this.getSelection().getSelectedFiles()) {
            Property<String> targetFileProperty = targetFile.getProperties().getValue(property, String.class).getValue();
            targetFileProperty.setValue(String.valueOf(this.getSelection().getSelectedFiles().size()));
        }
    }

    private void handleAddImage() {
        throw new UnsupportedOperationException();
    }

    private void handleClearImage() {
        SongFile targetFile = this.getSelection().focusFileProperty().getValue();
        Property<SongImages> targetImagesProperty = targetFile == null ? null : targetFile.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
        if (targetImagesProperty != null) {
            targetImagesProperty.setValue(targetImagesProperty.getValue().withRemovedImages());
        }
    }

    private void processKeyPressedEvent(KeyEvent event, SongProperty property) {
        this.processKeyPressedEventForNavigation(event);
        this.processKeyPressedEventForReset(event, property);
        this.processKeyPressedEventForCopy(event, property);
    }

    private void processKeyPressedEventForNavigation(KeyEvent event) {
        if (!this.getSelection().getAvailableFiles().isEmpty()) {
            int currentIndex = this.getSelection().getAvailableFiles().indexOf(this.getSelection().focusFileProperty().getValue());
            if (event.getCode() == KeyCode.PAGE_UP && currentIndex > 0) {
                this.getSelection().focusFileProperty().setValue(this.getSelection().getAvailableFiles().get(currentIndex - 1));
            } else if (event.getCode() == KeyCode.PAGE_DOWN && currentIndex < this.getSelection().getAvailableFiles().size() - 1) {
                this.getSelection().focusFileProperty().setValue(this.getSelection().getAvailableFiles().get(currentIndex + 1));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.HOME) {
                this.getSelection().focusFileProperty().setValue(this.getSelection().getAvailableFiles().get(0));
            } else if (event.isMetaDown() && event.getCode() == KeyCode.END) {
                this.getSelection().focusFileProperty().setValue(this.getSelection().getAvailableFiles().get(this.getSelection().getAvailableFiles().size() - 1));
            }
        }
    }

    private void processKeyPressedEventForReset(KeyEvent event, SongProperty property) {
        if (event.getCode() == KeyCode.DELETE && event.isMetaDown()) {
            List<SongProperty> resetProperties = event.isShiftDown() ? List.of(SongProperty.values()) : List.of(property);
            SongFile focusFile = this.getSelection().focusFileProperty().getValue();
            if (focusFile != null) {
                for (SongProperty resetProperty : resetProperties) {
                    focusFile.getProperties().getValue(resetProperty, Object.class).resetValue();
                }
            }
        }
    }

    private void processKeyPressedEventForCopy(KeyEvent event, SongProperty property) {
        if (event.getCode() == KeyCode.ENTER && event.isMetaDown()) {
            this.handleCopyPropertyValueToSelectedSongs(property);
        }
    }

    ComponentBuilder getComponentBuilder() {
        return this.componentBuilder;
    }
    private void setComponentBuilder(ComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
    }

    Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

}
