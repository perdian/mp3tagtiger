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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

abstract class BatchActionEventHandler implements EventHandler<ActionEvent> {

    private String title = null;
    private List<SongFile> files = null;

    protected BatchActionEventHandler(String title, List<SongFile> files) {
        this.setTitle(title);
        this.setFiles(files);
    }

    @Override
    public void handle(ActionEvent event) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Pane mainPane = this.createPane(this.getFiles());
        Scene mainScene = new Scene(mainPane);
        mainScene.getStylesheets().add(this.getClass().getClassLoader().getResource("css/tagtiger.css").toString());
        Stage mainStage = new Stage();
        mainStage.initOwner(((Node)event.getSource()).getScene().getWindow());
        mainStage.initModality(Modality.WINDOW_MODAL);
        mainStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        mainStage.setTitle(this.getTitle());
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);
        mainStage.setWidth(Math.min(1300, screenBounds.getWidth() - 200));
        mainStage.setHeight(Math.min(700, screenBounds.getHeight() - 200));
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
        mainStage.showAndWait();
    }

    protected abstract Pane createPane(List<SongFile> files);

    private String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    private List<SongFile> getFiles() {
        return this.files;
    }
    private void setFiles(List<SongFile> files) {
        this.files = files;
    }

}
