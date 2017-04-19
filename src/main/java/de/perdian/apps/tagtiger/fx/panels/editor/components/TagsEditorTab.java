/*
 * Copyright 2014-2017 Christian Robert
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

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.support.EditorComponentBuilderFactory;
import de.perdian.apps.tagtiger.fx.support.actions.EnumerateTracksAction;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

class TagsEditorTab extends Tab {

    TagsEditorTab(EditorComponentBuilderFactory componentBuilderFactory, Selection selection, Localization localization) {

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        int currentRowIndex = 0;

        Parent titleComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.TITLE))
            .useTextField()
            .actionCopyPropertyValue()
            .build();
        Parent artistComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.ARTIST))
            .useTextField()
            .actionCopyPropertyValue()
            .build();
        Parent albumComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.ALBUM))
            .useTextField()
            .actionCopyPropertyValue()
            .build();
        Parent discComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.DISC_NUMBER))
            .useNumericTextField()
            .actionCopyPropertyValue()
            .build();
        Parent albumArtistComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.ALBUM_ARTIST))
            .useTextField()
            .actionCopyPropertyValue()
            .build();
        Parent yearComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.YEAR))
            .useNumericTextField()
            .actionCopyPropertyValue()
            .build();
        Parent trackComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.TRACK_NUMBER))
            .useNumericTextField()
            .action("icons/16/add.png", localization.enumerateTracks(), new EnumerateTracksAction())
            .actionClearPropertyValues()
            .build();
        Parent tracksComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.TRACKS_TOTAL))
            .useNumericTextField()
            .action("icons/16/add.png", localization.enumerateTracks(), new EnumerateTracksAction())
            .actionClearPropertyValues()
            .build();
        Parent genreComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.GENRE))
            .useComboBox(GenreTypes.getInstanceOf().getAlphabeticalValueList())
            .actionCopyPropertyValue()
            .build();
        Parent commentComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.COMMENT))
            .useTextField()
            .actionCopyPropertyValue()
            .build();
        Parent composerComponent = componentBuilderFactory.componentBuilder(file -> file.property(TaggablePropertyKey.COMPOSER))
            .useTextField()
            .actionCopyPropertyValue()
            .build();

        gridPane.add(new Label(localization.title()), 0, currentRowIndex, 1, 1);
        gridPane.add(titleComponent, 1, currentRowIndex, 5, 1);
        gridPane.add(new Label(localization.artist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(artistComponent, 1, currentRowIndex, 5, 1);
        gridPane.add(new Label(localization.album()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(albumComponent, 1, currentRowIndex, 3, 1);
        gridPane.add(new Label(localization.disc()), 4, currentRowIndex, 1, 1);
        gridPane.add(discComponent, 5, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.albumArtist()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(albumArtistComponent, 1, currentRowIndex, 5, 1);
        gridPane.add(new Label(localization.year()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(yearComponent, 1, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.track()), 2, currentRowIndex, 1, 1);
        gridPane.add(trackComponent, 3, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.tracks()), 4, currentRowIndex, 1, 1);
        gridPane.add(tracksComponent, 5, currentRowIndex, 1, 1);
        gridPane.add(new Label(localization.genre()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(genreComponent, 1, currentRowIndex, 5, 1);
        gridPane.add(new Label(localization.comment()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(commentComponent, 1, currentRowIndex, 5, 1);
        gridPane.add(new Label(localization.composer()), 0, ++currentRowIndex, 1, 1);
        gridPane.add(composerComponent, 1, currentRowIndex, 5, 1);

        this.setText(localization.tags());
        this.setClosable(false);
        this.setContent(gridPane);

    }

}
