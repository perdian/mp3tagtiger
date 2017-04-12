/*
 * Copyright 2014-2017 Christian Robert
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

import java.util.Arrays;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.ChangeCurrentFileEventHandler;
import de.perdian.apps.tagtiger.fx.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.panels.editor.components.InformationEditorPane;
import de.perdian.apps.tagtiger.fx.support.EditorComponentBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EditorPane extends VBox {

    public EditorPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        EditorComponentBuilderFactory<TaggableFile> componentBuilderFactory = new EditorComponentBuilderFactory<>(selection.currentFileProperty());
        componentBuilderFactory.addControlCustomizer(component -> component.addEventHandler(KeyEvent.KEY_PRESSED, new ChangeCurrentFileEventHandler<>(selection.currentFileProperty(), selection.availableFilesProperty(), new ChangeCurrentFileEventHandler.KeyEventDirectionFunction())));
//        componentFactory.addControlCustomizer(component -> component.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.handleKeyPressedEvent(event, saveEventHandler)));
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(componentBuilderFactory.getDisabledProperty()));
//
        InformationEditorPane informationEditorPane = new InformationEditorPane(componentBuilderFactory, selection, localization);
//        informationEditorPane.currentFileProperty().bind(this.currentFileProperty());
//        informationEditorPane.selectedFilesProperty().bind(this.selectedFilesProperty());
//        informationEditorPane.availableFilesProperty().bind(this.availableFilesProperty());
//
//        CommonEditorTab commonEditorTab = new CommonEditorTab(componentFactory, localization);
//        commonEditorTab.currentFileProperty().bind(this.currentFileProperty());
//        commonEditorTab.selectedFilesProperty().bind(this.selectedFilesProperty());
//        commonEditorTab.availableFilesProperty().bind(this.availableFilesProperty());
//
//        ImagesEditorTab imagesEditorPane = new ImagesEditorTab(localization);
//        imagesEditorPane.currentFileProperty().bind(this.currentFileProperty());
//        imagesEditorPane.selectedFilesProperty().bind(this.selectedFilesProperty());
//
        TabPane taggingPane = new TabPane();
//        taggingPane.getTabs().addAll(commonEditorTab, imagesEditorPane);
        taggingPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane taggingWrapperPane = new TitledPane(localization.tags(), taggingPane);
        taggingWrapperPane.setCollapsible(false);
        taggingWrapperPane.setMaxHeight(Double.MAX_VALUE);
        taggingWrapperPane.setDisable(true);
        VBox.setVgrow(taggingWrapperPane, Priority.ALWAYS);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setSpacing(5);
        this.setMinWidth(400);
        this.setPrefWidth(600);
        this.getChildren().addAll(informationEditorPane, taggingWrapperPane);

        selection.currentFileProperty().addListener((o, oldValue, newValue) -> Arrays.asList(informationEditorPane, taggingWrapperPane).forEach(pane -> pane.setDisable(newValue == null)));
//        this.currentFileProperty().addListener((o, oldValue, newValue) -> imagesEditorPane.imagesProperty().set(newValue == null ? null : newValue.imagesProperty().getValue().getTagImages()));

    }

}
