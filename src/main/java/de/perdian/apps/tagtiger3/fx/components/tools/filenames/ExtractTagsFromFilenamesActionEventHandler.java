/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.tools.filenames;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class ExtractTagsFromFilenamesActionEventHandler extends FilenamesToolActionEventHandler {

    public ExtractTagsFromFilenamesActionEventHandler(ObservableList<SongFile> files) {
        super("Extract tags from file names", files);
    }

    @Override
    protected FilenamesToolLegendPane createLegendsPane() {

        Map<String, String> legendVariables = Arrays.stream(FilenamesToolAttribute.values())
            .filter(attribute -> attribute.getFilenameResolver() != null)
            .collect(Collectors.toMap(variable -> variable.name().toLowerCase(), variable -> variable.getAttribute().getTitle(), (e1, e2) -> e1, LinkedHashMap::new));

        return new FilenamesToolLegendPane("Destination regex groups", legendVariables, "?<", ">");

    }

    @Override
    protected void updateToItems(String pattern, ObservableList<FilenamesToolItem> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateToSongFiles(ObservableList<FilenamesToolItem> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected TableView<FilenamesToolItem> createTableView(ObservableList<FilenamesToolItem> items) {
        return new TableView<>();
    }

}