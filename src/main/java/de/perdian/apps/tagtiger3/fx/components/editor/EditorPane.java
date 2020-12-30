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

import de.perdian.apps.tagtiger3.fx.components.selection.SelectionModel;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EditorPane extends GridPane {

    public EditorPane(SelectionModel selectionModel) {
        EditorComponentBuilder componentBuilder = new EditorComponentBuilder(selectionModel);

        EditorFilePane fileEditorPane = new EditorFilePane(componentBuilder);
        TitledPane fileTitledPane = new TitledPane("File", fileEditorPane);
        fileTitledPane.setCollapsible(false);
        fileTitledPane.setFocusTraversable(false);
        GridPane.setHgrow(fileTitledPane, Priority.ALWAYS);

        EditorTagsPane tagsEditorPane = new EditorTagsPane(componentBuilder);
        GridPane.setHgrow(tagsEditorPane, Priority.ALWAYS);
        EditorImagePane imageEditorPane = new EditorImagePane(componentBuilder);
        GridPane tagsPane = new GridPane();
        tagsPane.add(tagsEditorPane, 0, 0, 1, 1);
        tagsPane.add(imageEditorPane, 1, 0, 1, 1);
        tagsPane.setHgap(10);
        tagsPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane tagsTitledPane = new TitledPane("Tags", tagsPane);
        tagsTitledPane.setCollapsible(false);
        tagsTitledPane.setFocusTraversable(false);
        GridPane.setHgrow(tagsTitledPane, Priority.ALWAYS);

        this.setVgap(10);
        this.add(fileTitledPane, 0, 0, 1, 1);
        this.add(tagsTitledPane, 0, 1, 1, 1);

    }

}
