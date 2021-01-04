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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.fx.TagTigerApplication;
import de.perdian.apps.tagtiger3.fx.model.Selection;
import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongImage;
import de.perdian.apps.tagtiger3.model.SongImages;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

class EditorComponentFactory {

    private ComponentBuilder componentBuilder = null;
    private Selection selection = null;
    private ObjectProperty<File> currentDirectory = null;

    EditorComponentFactory(Selection selection, Preferences preferences) {
        this.setComponentBuilder(new ComponentBuilder());
        this.setSelection(selection);

        String currentDirectoryValue = preferences.getStringProperty("EditorComponentFactory.currentDirectory").getValue();
        File currentDirectory = StringUtils.isEmpty(currentDirectoryValue) ? null : new File(currentDirectoryValue);
        ObjectProperty<File> currentDirectoryProperty = new SimpleObjectProperty<>(currentDirectory == null || !currentDirectory.exists() ? new File(System.getProperty("user.home")) : currentDirectory);
        currentDirectoryProperty.addListener((o, oldValue, newValue) -> preferences.setStringValue("EditorComponentFactory.currentDirectory", newValue == null ? null : newValue.getAbsolutePath()));
        this.setCurrentDirectory(currentDirectoryProperty);
    }

    Label createLabel(String title, double leftPadding, double rightPadding) {
        Label label = new Label(title);
        label.setPadding(new Insets(0, rightPadding, 0, leftPadding));
        return label;
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

    TextField createNumericTextField(SongAttribute attribute) {
        TextField textField = this.createTextField(attribute);
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d+")) {
                event.consume();
            }
        });
        return textField;
    }

    TextField createTextField(SongAttribute attribute) {
        StringProperty textFieldProperty = this.bindSongAttribute(attribute, new SimpleStringProperty());
        TextField textField = this.getComponentBuilder().createTextField(textFieldProperty).get();
        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.processKeyPressedEvent(event, attribute));
        textField.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        textFieldProperty.addListener((o, oldValue, newValue) -> {
            if (textField.focusedProperty().getValue()) {
                textField.selectAll();
            }
        });
        return textField;
    }

    ComboBox<String> createComboBox(SongAttribute attribute, List<String> values) {
        StringProperty valueProperty = this.bindSongAttribute(attribute, new SimpleStringProperty());
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.setPrefWidth(0);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setEditable(true);
        comboBox.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        comboBox.focusedProperty().addListener((o, oldValue, newValue) -> Platform.runLater(() -> comboBox.getEditor().selectAll()));
        comboBox.valueProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.getEditor().textProperty().addListener((o, oldValue, newValue) -> valueProperty.setValue(newValue));
        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.processKeyPressedEvent(event, attribute));
        valueProperty.addListener((o, oldValue, newValue) -> comboBox.setValue(newValue == null ? null : newValue.toString()));
        return comboBox;
    }

    Label createImagesLabel() {
        Label imagesLabel = new Label("No image");
        imagesLabel.setAlignment(Pos.TOP_CENTER);
        imagesLabel.setMaxWidth(Double.MAX_VALUE);
        imagesLabel.setMinWidth(190);
        imagesLabel.setMaxWidth(190);
        ObjectProperty<SongImages> imagesProperty = this.bindSongAttribute(SongAttribute.IMAGES, new SimpleObjectProperty<>());
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

    Button createCopyAttributeValuesToSelectedSongsButton(SongAttribute... attributes) {
        Button button = this.createButton("", FontAwesomeIcon.COPY, "Copy to other songs in selection");
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleCopyAttributeValueToSelectedSongs(attributes));
        return button;
    }

    Button createClearAttributesForSelectedSongsButton(SongAttribute... attributes) {
        Button button = this.createButton("", FontAwesomeIcon.ERASER, "Clear for all songs in selection");
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleClearAttributesForSelectedSongs(attributes));
        return button;
    }

    Button createEnumerateAttributesForSelectedSongsButton(SongAttribute indexAttribute, SongAttribute sumAttribute) {
        Button button = this.createButton("", FontAwesomeIcon.SORT_NUMERIC_ASC, "Enumerate within selection");
        button.disableProperty().bind(Bindings.size(this.getSelection().getSelectedFiles()).lessThanOrEqualTo(1));
        button.setOnAction(event -> this.handleEnumerateAttributesForSelectedSongs(indexAttribute, sumAttribute));
        return button;
    }

    Button createAddImageButton() {
        Button button = new Button("", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        button.setTooltip(new Tooltip("Add/replace image"));
        button.disableProperty().bind(this.getSelection().focusFileProperty().isNull());
        button.setOnAction(event -> this.handleAddImage(((Button)event.getSource()).getScene().getWindow()));
        return button;
    }

    Button createClearImageButton() {
        BooleanProperty imagesAvailableProperty = new SimpleBooleanProperty(false);
        ChangeListener<SongImages> songImagesChangeListener = (o, oldValue, newValue) -> imagesAvailableProperty.setValue(!newValue.getImages().isEmpty());
        this.getSelection().focusFileProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                Property<SongImages> songImagesProperty = oldValue.getAttributeValueProperty(SongAttribute.IMAGES, SongImages.class);
                songImagesProperty.removeListener(songImagesChangeListener);
                imagesAvailableProperty.setValue(false);
            }
            if (newValue != null) {
                Property<SongImages> songImagesProperty = newValue.getAttributeValueProperty(SongAttribute.IMAGES, SongImages.class);
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

    private Button createButton(String title, FontAwesomeIcon icon, String tooltip) {
        Button button = new Button(title, icon == null ? null : new FontAwesomeIconView(icon));
        button.setTooltip(new Tooltip(tooltip));
        button.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.processKeyPressedEventForNavigation(event));
        return button;
    }

    @SuppressWarnings("unchecked")
    private <T extends Property<U>, U> T bindSongAttribute(SongAttribute songAttribute, T fxProperty) {
        ChangeListener<U> updateFxPropertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(fxProperty.getValue(), newValue)) {
                fxProperty.setValue(newValue);
            }
        };
        this.getSelection().focusFileProperty().addListener((o, oldFocusFile, newFocusFile) -> {
            if (newFocusFile != null) {
                Property<U> newFocusFileProperty = (Property<U>)newFocusFile.getAttributeValueProperty(songAttribute, Object.class);
                fxProperty.setValue(newFocusFileProperty.getValue());
                newFocusFileProperty.addListener(updateFxPropertyChangeListener);
            } else {
                fxProperty.setValue(null);
            }
            if (oldFocusFile != null) {
                Property<U> oldFocusFileProperty = (Property<U>)oldFocusFile.getAttributeValueProperty(songAttribute, Object.class);
                oldFocusFileProperty.removeListener(updateFxPropertyChangeListener);
            }
        });
        fxProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && this.getSelection().focusFileProperty().getValue() != null) {
                Property<Object> focusFileTargetProperty = this.getSelection().focusFileProperty().getValue().getAttributeValueProperty(songAttribute, Object.class);
                if (!Objects.equals(newValue, focusFileTargetProperty.getValue())) {
                    focusFileTargetProperty.setValue(newValue);
                }
            }
        });
        return fxProperty;
    }

    private void handleCopyAttributeValueToSelectedSongs(SongAttribute... properties) {
        SongFile focusFile = this.getSelection().focusFileProperty().getValue();
        if (focusFile != null) {
            for (SongAttribute attribute : properties) {
                Property<Object> sourceFileProperty = focusFile.getAttributeValueProperty(attribute, Object.class);
                Object sourceFileValue = sourceFileProperty.getValue();
                for (SongFile targetFile : this.getSelection().getSelectedFiles()) {
                    Property<Object> targetFileProperty = targetFile.getAttributeValueProperty(attribute, Object.class);
                    targetFileProperty.setValue(sourceFileValue);
                }
            }
        }
    }

    private void handleClearAttributesForSelectedSongs(SongAttribute... attributes) {
        for (SongFile targetFile : this.getSelection().getSelectedFiles()) {
            for (SongAttribute attribute : attributes) {
                targetFile.clearAttributeValue(attribute);
            }
        }
    }

    private void handleEnumerateAttributesForSelectedSongs(SongAttribute indexProperty, SongAttribute sumProperty) {
        List<SongFile> targetFiles = this.getSelection().getSelectedFiles();
        for (int i=0; i < targetFiles.size(); i++) {
            SongFile targetFile = targetFiles.get(i);
            Property<String> targetIndexProperty = targetFile.getAttributeValueProperty(indexProperty, String.class);
            targetIndexProperty.setValue(String.valueOf(i + 1));
            Property<String> targetSumProperty = targetFile.getAttributeValueProperty(sumProperty, String.class);
            targetSumProperty.setValue(String.valueOf(this.getSelection().getSelectedFiles().size()));
        }
    }

    private void handleAddImage(Window parentWindow) {
        FileChooser imageFileChooser = new FileChooser();
        imageFileChooser.setInitialDirectory(this.getCurrentDirectory().getValue());
        imageFileChooser.setTitle("Select image file");
        imageFileChooser.setSelectedExtensionFilter(new ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
        File imageFile = imageFileChooser.showOpenDialog(parentWindow);
        if (imageFile != null && imageFile.exists()) {
            this.getCurrentDirectory().setValue(imageFile.getParentFile());
            try {
                java.awt.image.BufferedImage image = ImageIO.read(imageFile);
                try (ByteArrayOutputStream pngStream = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "png", pngStream);
                    SongFile songFile = this.getSelection().focusFileProperty().getValue();
                    Property<SongImages> songImagesProperty = songFile.getAttributeValueProperty(SongAttribute.IMAGES, SongImages.class);
                    SongImage songImage = new SongImage(null, null, pngStream.toByteArray(), "image/png");
                    songImagesProperty.setValue(songImagesProperty.getValue().withRemovedImages().withNewImage(songImage));
                }
            } catch (IOException e) {
                TagTigerApplication.showError("Cannot load selected image file", e, parentWindow);
            }
        }
    }

    private void handleClearImage() {
        SongFile targetFile = this.getSelection().focusFileProperty().getValue();
        Property<SongImages> targetImagesProperty = targetFile == null ? null : targetFile.getAttributeValueProperty(SongAttribute.IMAGES, SongImages.class);
        if (targetImagesProperty != null) {
            targetImagesProperty.setValue(targetImagesProperty.getValue().withRemovedImages());
        }
    }

    private void processKeyPressedEvent(KeyEvent event, SongAttribute attribute) {
        this.processKeyPressedEventForNavigation(event);
        this.processKeyPressedEventForReset(event, attribute);
        this.processKeyPressedEventForCopy(event, attribute);
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

    private void processKeyPressedEventForReset(KeyEvent event, SongAttribute attribute) {
        if (event.getCode() == KeyCode.DELETE && event.isMetaDown()) {
            List<SongAttribute> resetAttributes = event.isShiftDown() ? List.of(SongAttribute.values()) : List.of(attribute);
            SongFile focusFile = this.getSelection().focusFileProperty().getValue();
            if (focusFile != null) {
                for (SongAttribute resetProperty : resetAttributes) {
                    focusFile.resetAttributeValue(resetProperty);
                }
            }
        }
    }

    private void processKeyPressedEventForCopy(KeyEvent event, SongAttribute attribute) {
        if (event.getCode() == KeyCode.ENTER && event.isMetaDown()) {
            this.handleCopyAttributeValueToSelectedSongs(attribute);
        }
    }

    private ComponentBuilder getComponentBuilder() {
        return this.componentBuilder;
    }
    private void setComponentBuilder(ComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private ObjectProperty<File> getCurrentDirectory() {
        return this.currentDirectory;
    }
    private void setCurrentDirectory(ObjectProperty<File> currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

}
