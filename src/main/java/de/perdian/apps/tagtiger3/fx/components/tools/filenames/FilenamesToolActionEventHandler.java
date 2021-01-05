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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public abstract class FilenamesToolActionEventHandler implements EventHandler<ActionEvent> {

    private String title = null;
    private List<SongFile> files = null;

    protected FilenamesToolActionEventHandler(String title, List<SongFile> files) {
        this.setTitle(title);
        this.setFiles(files);
    }

    @Override
    public void handle(ActionEvent event) {

        ObservableList<FilenamesToolItem> dialogItems = FXCollections.observableArrayList(this.getFiles().stream().map(FilenamesToolItem::new).collect(Collectors.toList()));
        Pane dialogPane = this.createDialogPane(dialogItems);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene mainScene = new Scene(dialogPane);
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

    private Pane createDialogPane(ObservableList<FilenamesToolItem> items) {

        FilenamesToolLegendPane legendPane = this.createLegendsPane();

        FilenamesToolPatternPane patternPane = new FilenamesToolPatternPane();
        patternPane.patternProperty().addListener((o, oldValue, newValue) -> this.updateToItems(newValue, items));
        patternPane.setOnActionEvent(event -> {
            this.updateToSongFiles(items);
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        });

        TableView<FilenamesToolItem> tableView = this.createTableView(items);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox dialogPane = new VBox();
        dialogPane.getChildren().add(legendPane);
        dialogPane.getChildren().add(patternPane);
        dialogPane.getChildren().add(tableView);
        dialogPane.setPadding(new Insets(10, 10, 10, 10));
        dialogPane.setSpacing(10);
        return dialogPane;

    }

    protected abstract FilenamesToolLegendPane createLegendsPane();
    protected abstract TableView<FilenamesToolItem> createTableView(ObservableList<FilenamesToolItem> items);

    protected abstract void updateToItems(String pattern, ObservableList<FilenamesToolItem> items);

    protected void updateToSongFiles(ObservableList<FilenamesToolItem> items) {
        for (FilenamesToolItem item : items) {
            for (Map.Entry<SongAttribute, FilenamesToolItemValue> itemValue : item.getValues().entrySet()) {
                item.getSongFile().getAttributeValueProperty(itemValue.getKey(), String.class).setValue(itemValue.getValue().getNewValue().getValue());
            }
        }
    }

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
