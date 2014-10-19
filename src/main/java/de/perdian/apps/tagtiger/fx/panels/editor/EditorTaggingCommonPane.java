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

import java.util.List;
import java.util.Objects;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.business.framework.tagging.TagHandler;
import de.perdian.apps.tagtiger.fx.components.EditorComponentFactory;

class EditorTaggingCommonPane extends GridPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    EditorTaggingCommonPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        this.setHgap(5);
        this.add(new Label(localization.title()), 0, 0, 4, 1);
        this.add(this.createTextFieldControl(componentFactory, TagHandler.TITLE, localization), 0, 1, 4, 1);
        this.add(new Label(localization.artist()), 0, 2, 4, 1);
        this.add(this.createTextFieldControl(componentFactory, TagHandler.ARTIST, localization), 0, 3, 4, 1);
        this.add(new Label(localization.album()), 0, 4, 3, 1);
        this.add(new Label(localization.year()), 3, 4, 1, 1);
        this.add(this.createTextFieldControl(componentFactory, TagHandler.ALBUM, localization), 0, 5, 3, 1);
        this.add(this.createNumericTextFieldControl(componentFactory, TagHandler.YEAR, localization), 3, 5, 1, 1);
        this.add(new Label(localization.track()), 0, 6, 1, 1);
        this.add(new Label(localization.tracks()), 1, 6, 1, 1);
        this.add(new Label(localization.disc()), 2, 6, 1, 1);
        this.add(new Label(localization.discs()), 3, 6, 1, 1);
        this.add(this.createNumericTextFieldControl(componentFactory, TagHandler.TRACK_NO, localization), 0, 7, 1, 1);
        this.add(this.createNumericTextFieldControl(componentFactory, TagHandler.TRACKS_TOTAL, localization), 1, 7, 1, 1);
        this.add(this.createNumericTextFieldControl(componentFactory, TagHandler.DISC_NO, localization), 2, 7, 1, 1);
        this.add(this.createNumericTextFieldControl(componentFactory, TagHandler.DISCS_TOTAL, localization), 3, 7, 1, 1);
        this.add(new Label(localization.genre()), 0, 8, 4, 1);
        this.add(this.createSelectBoxControl(componentFactory, TagHandler.GENRE, GenreTypes.getInstanceOf().getAlphabeticalValueList(), localization), 0, 9, 4, 1);
        this.add(new Label(localization.comment()), 0, 10, 4, 1);
        this.add(this.createTextFieldControl(componentFactory, TagHandler.COMMENT, localization), 0, 11, 4, 1);
        this.add(new Label(localization.composer()), 0, 12, 4, 1);
        this.add(this.createTextFieldControl(componentFactory, TagHandler.COMPOSER, localization), 0, 13, 4, 1);

    }

    private Node createSelectBoxControl(EditorComponentFactory<TaggableFile> componentFactory, TagHandler tag, List<String> values, Localization localization) {
        return this.createEnhancedControl(componentFactory.createSelectBox(file -> file.getTagProperty(tag), values), tag, localization);
    }

    private Node createNumericTextFieldControl(EditorComponentFactory<TaggableFile> componentFactory, TagHandler tag, Localization localization) {
        TextField textField = componentFactory.createTextField(file -> file.getTagProperty(tag));
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d+")) {
                event.consume();
            }
        });
        return this.createEnhancedControl(textField, tag, localization);
    }

    private Node createTextFieldControl(EditorComponentFactory<TaggableFile> componentFactory, TagHandler tag, Localization localization) {
        return this.createEnhancedControl(componentFactory.createTextField(file -> file.getTagProperty(tag)), tag, localization);
    }

    private Node createEnhancedControl(Control control, TagHandler tag, Localization localization) {

        HBox resultControl = new HBox();
        resultControl.setSpacing(2);
        resultControl.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHgrow(resultControl, Priority.ALWAYS);

        resultControl.getChildren().add(control);
        HBox.setHgrow(control, Priority.ALWAYS);

        Node groupActionControl = this.createGroupActionControl(tag, localization);
        if (groupActionControl != null) {
            resultControl.getChildren().add(groupActionControl);
        }

        return resultControl;

    }

    private Node createGroupActionControl(TagHandler tag, Localization localization) {
        switch (tag.getGroupAction()) {

            case COPY:
                Button copyButton = new Button(null, new ImageView(new Image(EditorTaggingCommonPane.class.getClassLoader().getResourceAsStream("icons/16/copy.png"))));
                copyButton.setOnAction(event -> this.handleGroupAction(tag));
                copyButton.setTooltip(new Tooltip(localization.copyToAllOtherSelectedFiles()));
                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> {
                    copyButton.setDisable(!change.getList().stream().filter(file -> !Objects.equals(this.currentFileProperty().get(), file)).findAny().isPresent());
                });
                return copyButton;

            case GENERATE_FROM_POSITION:
                Button generateButton = new Button(null, new ImageView(new Image(EditorTaggingCommonPane.class.getClassLoader().getResourceAsStream("icons/16/create.png"))));
                generateButton.setOnAction(event -> this.handleGroupAction(tag));
                generateButton.setTooltip(new Tooltip(localization.enumerateTracks()));
                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> generateButton.setDisable(change.getList().isEmpty()));
                return generateButton;

            default:
                return null;

        }
    }

    private void handleGroupAction(TagHandler tag) {
        tag.getGroupAction().apply(this.currentFileProperty().get(), tag, this.selectedFilesProperty().get());
    }


    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}