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

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.components.EditorComponentFactory;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.CopyTracksCountGroupAction;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.CopyValuesGroupAction;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.GenerateTrackIndexGroupAction;

class EditorTaggingCommonPane extends GridPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private EditorComponentFactory<TaggableFile> componentFactory = null;
    private Localization localization = null;

    EditorTaggingCommonPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {
        this.setHgap(5);
        this.setComponentFactory(componentFactory);
        this.setLocalization(localization);
    }

    void initialize() {

        this.addLabel(this.getLocalization().title(), 0, 0, 4, 1);
        this.addTextFieldControl(TaggableFile::titleProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::titleProperty), 0, 1, 4, 1);
        this.addLabel(this.getLocalization().artist(), 0, 2, 4, 1);
        this.addTextFieldControl(TaggableFile::artistProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::artistProperty), 0, 3, 4, 1);
        this.addLabel(this.getLocalization().album(), 0, 4, 3, 1);
        this.addLabel(this.getLocalization().year(), 3, 4, 1, 1);
        this.addTextFieldControl(TaggableFile::albumProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::albumProperty),0, 5, 3, 1);
        this.addNumericTextFieldControl(TaggableFile::yearProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::yearProperty), 3, 5, 1, 1);
        this.addLabel(this.getLocalization().track(), 0, 6, 1, 1);
        this.addLabel(this.getLocalization().tracks(), 1, 6, 1, 1);
        this.addLabel(this.getLocalization().disc(), 2, 6, 1, 1);
        this.addLabel(this.getLocalization().discs(), 3, 6, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::trackNumberProperty, new GenerateTrackIndexGroupAction("icons/16/create.png", this.getLocalization().enumerateTracks(), TaggableFile::trackNumberProperty), 0, 7, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::tracksTotalProperty, new CopyTracksCountGroupAction("icons/16/create.png", this.getLocalization().countTracks(), TaggableFile::tracksTotalProperty), 1, 7, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::discNumberProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::discNumberProperty), 2, 7, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::discsTotalProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::discsTotalProperty), 3, 7, 1, 1);
        this.addLabel(this.getLocalization().genre(), 0, 8, 4, 1);
        this.addSelectBoxControl(TaggableFile::genreProperty, GenreTypes.getInstanceOf().getAlphabeticalValueList(), new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::genreProperty), 0, 9, 4, 1);
        this.addLabel(this.getLocalization().comment(), 0, 10, 4, 1);
        this.addTextFieldControl(TaggableFile::commentProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::commentProperty), 0, 11, 4, 1);
        this.addLabel(this.getLocalization().composer(), 0, 12, 4, 1);
        this.addTextFieldControl(TaggableFile::composerProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::composerProperty), 0, 13, 4, 1);
    }

    private void addLabel(String text, int x, int y, int width, int height) {
        this.add(new Label(text), x, y, width, height);
    }

    private void addSelectBoxControl(Function<TaggableFile, Property<String>> propertyFunction, List<String> values, EditorGroupAction<String> groupAction, int x, int y, int width, int height) {
        this.addControl(this.getComponentFactory().createSelectBox(propertyFunction, values), groupAction, x, y, width, height);
    }

    private void addTextFieldControl(Function<TaggableFile, Property<String>> propertyFunction, EditorGroupAction<String> groupAction, int x, int y, int width, int height) {
        this.addControl(this.getComponentFactory().createTextField(propertyFunction), groupAction,x, y, width, height);
    }

    private void addNumericTextFieldControl(Function<TaggableFile, Property<String>> propertyFunction, EditorGroupAction<String> groupAction, int x, int y, int width, int height) {
        TextField textField = this.getComponentFactory().createTextField(propertyFunction);
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d+")) {
                event.consume();
            }
        });
        this.addControl(textField, groupAction, x, y, width, height);
    }

    private void addControl(Control control, EditorGroupAction<?> groupAction, int x, int y, int width, int height) {

        HBox resultControl = new HBox();
        resultControl.setSpacing(2);
        resultControl.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHgrow(resultControl, Priority.ALWAYS);

        resultControl.getChildren().add(control);
        HBox.setHgrow(control, Priority.ALWAYS);

        if (groupAction != null) {
           Button groupActionButton = new Button(null, new ImageView(new Image(EditorTaggingCommonPane.class.getClassLoader().getResourceAsStream(groupAction.getIconLocation()))));
           groupActionButton.setTooltip(new Tooltip(groupAction.getTooltipText()));
           groupActionButton.setOnAction(event -> {
               groupAction.execute(this.currentFileProperty().get(), this.selectedFilesProperty().get());
           });
           this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> {
               groupActionButton.setDisable(change.getList().size() < groupAction.getMinimumFiles());
           });
           resultControl.getChildren().add(groupActionButton);
        }

        this.add(resultControl, x, y, width, height);

    }

    //    private Node createGroupActionControl(TagHandler tag, Localization localization) {
//        switch (tag.getGroupAction()) {
//
//            case COPY:
//                Button copyButton = new Button(null, new ImageView(new Image(EditorTaggingCommonPane.class.getClassLoader().getResourceAsStream("icons/16/copy.png"))));
//                copyButton.setOnAction(event -> this.handleGroupAction(tag));
//                copyButton.setTooltip(new Tooltip(localization.copyToAllOtherSelectedFiles()));
//                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> {
//                    copyButton.setDisable(!change.getList().stream().filter(file -> !Objects.equals(this.currentFileProperty().get(), file)).findAny().isPresent());
//                });
//                return copyButton;
//
//            case GENERATE_FROM_POSITION:
//                Button generateButton = new Button(null, new ImageView(new Image(EditorTaggingCommonPane.class.getClassLoader().getResourceAsStream("icons/16/create.png"))));
//                generateButton.setOnAction(event -> this.handleGroupAction(tag));
//                generateButton.setTooltip(new Tooltip(localization.enumerateTracks()));
//                this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> generateButton.setDisable(change.getList().isEmpty()));
//                return generateButton;
//
//            default:
//                return null;
//
//        }
//    }
//
//    private void handleGroupAction(TagHandler tag) {
//        tag.getGroupAction().apply(this.currentFileProperty().get(), tag, this.selectedFilesProperty().get());
//    }


    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    private EditorComponentFactory<TaggableFile> getComponentFactory() {
        return this.componentFactory;
    }
    private void setComponentFactory(EditorComponentFactory<TaggableFile> componentFactory) {
        this.componentFactory = componentFactory;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}