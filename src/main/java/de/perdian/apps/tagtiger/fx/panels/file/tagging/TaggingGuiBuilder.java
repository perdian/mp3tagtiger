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
package de.perdian.apps.tagtiger.fx.panels.file.tagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.file.FilePropertyControlFactory;

public class TaggingGuiBuilder {

    private SortedMap<Integer, List<TaggingGuiProperty>> items = null;
    private List<Control> controls = null;
    private FilePropertyControlFactory propertyFactory = null;
    private Selection selection = null;
    private Localization localization = null;

    public TaggingGuiBuilder(FilePropertyControlFactory propertyFactory, Selection selection, Localization localization) {
        this.setItems(new TreeMap<>());
        this.setControls(new ArrayList<>());
        this.setPropertyFactory(propertyFactory);
        this.setSelection(selection);
        this.setLocalization(localization);
    }

    public void addTextfield(int row, int colspan, String title, FieldKey fieldKey, TaggingAction action) {

        List<Control> controls = this.getControls();
        int controlIndex = controls.size();

        TextField textField = this.getPropertyFactory().createTextField(file -> file.getTag(fieldKey));
        textField.setPrefWidth(0d);
        textField.setMinWidth(0d);
        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.handleTextFieldKeyPress(event, controlIndex, controls));
        controls.add(textField);

        TaggingGuiProperty item = new TaggingGuiProperty();
        item.setAction(action);
        item.setColspan(colspan);
        item.setControl(textField);
        item.setControlProperty(textField.textProperty());
        item.setFieldKey(fieldKey);
        item.setTitle(title);

        this.ensureItems(Integer.valueOf(row)).add(item);

    }

    public void applyTo(GridPane pane) {
        int rowIndex = 0;
        for (Map.Entry<Integer, List<TaggingGuiProperty>> itemEntry : this.getItems().entrySet()) {

            int columnIndex = 0;

            for (TaggingGuiProperty paneItem : itemEntry.getValue()) {

                Label titleLabel = new Label(paneItem.getTitle() + ":");
                titleLabel.setAlignment(Pos.CENTER_RIGHT);
                titleLabel.setMaxWidth(Double.MAX_VALUE);
                titleLabel.setMinWidth(Region.USE_PREF_SIZE);

                HBox wrapperComponent = new HBox();
                wrapperComponent.setSpacing(2);

                Control editorControl = paneItem.getControl();

                HBox.setHgrow(editorControl, Priority.ALWAYS);
                GridPane.setHgrow(wrapperComponent, Priority.ALWAYS);
                wrapperComponent.getChildren().add(editorControl);

                Button actionButton = this.createActionButton(paneItem);
                if (actionButton != null) {
                    wrapperComponent.getChildren().add(actionButton);
                }

                int wrapperComponentColspan = (paneItem.getColspan() * 2) - 1;

                pane.add(titleLabel, columnIndex, rowIndex, 1, 1);
                pane.add(wrapperComponent, columnIndex + 1, rowIndex, wrapperComponentColspan, 1);

                columnIndex += wrapperComponentColspan + 1;

            }

            // Move to next row
            rowIndex++;

        }
    }

    private Button createActionButton(TaggingGuiProperty property) {
        TaggingAction action = property.getAction();
        if (action == null) {
            return null;
        } else {
            Button actionButton = new Button();
            actionButton.setGraphic(new ImageView(new Image(TaggingGuiBuilder.class.getClassLoader().getResourceAsStream(action.resolveIconLocation()))));
            actionButton.setTooltip(new Tooltip(action.resolveTooltipText(this.getLocalization())));
            actionButton.setOnAction(this.createActionButtonOnActionListener(property));
            return actionButton;
        }
    }

    private EventHandler<ActionEvent> createActionButtonOnActionListener(TaggingGuiProperty property) {
        switch (property.getAction()) {
            case COPY_TO_OTHERS:
                return event -> this.handleActionCopyToOtherFiles(event, property.getControlProperty(), property.getFieldKey());
            case CREATE_TRACK_LIST:
                return event -> this.handleActionCreateTrackList(event, property.getControlProperty(), property.getFieldKey());
            default:
                return null;
        }
    }

    // -------------------------------------------------------------------------
    // --- Event handling methods ----------------------------------------------
    // -------------------------------------------------------------------------

    private void handleTextFieldKeyPress(KeyEvent event, int controlIndex, List<Control> controlList) {
        if (event.getCode() == KeyCode.UP && controlIndex > 0) {
            controlList.get(event.isShiftDown() ? 0 : (controlIndex - 1)).requestFocus();
        } else if (event.getCode() == KeyCode.ENTER && event.isShiftDown() && controlIndex > 0) {
            controlList.get(controlIndex - 1).requestFocus();
        } else if (event.getCode() == KeyCode.ENTER && !event.isShiftDown() && controlIndex < controlList.size() - 1) {
            controlList.get(controlIndex + 1).requestFocus();
        } else if (event.getCode() == KeyCode.DOWN && controlIndex < controlList.size() - 1) {
            controlList.get(event.isShiftDown() ? (controlList.size() - 1) : (controlIndex + 1)).requestFocus();
        }
    }

    private void handleActionCopyToOtherFiles(ActionEvent event, StringProperty sourceProperty, FieldKey fieldKey) {
        List<TaggableFile> targetList = this.getSelection().getSelectedFiles();
        List<TaggableFile> useList = targetList.isEmpty() ? this.getSelection().getAvailableFiles() : targetList;
        String sourceValue = sourceProperty.getValue();
        for (TaggableFile file : useList) {
            StringProperty targetProperty = file.getTag(fieldKey);
            if (!Objects.equals(sourceValue, targetProperty.getValue())) {
                targetProperty.setValue(sourceValue);
            }
        }
    }

    private void handleActionCreateTrackList(ActionEvent event, StringProperty controlProperty, FieldKey fieldKey) {
        List<TaggableFile> targetList = this.getSelection().getSelectedFiles();
        List<TaggableFile> useList = targetList.isEmpty() ? this.getSelection().getAvailableFiles() : targetList;
        for (int i = 0; i < useList.size(); i++) {
            useList.get(i).getTag(fieldKey).setValue(String.valueOf(i + 1));
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private List<TaggingGuiProperty> ensureItems(Integer row) {
        List<TaggingGuiProperty> itemsForRow = this.getItems().get(row);
        if (itemsForRow == null) {
            itemsForRow = new ArrayList<>();
            this.getItems().put(Integer.valueOf(row), itemsForRow);
        }
        return itemsForRow;
    }
    SortedMap<Integer, List<TaggingGuiProperty>> getItems() {
        return this.items;
    }
    void setItems(SortedMap<Integer, List<TaggingGuiProperty>> items) {
        this.items = items;
    }

    private FilePropertyControlFactory getPropertyFactory() {
        return this.propertyFactory;
    }
    private void setPropertyFactory(FilePropertyControlFactory propertyFactory) {
        this.propertyFactory = propertyFactory;
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

    private List<Control> getControls() {
        return this.controls;
    }
    private void setControls(List<Control> controls) {
        this.controls = controls;
    }

}