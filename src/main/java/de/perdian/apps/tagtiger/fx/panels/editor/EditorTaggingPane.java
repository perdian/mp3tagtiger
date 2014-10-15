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
package de.perdian.apps.tagtiger.fx.panels.editor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileTag;
import de.perdian.apps.tagtiger.fx.components.EditorComponentFactory;

class EditorTaggingPane extends TabPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    EditorTaggingPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        GridPane commonPane = new GridPane();
        commonPane.setHgap(5);
        commonPane.setPadding(new Insets(5, 5, 5, 5));
        commonPane.add(new Label(localization.title()), 0, 0, 6, 1);
        commonPane.add(this.createTextFieldControl(componentFactory, TaggableFileTag.TITLE, localization), 0, 1, 6, 1);
        commonPane.add(new Label(localization.artist()), 0, 2, 6, 1);
        commonPane.add(this.createTextFieldControl(componentFactory, TaggableFileTag.ARTIST, localization), 0, 3, 6, 1);
        commonPane.add(new Label(localization.album()), 0, 4, 5, 1);
        commonPane.add(this.createTextFieldControl(componentFactory, TaggableFileTag.ALBUM, localization), 0, 5, 5, 1);
        commonPane.add(new Label(localization.year()), 5, 4, 1, 1);
        commonPane.add(this.createTextFieldControl(componentFactory, TaggableFileTag.YEAR, localization), 5, 5, 1, 1);
        commonPane.add(new Label(localization.track()), 5, 6, 1, 1);
        commonPane.add(this.createTextFieldControl(componentFactory, TaggableFileTag.TRACK_NO, localization), 5, 7, 1, 1);

        Tab commonTab = new Tab(localization.common());
        commonTab.setContent(commonPane);
        commonTab.setClosable(false);

        GridPane imagesPane = new GridPane();
        Tab imagesTab = new Tab(localization.images());
        imagesTab.setContent(imagesPane);
        imagesTab.setClosable(false);

        this.getTabs().addAll(commonTab, imagesTab);

    }

    private Node createTextFieldControl(EditorComponentFactory<TaggableFile> componentFactory, TaggableFileTag tag, Localization localization) {
        return this.createEnhancedControl(componentFactory.createTextField(file -> file.getTag(tag)), tag, localization);
    }

    private Node createEnhancedControl(Control control, TaggableFileTag tag, Localization localization) {

        HBox resultControl = new HBox();
        resultControl.setSpacing(2);
        resultControl.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHgrow(resultControl, Priority.ALWAYS);

        resultControl.getChildren().add(control);
        HBox.setHgrow(control, Priority.ALWAYS);

        Node groupActionControl = this.createGroupActionControl(tag, localization);
        if (groupActionControl != null) {
            resultControl.getChildren().add(groupActionControl);
        }

        return resultControl;

    }

    private Node createGroupActionControl(TaggableFileTag tag, Localization localization) {
        switch (tag.getGroupAction()) {

            case COPY:
                Button copyButton = new Button(null, new ImageView(new Image(EditorTaggingPane.class.getClassLoader().getResourceAsStream("icons/16/copy.png"))));
                copyButton.setOnAction(event -> this.handleCopyValue(tag));
                copyButton.setTooltip(new Tooltip(localization.copyToAllOtherSelectedFiles()));
                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> {
                    copyButton.setDisable(!change.getList().stream().filter(file -> !Objects.equals(this.currentFileProperty().get(), file)).findAny().isPresent());
                });
                return copyButton;

            case GENERATE_FROM_POSITION:
                Button generateButton = new Button(null, new ImageView(new Image(EditorTaggingPane.class.getClassLoader().getResourceAsStream("icons/16/create.png"))));
                generateButton.setOnAction(event -> this.handleGenerateValue(tag));
                generateButton.setTooltip(new Tooltip(localization.enumerateTracks()));
                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> generateButton.setDisable(change.getList().isEmpty()));
                return generateButton;

            default:
                return null;

        }
    }

    private void handleCopyValue(TaggableFileTag tag) {
        TaggableFile currentFile = this.currentFileProperty().get();
        Object currentValue = currentFile.getTag(tag).getValue();
        this.selectedFilesProperty().stream().filter(file -> !file.equals(currentFile)).forEach(file -> file.getTag(tag).setValue(currentValue));
    }

    private void handleGenerateValue(TaggableFileTag tag) {
        AtomicInteger counter = new AtomicInteger(1);
        this.selectedFilesProperty().stream().forEach(file -> file.getTag(tag).setValue(String.valueOf(counter.getAndIncrement())));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}