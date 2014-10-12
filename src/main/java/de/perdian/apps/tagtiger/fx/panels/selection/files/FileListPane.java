/*
 * Copyright 2014 Christian Robert
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
package de.perdian.apps.tagtiger.fx.panels.selection.files;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobListener;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

public class FileListPane extends VBox {

    public FileListPane(TagTiger tagTiger) {

        TableView<TaggableFile> selectedFilesTable = new TableView<>();
        selectedFilesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        selectedFilesTable.setBorder(null);
        selectedFilesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox.setVgrow(selectedFilesTable, Priority.ALWAYS);
        tagTiger.getSelection().getAvailableFiles().addListener((ListChangeListener.Change<? extends TaggableFile> change) -> {
            Platform.runLater(() -> selectedFilesTable.getItems().setAll(change.getList()));
        });
        tagTiger.getSelection().getSelectedFile().addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                int indexOfNewValue = selectedFilesTable.getItems().indexOf(newValue);
                if (indexOfNewValue != selectedFilesTable.getSelectionModel().getSelectedIndex()) {
                    List<Integer> removeIndices = new ArrayList<>(selectedFilesTable.getSelectionModel().getSelectedIndices());
                    removeIndices.remove(Integer.valueOf(indexOfNewValue));
                    selectedFilesTable.getSelectionModel().select(indexOfNewValue);
                    removeIndices.forEach(index -> selectedFilesTable.getSelectionModel().clearSelection(index.intValue()));
                }
            }
        });
        selectedFilesTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TaggableFile> change) -> {

            TaggableFile selectedFile = selectedFilesTable.getSelectionModel().getSelectedItem();
            if (selectedFile == null && selectedFilesTable.getSelectionModel().getSelectedIndex() > -1) {
                selectedFile = change.getList().get(selectedFilesTable.getSelectionModel().getSelectedIndex());
            }

            tagTiger.getSelection().updateSelectedFiles(change.getList());
            tagTiger.getSelection().updateSelectedFile(selectedFile, selectedFilesTable.getSelectionModel().getSelectedIndex());

        });

        Image flagIconImage = new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/flag-red.png"));
        TableColumn<TaggableFile, Boolean> changedColumn = new TableColumn<>();
        changedColumn.setCellValueFactory(p -> p.getValue().getChanged());
        changedColumn.setCellFactory(item -> {
            TableCell<TaggableFile, Boolean> tableCell = new TableCell<TaggableFile, Boolean>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        this.setGraphic(item.booleanValue() ? new Label("", new ImageView(flagIconImage)) : null);
                    }
                }
            };
            return tableCell;
        });
        changedColumn.setMinWidth(24);
        changedColumn.setMaxWidth(24);
        selectedFilesTable.getColumns().add(changedColumn);

        TableColumn<TaggableFile, String> fileNameColumn = new TableColumn<>(tagTiger.getLocalization().fileName());
        fileNameColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getFile().getName()));
        fileNameColumn.setMaxWidth(Double.MAX_VALUE);
        selectedFilesTable.getColumns().add(fileNameColumn);

        tagTiger.getJobExecutor().addListener(new JobListener() {

            @Override
            public void jobStarted(Job job) {
                Platform.runLater(() -> FileListPane.this.setDisable(true));
            }

            @Override
            public void jobCompleted(Job job, boolean otherJobsActive) {
                if (!otherJobsActive) {
                    Platform.runLater(() -> FileListPane.this.setDisable(false));
                }
            }

        });

        VBox.setVgrow(selectedFilesTable, Priority.ALWAYS);

        FileActionPane fileActionPane = new FileActionPane(tagTiger);
        fileActionPane.setPadding(new Insets(5, 5, 5, 5));

        this.getChildren().addAll(selectedFilesTable, fileActionPane);

    }

}