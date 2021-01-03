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

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger3.fx.model.Selection;
import de.perdian.apps.tagtiger3.model.SongProperty;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class EditorPane extends BorderPane {

    public EditorPane(Selection selection, Preferences preferences) {
        EditorComponentFactory componentFactory = new EditorComponentFactory(selection, preferences);

        Pane tagValuesPane = this.createTagValuesPane(componentFactory);
        GridPane.setHgrow(tagValuesPane, Priority.ALWAYS);

        Pane tagImagesPane = this.createTagImagesPane(componentFactory);
        GridPane tagsPane = new GridPane();
        tagsPane.add(tagValuesPane, 0, 0, 1, 1);
        tagsPane.add(tagImagesPane, 1, 0, 1, 1);
        tagsPane.setHgap(10);
        tagsPane.setPadding(new Insets(10, 10, 10, 10));

        TitledPane tagsTitledPane = new TitledPane("Tags", tagsPane);
        tagsTitledPane.setCollapsible(false);
        tagsTitledPane.setFocusTraversable(false);
        GridPane.setHgrow(tagsTitledPane, Priority.ALWAYS);

        this.setCenter(tagsTitledPane);
        this.disableProperty().bind(selection.busyProperty());

    }

    private Pane createTagValuesPane(EditorComponentFactory componentFactory) {

        GridPane tagsPane = new GridPane();
        tagsPane.add(componentFactory.createOuterLabel("Title"), 0, 0, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.TITLE), 1, 0, 10, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.TITLE), 11, 0, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Artist"), 0, 1, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.ARTIST), 1, 1, 10, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.ARTIST), 11, 1, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Album"), 0, 2, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.ALBUM), 1, 2, 7, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.ALBUM), 8, 2, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel("Year"), 9, 2, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongProperty.YEAR), 10, 2, 1, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.YEAR), 11, 2, 1, 1);
        tagsPane.add(componentFactory.createOuterLabel("Album artist"), 0, 3, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.ALBUM_ARTIST), 1, 3, 10, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.ALBUM_ARTIST), 11, 3, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Track(s)"), 0, 4, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongProperty.TRACK_NUMBER), 1, 4, 1, 1);
        tagsPane.add(componentFactory.createLabel("/", 0, 0), 2, 4, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongProperty.TRACKS_TOTAL), 3, 4, 1, 1);
        tagsPane.add(componentFactory.createEnumeratePropertiesForSelectedSongsButton(SongProperty.TRACK_NUMBER, SongProperty.TRACKS_TOTAL), 4, 4, 1, 1);
        tagsPane.add(componentFactory.createClearPropertiesForSelectedSongsButton(SongProperty.TRACK_NUMBER, SongProperty.TRACKS_TOTAL), 5, 4, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel("Genre"), 6, 4, 1, 1);
        tagsPane.add(componentFactory.createComboBox(SongProperty.GENRE, GenreTypes.getInstanceOf().getAlphabeticalValueList()), 7, 4, 4, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.GENRE), 11, 4, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Disc(s)"), 0, 5, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongProperty.DISC_NUMBER), 1, 5, 1, 1);
        tagsPane.add(componentFactory.createLabel("/", 0, 0), 2, 5, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongProperty.DISCS_TOTAL), 3, 5, 1, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.DISC_NUMBER, SongProperty.DISCS_TOTAL), 4, 5, 1, 1);
        tagsPane.add(componentFactory.createClearPropertiesForSelectedSongsButton(SongProperty.DISC_NUMBER, SongProperty.DISCS_TOTAL), 5, 5, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel("Composer"), 6, 5, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.COMPOSER), 7, 5, 4, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.COMPOSER), 11, 5, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Comment"), 0, 6, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.COMMENT), 1, 6, 10, 1);
        tagsPane.add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.COMMENT), 11, 6, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("File name"), 0, 7, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongProperty.FILENAME), 1, 7, 11, 1);

//        tagsPane.add(componentFactory.createOuterLabel("Genre"), 0, 5, 1, 1);
//        tagsPane.add(componentFactory.createComboBox(SongProperty.GENRE, GenreTypes.getInstanceOf().getAlphabeticalValueList()), 1, 5, 9, 1);
//        tagsPane.add(componentFactory.createCopyPropertyValueToSelectedSongsButton(SongProperty.GENRE), 10, 5, 1, 1);
//        tagsPane.add(componentFactory.createOuterLabel("Comment"), 0, 6, 1, 1);
//        tagsPane.add(componentFactory.createTextField(SongProperty.COMMENT), 1, 6, 9, 1);
//        tagsPane.add(componentFactory.createCopyPropertyValueToSelectedSongsButton(SongProperty.COMMENT), 10, 6, 1, 1);
//        tagsPane.add(componentFactory.createOuterLabel("Composer"), 0, 7, 1, 1);
//        tagsPane.add(componentFactory.createTextField(SongProperty.COMPOSER), 1, 7, 9, 1);
//        tagsPane.add(componentFactory.createCopyPropertyValueToSelectedSongsButton(SongProperty.COMPOSER), 10, 7, 1, 1);
//        tagsPane.add(componentFactory.createOuterLabel("File name"), 0, 8, 1, 1);
//        tagsPane.add(componentFactory.createTextField(SongProperty.FILENAME), 1, 8, 10, 1);

        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 0
        tagsPane.getColumnConstraints().add(new ColumnConstraints(50)); // 1
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 2
        tagsPane.getColumnConstraints().add(new ColumnConstraints(50)); // 3
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 4
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 5
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 6
        tagsPane.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, null, true)); // 7
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 8
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 9
        tagsPane.getColumnConstraints().add(new ColumnConstraints(75)); // 10
        tagsPane.getColumnConstraints().add(new ColumnConstraints()); // 11
        tagsPane.setHgap(5);
        tagsPane.setVgap(5);
        return tagsPane;

    }

    private Pane createTagImagesPane(EditorComponentFactory componentFactory) {

        HBox imagesButtons = new HBox();
        imagesButtons.setSpacing(5);
        imagesButtons.setAlignment(Pos.CENTER);
        imagesButtons.getChildren().add(componentFactory.createCopyPropertyValuesToSelectedSongsButton(SongProperty.IMAGES));
        imagesButtons.getChildren().add(componentFactory.createAddImageButton());
        imagesButtons.getChildren().add(componentFactory.createClearImageButton());

        BorderPane imagesPane = new BorderPane();
        imagesPane.setTop(componentFactory.createImagesLabel());
        imagesPane.setBottom(imagesButtons);
        return imagesPane;

    }

}
