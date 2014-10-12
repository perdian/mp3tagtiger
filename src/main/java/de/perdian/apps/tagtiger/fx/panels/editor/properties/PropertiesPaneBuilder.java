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
package de.perdian.apps.tagtiger.fx.panels.editor.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

public class PropertiesPaneBuilder {

    private SortedMap<Integer, List<PropertiesPaneItem>> items = new TreeMap<>();
    private Selection selection = null;

    public PropertiesPaneBuilder(Selection selection) {
        this.setSelection(selection);
    }

    public void add(int row, int colspan, Double componentWidth, String title, Function<TaggableFile, Property<String>> propertyFunction, Supplier<Control> controlFactory, Function<Control, Property<String>> controlTextPropertyFunction, boolean addCopyToAll) {
        List<PropertiesPaneItem> targetItems = this.getItems().get(Integer.valueOf(row));
        if (targetItems == null) {
            targetItems = new ArrayList<>();
            this.getItems().put(Integer.valueOf(row), targetItems);
        }
        targetItems.add(new PropertiesPaneItem(colspan, componentWidth, title, propertyFunction, controlFactory, controlTextPropertyFunction, addCopyToAll));
    }

    public void applyTo(GridPane pane) {
        int rowIndex = 0;
        int controlIndex = 0;
        List<Control> controlList = new ArrayList<>();
        for (Map.Entry<Integer, List<PropertiesPaneItem>> itemEntry : this.getItems().entrySet()) {

            int columnIndex = 0;

            for (PropertiesPaneItem paneItem : itemEntry.getValue()) {

                Label titleLabel = new Label(paneItem.getTitle() + ":");
                titleLabel.setAlignment(Pos.CENTER_RIGHT);
                titleLabel.setMaxWidth(Double.MAX_VALUE);

                HBox wrapperComponent = new HBox();
                wrapperComponent.setSpacing(2);

                int editorControlIndex = controlIndex;
                Control editorControl = paneItem.getControlFactory().get();
                controlList.add(editorControl);
                editorControl.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.onKeyPress(event, editorControlIndex, controlList));

                if (paneItem.getWidth() != null) {
                    editorControl.setPrefWidth(paneItem.getWidth().doubleValue());
                } else {
                    HBox.setHgrow(editorControl, Priority.ALWAYS);
                    GridPane.setHgrow(wrapperComponent, Priority.ALWAYS);
                }
                wrapperComponent.getChildren().add(editorControl);

                if (paneItem.isAddCopyToAll()) {
                    Button copyToAllButton = new Button("COPY");
                    copyToAllButton.setOnAction(new PropertiesCopyEventHandler(paneItem.getControlTextPropertyFunction().apply(editorControl), paneItem.getPropertyFunction(), this.getSelection()));
                    wrapperComponent.getChildren().add(copyToAllButton);
                }

                int wrapperComponentColspan = paneItem.getColspan() == 1 ? 1 : (paneItem.getColspan() + 1);
                pane.add(titleLabel, columnIndex, rowIndex, 1, 1);
                pane.add(wrapperComponent, columnIndex + 1, rowIndex, wrapperComponentColspan, 1);

                columnIndex += (1 + paneItem.getColspan());
                controlIndex += 1;

            }

            // Move to next row
            rowIndex++;

        }
    }

    private void onKeyPress(KeyEvent event, int controlIndex, List<Control> controlList) {
        if (event.getCode() == KeyCode.UP && controlIndex > 0) {
            controlList.get(controlIndex - 1).requestFocus();
        } else if (event.getCode() == KeyCode.DOWN && controlIndex < controlList.size() - 1) {
            controlList.get(controlIndex + 1).requestFocus();
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    SortedMap<Integer, List<PropertiesPaneItem>> getItems() {
        return this.items;
    }
    void setItems(SortedMap<Integer, List<PropertiesPaneItem>> items) {
        this.items = items;
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

}