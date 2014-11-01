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
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.CopyTracksCountGroupAction;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.CopyValuesGroupAction;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.GenerateTrackIndexGroupAction;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;

class EditorTaggingCommonPane extends GridPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private EditorComponentFactory<TaggableFile> componentFactory = null;
    private Localization localization = null;

    EditorTaggingCommonPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {
        this.setHgap(5);
        this.setVgap(5);
        this.setComponentFactory(componentFactory);
        this.setLocalization(localization);
    }

    void initialize() {

        this.addLabel(this.getLocalization().title(), 0, 0, 1, 1);
        this.addTextFieldControl(TaggableFile::titleProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::titleProperty), 1, 0, 5, 1);

        this.addLabel(this.getLocalization().artist(), 0, 1, 1, 1);
        this.addTextFieldControl(TaggableFile::artistProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::artistProperty), 1, 1, 5, 1);

        this.addLabel(this.getLocalization().album(), 0, 2, 1, 1);
        this.addTextFieldControl(TaggableFile::albumProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::albumProperty), 1, 2, 3, 1);
        this.addLabel(this.getLocalization().disc(), 4, 2, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::discNumberProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::discNumberProperty), 5, 2, 1, 1);

        this.addLabel(this.getLocalization().year(), 0, 3, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::yearProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::yearProperty), 1, 3, 1, 1);
        this.addLabel(this.getLocalization().track(), 2, 3, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::trackNumberProperty, new GenerateTrackIndexGroupAction("icons/16/create.png", this.getLocalization().enumerateTracks(), TaggableFile::trackNumberProperty), 3, 3, 1, 1);
        this.addLabel(this.getLocalization().tracks(), 4, 3, 1, 1);
        this.addNumericTextFieldControl(TaggableFile::tracksTotalProperty, new CopyTracksCountGroupAction("icons/16/create.png", this.getLocalization().countTracks(), TaggableFile::tracksTotalProperty), 5, 3, 1, 1);

        this.addLabel(this.getLocalization().genre(), 0, 4, 1, 1);
        this.addSelectBoxControl(TaggableFile::genreProperty, GenreTypes.getInstanceOf().getAlphabeticalValueList(), new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::genreProperty), 1, 4, 5, 1);

        this.addLabel(this.getLocalization().comment(), 0, 5, 1, 1);
        this.addTextFieldControl(TaggableFile::commentProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::commentProperty), 1, 5, 5, 1);

        this.addLabel(this.getLocalization().composer(), 0, 6, 1, 1);
        this.addTextFieldControl(TaggableFile::composerProperty, new CopyValuesGroupAction<>("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), TaggableFile::composerProperty), 1, 6, 5, 1);

    }

    private void addLabel(String text, int x, int y, int width, int height) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER_LEFT);
        this.add(label, x, y, width, height);
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
        this.add(EditorPaneHelper.createControlWrapper(control, groupAction, this.currentFileProperty(), this.selectedFilesProperty()), x, y, width, height);
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