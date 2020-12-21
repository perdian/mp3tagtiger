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
package de.perdian.apps.tagtiger3.fx;

import de.perdian.apps.tagtiger3.fx.components.editor.EditorPane;
import de.perdian.apps.tagtiger3.fx.components.selection.SelectionPane;
import de.perdian.apps.tagtiger3.fx.components.status.StatusPane;
import de.perdian.commons.fx.AbstractApplication;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class TagTigerApplication extends AbstractApplication {

    @Override
    protected Pane createMainPane() {

        SelectionPane selectionPane = new SelectionPane(this.getPreferences());
        EditorPane editorPane = new EditorPane();
        SplitPane mainSplitPane = new SplitPane(selectionPane, editorPane);
        mainSplitPane.setDividerPositions(0.3d);
        GridPane.setHgrow(mainSplitPane, Priority.ALWAYS);
        GridPane.setVgrow(mainSplitPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane();
        statusPane.setPadding(new Insets(10, 10, 10, 10));

        GridPane mainPane = new GridPane();
        mainPane.add(mainSplitPane, 0, 0, 1, 1);
        mainPane.add(statusPane, 0, 1, 1, 1);
        return mainPane;

    }

    @Override
    protected void configurePrimaryStage(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("MP3 TagTiger");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
    }

}
