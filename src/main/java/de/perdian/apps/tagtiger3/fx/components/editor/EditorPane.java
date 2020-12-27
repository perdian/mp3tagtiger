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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EditorPane extends GridPane {

    public EditorPane(SelectionModel selectionModel) {

        BorderPane filePane = new BorderPane();
        filePane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane fileTitledPane = new TitledPane("File", filePane);
        fileTitledPane.setCollapsible(false);
        GridPane.setHgrow(fileTitledPane, Priority.ALWAYS);

        EditorComponentBuilder componentBuilder = new EditorComponentBuilder(selectionModel);
        EditorTagsPane tagsEditorPane = new EditorTagsPane(componentBuilder);
        tagsEditorPane.setPadding(new Insets(10, 10, 10, 10));
        Tab tagsEditorTab = new Tab("Tags", tagsEditorPane);
        tagsEditorTab.setClosable(false);
        BorderPane imagesEditorPane = new BorderPane();
        imagesEditorPane.setPadding(new Insets(10, 10, 10, 10));
        Tab imagesEditorTab = new Tab("Images", imagesEditorPane);
        imagesEditorTab.setClosable(false);
        TabPane tagsPane = new TabPane(tagsEditorTab, imagesEditorTab);
        TitledPane tagsTitledPane = new TitledPane("Tags", tagsPane);
        tagsTitledPane.setCollapsible(false);
        GridPane.setHgrow(tagsTitledPane, Priority.ALWAYS);

        this.setVgap(10);
        this.add(fileTitledPane, 0, 0, 1, 1);
        this.add(tagsTitledPane, 0, 1, 1, 1);

    }

}
