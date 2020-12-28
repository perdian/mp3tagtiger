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

import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

class EditorTagsPane extends GridPane {

    EditorTagsPane(EditorComponentBuilder editorComponentBuilder) {

        this.add(editorComponentBuilder.createLabel("Title", 0, 5), 0, 0, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.TITLE), 1, 0, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.TITLE), 10, 0, 1, 1);
        this.add(editorComponentBuilder.createLabel("Artist", 0, 5), 0, 1, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.ARTIST), 1, 1, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.ARTIST), 10, 1, 1, 1);
        this.add(editorComponentBuilder.createLabel("Album", 0, 5), 0, 2, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.ALBUM), 1, 2, 5, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.ALBUM), 6, 2, 1, 1);
        this.add(editorComponentBuilder.createLabel("Year", 10, 5), 7, 2, 1, 1);
        this.add(editorComponentBuilder.createNumericTextField(SongProperty.YEAR), 8, 2, 2, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.YEAR), 10, 2, 1, 1);
        this.add(editorComponentBuilder.createLabel("Album artist", 0, 5), 0, 3, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.ALBUM_ARTIST), 1, 3, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.ALBUM_ARTIST), 10, 3, 1, 1);
        this.add(editorComponentBuilder.createLabel("Disc", 0, 5), 0, 4, 1, 1);
        this.add(editorComponentBuilder.createNumericTextField(SongProperty.DISC_NUMBER), 1, 4, 1, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.DISC_NUMBER), 2, 4, 1, 1);
        this.add(editorComponentBuilder.createLabel("Track", 10, 5), 3, 4, 1, 1);
        this.add(editorComponentBuilder.createNumericTextField(SongProperty.TRACK_NUMBER), 4, 4, 1, 1);
        this.add(editorComponentBuilder.enumerateWithinSelectionButton(SongProperty.TRACK_NUMBER), 5, 4, 1, 1);
        this.add(editorComponentBuilder.clearForSelectionButton(SongProperty.TRACK_NUMBER), 6, 4, 1, 1);
        this.add(editorComponentBuilder.createLabel("Tracks", 10, 5), 7, 4, 1, 1);
        this.add(editorComponentBuilder.createNumericTextField(SongProperty.TRACKS_TOTAL), 8, 4, 1, 1);
        this.add(editorComponentBuilder.countSelectionButton(SongProperty.TRACKS_TOTAL), 9, 4, 1, 1);
        this.add(editorComponentBuilder.clearForSelectionButton(SongProperty.TRACKS_TOTAL), 10, 4, 1, 1);
        this.add(editorComponentBuilder.createLabel("Genre", 0, 5), 0, 5, 1, 1);
        this.add(editorComponentBuilder.createComboBox(SongProperty.GENRE, GenreTypes.getInstanceOf().getAlphabeticalValueList()), 1, 5, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.GENRE), 10, 5, 1, 1);
        this.add(editorComponentBuilder.createLabel("Comment", 0, 5), 0, 6, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.COMMENT), 1, 6, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.COMMENT), 10, 6, 1, 1);
        this.add(editorComponentBuilder.createLabel("Composer", 0, 5), 0, 7, 1, 1);
        this.add(editorComponentBuilder.createTextField(SongProperty.COMPOSER), 1, 7, 9, 1);
        this.add(editorComponentBuilder.createCopyToOtherSongsButton(SongProperty.COMPOSER), 10, 7, 1, 1);

        this.getColumnConstraints().add(new ColumnConstraints(75));
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints(60));
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints(60));
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.setHgap(5);
        this.setVgap(5);

    }

}
