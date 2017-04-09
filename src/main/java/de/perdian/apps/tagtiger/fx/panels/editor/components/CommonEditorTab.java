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

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.ClearPropertyValuesActionEventHandler;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.CopyPropertyValueActionEventHandler;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.GenerateTrackCountActionEventHandler;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.GenerateTrackNumberActionEventHandler;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

public class CommonEditorTab extends Tab {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public CommonEditorTab(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        BooleanProperty disableProperty = new SimpleBooleanProperty(true);
        this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> disableProperty.setValue(change.getList().size() <= 1));

        int currentRowIndex = 0;

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        gridPane.add(new Label(localization.title()), 0, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::titleProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::titleProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.artist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::artistProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::artistProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.album()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::albumProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::albumProperty, null))
            .buildControlPane(), 1, currentRowIndex, 3, 1);
        gridPane.add(new Label(localization.disc()), 4, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createNumericTextField(TaggableFile::discNumberProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::discNumberProperty, null))
            .buildControlPane(), 5, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.albumArtist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::albumArtistProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::albumArtistProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.year()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createNumericTextField(TaggableFile::yearProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::yearProperty, null))
            .buildControlPane(), 1, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.track()), 2, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createNumericTextField(TaggableFile::trackNumberProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/create.png", localization.enumerateTracks(), new GenerateTrackNumberActionEventHandler(this.currentFileProperty(), this.selectedFilesProperty()))
            .button(false, "icons/16/delete.png", localization.clearAllValues(), new ClearPropertyValuesActionEventHandler(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::trackNumberProperty))
            .buildControlPane(), 3, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.tracks()), 4, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createNumericTextField(TaggableFile::tracksTotalProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/create.png", localization.countTracks(), new GenerateTrackCountActionEventHandler(this.currentFileProperty(), this.selectedFilesProperty()))
            .button(false, "icons/16/delete.png", localization.clearAllValues(), new ClearPropertyValuesActionEventHandler(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::tracksTotalProperty))
            .buildControlPane(), 5, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.genre()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createSelectBox(TaggableFile::genreProperty, GenreTypes.getInstanceOf().getAlphabeticalValueList()))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::genreProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.comment()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::commentProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::commentProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.composer()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::composerProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::composerProperty, null))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        this.setText(localization.common());
        this.setContent(gridPane);
        this.setClosable(false);

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