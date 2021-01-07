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
package de.perdian.apps.tagtiger.fx.components.actions;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger.fx.components.tools.filenames.ComputeFilenamesFromTagsActionEventHandler;
import de.perdian.apps.tagtiger.fx.components.tools.filenames.ExtractTagsFromFilenamesActionEventHandler;
import de.perdian.apps.tagtiger.fx.model.Selection;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ActionsPane extends TitledPane {

    public ActionsPane(Selection selection) {

        Button saveChangedFilesButton = new Button("Save changed files", new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        saveChangedFilesButton.setOnAction(event -> selection.saveDirtySongs());
        saveChangedFilesButton.disableProperty().bind(selection.dirtyProperty().not());

        Button computeFilenamesFromTagsButton = new Button("Compute file names from tags", new FontAwesomeIconView(FontAwesomeIcon.LIST));
        computeFilenamesFromTagsButton.setOnAction(new ComputeFilenamesFromTagsActionEventHandler(selection.getSelectedFiles()));
        computeFilenamesFromTagsButton.disableProperty().bind(Bindings.isEmpty(selection.getSelectedFiles()));
        Button extractTagsFromFilenamesButton = new Button("Extract tags from file names", new FontAwesomeIconView(FontAwesomeIcon.LIST));
        extractTagsFromFilenamesButton.setOnAction(new ExtractTagsFromFilenamesActionEventHandler(selection.getSelectedFiles()));
        extractTagsFromFilenamesButton.disableProperty().bind(Bindings.isEmpty(selection.getSelectedFiles()));

        HBox leftButtons = new HBox(5, computeFilenamesFromTagsButton, extractTagsFromFilenamesButton);
        HBox rightButtons = new HBox(saveChangedFilesButton);

        BorderPane internalPane = new BorderPane();
        internalPane.setLeft(leftButtons);
        internalPane.setRight(rightButtons);
        internalPane.setPadding(new Insets(10, 10, 10, 10));

        this.setText("Actions");
        this.setContent(internalPane);
        this.setCollapsible(false);
        this.disableProperty().bind(selection.busyProperty());

    }

}
