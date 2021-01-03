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
package de.perdian.apps.tagtiger3.fx.components.actions.batchactions;

import java.util.List;

import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

public class ExtractTagsFromFilenamesActionEventHandler extends BatchActionEventHandler {

    public ExtractTagsFromFilenamesActionEventHandler(ObservableList<SongFile> files) {
        super("Extract tags from file names", files);
    }

    @Override
    protected Pane createPane(List<SongFile> files) {
        throw new UnsupportedOperationException();
    }

}