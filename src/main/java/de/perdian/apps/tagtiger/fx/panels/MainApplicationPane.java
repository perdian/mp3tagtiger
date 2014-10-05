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
package de.perdian.apps.tagtiger.fx.panels;

import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorPane;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionPane;
import de.perdian.apps.tagtiger.fx.panels.status.StatusPane;

/**
 * The central pane in which the main components of the application are being
 * layed out
 *
 * @author Christian Robert
 */

public class MainApplicationPane extends VBox {

    public MainApplicationPane(TagTiger tagTiger) {

        SelectionPane selectionPane = new SelectionPane(tagTiger);
        selectionPane.setMinWidth(400d);
        TitledPane fileSelectionWrapperPanel = new TitledPane(tagTiger.getLocalization().selectFiles(), selectionPane);
        fileSelectionWrapperPanel.setMaxHeight(Double.MAX_VALUE);
        fileSelectionWrapperPanel.setCollapsible(false);
        fileSelectionWrapperPanel.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(fileSelectionWrapperPanel, Priority.ALWAYS);

        EditorPane editorPane = new EditorPane(tagTiger);
        editorPane.setMinWidth(400d);
        VBox.setVgrow(editorPane, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(fileSelectionWrapperPanel);
        splitPane.getItems().add(editorPane);
        splitPane.setDividerPositions(0.3f);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(tagTiger);
        statusPane.setPadding(new Insets(2.5, 5, 2.5, 5));

        this.getChildren().add(splitPane);
        this.getChildren().add(statusPane);

    }

}