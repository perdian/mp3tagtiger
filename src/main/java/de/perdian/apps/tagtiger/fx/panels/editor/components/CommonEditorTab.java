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
import de.perdian.apps.tagtiger.fx.handlers.files.ClearPropertyValueAction;
import de.perdian.apps.tagtiger.fx.handlers.files.CopyPropertyValueAction;
import de.perdian.apps.tagtiger.fx.handlers.files.GenerateTrackCountAction;
import de.perdian.apps.tagtiger.fx.handlers.files.GenerateTrackNumberAction;
import de.perdian.apps.tagtiger.fx.panels.editor.support.ComponentBuilder;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
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

    private final Property<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public CommonEditorTab(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        BooleanProperty disableProperty = new SimpleBooleanProperty(true);
        this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> disableProperty.setValue(change.getList().size() <= 1));

        int currentRowIndex = 0;

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        gridPane.add(new Label(localization.title()), 0, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::titleProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::titleProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.artist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::artistProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::artistProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.album()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::albumProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::albumProperty))
            .buildControlPane(), 1, currentRowIndex, 3, 1);
        gridPane.add(new Label(localization.disc()), 4, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createNumericTextField(TaggableFile::discNumberProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::discNumberProperty))
            .buildControlPane(), 5, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.albumArtist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::albumArtistProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::albumArtistProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.year()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createNumericTextField(TaggableFile::yearProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::yearProperty))
            .buildControlPane(), 1, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.track()), 2, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createNumericTextField(TaggableFile::trackNumberProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/create.png", localization.enumerateTracks(), new GenerateTrackNumberAction())
            .secondaryAction("icons/16/delete.png", localization.clearAllValues(), new ClearPropertyValueAction(TaggableFile::trackNumberProperty))

            .buildControlPane(), 3, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.tracks()), 4, currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createNumericTextField(TaggableFile::tracksTotalProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/create.png", localization.countTracks(), new GenerateTrackCountAction())
            .secondaryAction("icons/16/delete.png", localization.clearAllValues(), new ClearPropertyValueAction(TaggableFile::tracksTotalProperty))
            .buildControlPane(), 5, currentRowIndex, 1, 1);

        gridPane.add(new Label(localization.genre()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createSelectBox(TaggableFile::genreProperty, GenreTypes.getInstanceOf().getAlphabeticalValueList()))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::genreProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.comment()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::commentProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::commentProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        gridPane.add(new Label(localization.composer()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(ComponentBuilder.create(this.currentFileProperty(), this.selectedFilesProperty(), this.availableFilesProperty())
            .control(componentFactory.createTextField(TaggableFile::composerProperty))
            .actionsDisabledProperty(disableProperty)
            .primaryAction("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(TaggableFile::composerProperty))
            .buildControlPane(), 1, currentRowIndex, 5, 1);

        this.setText(localization.common());
        this.setContent(gridPane);
        this.setClosable(false);

    }

    public Property<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

}