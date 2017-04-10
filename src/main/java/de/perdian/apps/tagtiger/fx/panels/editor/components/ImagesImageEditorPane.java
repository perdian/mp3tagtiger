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
package de.perdian.apps.tagtiger.fx.panels.editor.components;

import java.util.Optional;

import org.jaudiotagger.tag.reference.PictureTypes;

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TagImage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class ImagesImageEditorPane extends HBox {

    private final BooleanProperty changed = new SimpleBooleanProperty();
    private EventHandler<ActionEvent> onDeleteActionHandler = null;

    ImagesImageEditorPane(TagImage image, Localization localization) {

        ImageView imageView = new ImageView(image.imageProperty().getValue());
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        this.getChildren().add(imageView);

        TextField descriptionField = new TextField();
        descriptionField.setMaxWidth(Double.MAX_VALUE);
        descriptionField.textProperty().set(image.descriptionProperty().get());
        descriptionField.textProperty().addListener((o, oldValue, newValue) -> image.descriptionProperty().set(newValue));
        image.descriptionProperty().addListener(new WeakChangeListener<>((o, oldValue, newValue) -> descriptionField.setText(newValue)));
        GridPane.setHgrow(descriptionField, Priority.ALWAYS);

        ChoiceBox<String> pictureTypeBox = new ChoiceBox<>(FXCollections.observableArrayList(PictureTypes.getInstanceOf().getAlphabeticalValueList()));
        pictureTypeBox.setMaxWidth(Double.MAX_VALUE);
        pictureTypeBox.valueProperty().set(image.pictureTypeProperty().get());
        pictureTypeBox.valueProperty().addListener((o, oldValue, newValue) -> image.pictureTypeProperty().set(newValue));
        image.pictureTypeProperty().addListener(new WeakChangeListener<>((o, oldValue, newValue) -> pictureTypeBox.setValue(newValue)));
        GridPane.setHgrow(pictureTypeBox, Priority.ALWAYS);

        Button removeButton = new Button(localization.removeImage());
        removeButton.setGraphic(new ImageView(new Image(ImagesImageEditorPane.class.getClassLoader().getResourceAsStream("icons/16/delete.png"))));
        removeButton.setOnAction(event -> Optional.ofNullable(this.getOnDeleteActionHandler()).ifPresent(handler -> handler.handle(event)));
        HBox buttonPane = new HBox(removeButton);
        buttonPane.setAlignment(Pos.CENTER_RIGHT);

        GridPane descriptionPane = new GridPane();
        descriptionPane.setHgap(5);
        descriptionPane.setVgap(5);
        descriptionPane.add(new Label(localization.description() + ":"), 0, 0);
        descriptionPane.add(descriptionField, 1, 0);
        descriptionPane.add(new Label(localization.pictureType() + ":"), 0, 1);
        descriptionPane.add(pictureTypeBox, 1, 1);
        descriptionPane.add(buttonPane, 1, 2);
        this.getChildren().add(descriptionPane);
        HBox.setHgrow(descriptionPane, Priority.ALWAYS);
        HBox.setMargin(descriptionPane, new Insets(5, 0, 5, 10));

    }

    BooleanProperty changedProperty() {
        return this.changed;
    }

    EventHandler<ActionEvent> getOnDeleteActionHandler() {
        return this.onDeleteActionHandler;
    }
    void setOnDeleteActionHandler(EventHandler<ActionEvent> onDeleteActionHandler) {
        this.onDeleteActionHandler = onDeleteActionHandler;
    }

}