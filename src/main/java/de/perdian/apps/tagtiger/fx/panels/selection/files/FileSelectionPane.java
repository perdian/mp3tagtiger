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

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;

/**
 * Displays a list of selected files that are in use by the user
 *
 * @author Christian Robert
 */

public class FileSelectionPane extends BorderPane {

    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final Property<TaggableFile> selectedFile = new SimpleObjectProperty<>();
    private FileSelectionTableView fileSelectionTableView = null;

    public FileSelectionPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        FileSelectionTableView fileSelectionTableView = new FileSelectionTableView(localization);
        fileSelectionTableView.itemsProperty().bind(this.availableFilesProperty());
        fileSelectionTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileSelectionTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends TaggableFile> change) -> this.selectedFilesProperty().setAll(change.getList()));
        fileSelectionTableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.selectedFileProperty().setValue(newValue));
        this.setFileSelectionTableView(fileSelectionTableView);

        selection.currentFileProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                this.scrollTo(newValue);
            }
        });

        this.setTop(new FileSelectionTopButtonPane(fileSelectionTableView, selection, localization, jobExecutor));
        this.setCenter(fileSelectionTableView);
        this.setBottom(new FileSelectionBottomButtonPane(selection, localization, jobExecutor));

    }

    private void scrollTo(TaggableFile file) {
        TableViewSkin<?> tableViewSkin = (TableViewSkin<?>)this.getFileSelectionTableView().getSkin();
        VirtualFlow<?> virtualFlow = (VirtualFlow<?>)tableViewSkin.getChildren().get(1);
        int firstCellIndex = virtualFlow.getFirstVisibleCell().getIndex();
        int lastCellIndex = virtualFlow.getLastVisibleCell().getIndex();
        int fileIndex = this.getFileSelectionTableView().getItems().indexOf(file);
        if (fileIndex <= firstCellIndex) {
            this.getFileSelectionTableView().scrollTo(Math.max(0, fileIndex - 1));
        } else if (fileIndex > 0 && fileIndex >= lastCellIndex) {
            this.getFileSelectionTableView().scrollTo(fileIndex);
        }
    }

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    public Property<TaggableFile> selectedFileProperty() {
        return this.selectedFile;
    }

    private FileSelectionTableView getFileSelectionTableView() {
        return this.fileSelectionTableView;
    }
    private void setFileSelectionTableView(FileSelectionTableView fileSelectionTableView) {
        this.fileSelectionTableView = fileSelectionTableView;
    }

}