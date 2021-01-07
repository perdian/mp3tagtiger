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
package de.perdian.apps.tagtiger.fx.components.editor;

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.fx.model.Selection;
import de.perdian.apps.tagtiger.model.SongAttribute;
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
        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.TITLE.getTitle()), 0, 0, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.TITLE), 1, 0, 10, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.TITLE), 11, 0, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.ARTIST.getTitle()), 0, 1, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.ARTIST), 1, 1, 10, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.ARTIST), 11, 1, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.ALBUM.getTitle()), 0, 2, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.ALBUM), 1, 2, 7, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.ALBUM), 8, 2, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel(SongAttribute.YEAR.getTitle()), 9, 2, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongAttribute.YEAR), 10, 2, 1, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.YEAR), 11, 2, 1, 1);
        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.ALBUM_ARTIST.getTitle()), 0, 3, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.ALBUM_ARTIST), 1, 3, 10, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.ALBUM_ARTIST), 11, 3, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Track(s)"), 0, 4, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongAttribute.TRACK_NUMBER), 1, 4, 1, 1);
        tagsPane.add(componentFactory.createLabel("/", 0, 0), 2, 4, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongAttribute.TRACKS_TOTAL), 3, 4, 1, 1);
        tagsPane.add(componentFactory.createEnumerateAttributesForSelectedSongsButton(SongAttribute.TRACK_NUMBER, SongAttribute.TRACKS_TOTAL), 4, 4, 1, 1);
        tagsPane.add(componentFactory.createClearAttributesForSelectedSongsButton(SongAttribute.TRACK_NUMBER, SongAttribute.TRACKS_TOTAL), 5, 4, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel(SongAttribute.GENRE.getTitle()), 6, 4, 1, 1);
        tagsPane.add(componentFactory.createComboBox(SongAttribute.GENRE, GenreTypes.getInstanceOf().getAlphabeticalValueList()), 7, 4, 4, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.GENRE), 11, 4, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel("Disc(s)"), 0, 5, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongAttribute.DISC_NUMBER), 1, 5, 1, 1);
        tagsPane.add(componentFactory.createLabel("/", 0, 0), 2, 5, 1, 1);
        tagsPane.add(componentFactory.createNumericTextField(SongAttribute.DISCS_TOTAL), 3, 5, 1, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.DISC_NUMBER, SongAttribute.DISCS_TOTAL), 4, 5, 1, 1);
        tagsPane.add(componentFactory.createClearAttributesForSelectedSongsButton(SongAttribute.DISC_NUMBER, SongAttribute.DISCS_TOTAL), 5, 5, 1, 1);
        tagsPane.add(componentFactory.createInnerLabel(SongAttribute.COMPOSER.getTitle()), 6, 5, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.COMPOSER), 7, 5, 4, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.COMPOSER), 11, 5, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.COMMENT.getTitle()), 0, 6, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.COMMENT), 1, 6, 10, 1);
        tagsPane.add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.COMMENT), 11, 6, 1, 1);

        tagsPane.add(componentFactory.createOuterLabel(SongAttribute.FILENAME.getTitle()), 0, 7, 1, 1);
        tagsPane.add(componentFactory.createTextField(SongAttribute.FILENAME), 1, 7, 11, 1);

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
        imagesButtons.getChildren().add(componentFactory.createCopyAttributeValuesToSelectedSongsButton(SongAttribute.IMAGES));
        imagesButtons.getChildren().add(componentFactory.createAddImageButton());
        imagesButtons.getChildren().add(componentFactory.createClearImageButton());

        BorderPane imagesPane = new BorderPane();
        imagesPane.setTop(componentFactory.createImagesLabel());
        imagesPane.setBottom(imagesButtons);
        return imagesPane;

    }

}
