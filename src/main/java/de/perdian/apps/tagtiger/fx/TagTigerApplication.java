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
package de.perdian.apps.tagtiger.fx;

import java.io.File;
import java.util.Collections;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.jobs.JobExecutor;
import de.perdian.apps.tagtiger.business.framework.jobs.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.messages.MessageDistributor;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesLookup;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.impl.jobs.DirectorySelectJob;
import de.perdian.apps.tagtiger.business.impl.jobs.SaveChangedFilesInSelectionJob;
import de.perdian.apps.tagtiger.fx.components.status.StatusPane;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorPane;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionPane;

/**
 * The main JavaFX application that makes up the TagTiger application
 *
 * @author Christian Robert
 */

public class TagTigerApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(TagTigerApplication.class);

    public static void main(String[] args) {
        log.info("Starting application");
        Application.launch(TagTigerApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Creating TagTiger business objects");
        JobExecutor jobExecutor = new JobExecutor();
        MessageDistributor messageDistributor = new MessageDistributor(Collections.singleton(new TagTigerMessageConsumer()));
        PreferencesLookup preferences = new PreferencesLookup();
        Localization localization = new Localization() {};
        Selection selection = new Selection();
        selection.currentDirectoryProperty().addListener((o, oldValue, newValue) -> jobExecutor.executeJob(new DirectorySelectJob(newValue, selection, localization, messageDistributor)));
        selection.currentDirectoryProperty().addListener((o, oldValue, newValue) -> preferences.setString(PreferencesKey.CURRENT_DIRECTORY, newValue == null ? null : newValue.getAbsolutePath()));

        String currentDirectoryValue = preferences.getString(PreferencesKey.CURRENT_DIRECTORY, null);
        File currentDirectory = currentDirectoryValue == null ? null : new File(currentDirectoryValue);
        if (currentDirectory != null && currentDirectory.exists() && currentDirectory.isDirectory()) {
            selection.currentDirectoryProperty().set(currentDirectory);
        }

        log.info("Creating JavaFX UI");
        SelectionPane selectionPane = new SelectionPane(selection, localization);
        selectionPane.setMinWidth(250d);
        selectionPane.setPrefWidth(250d);
        TitledPane fileSelectionWrapperPane = new TitledPane(localization.selectFiles(), selectionPane);
        fileSelectionWrapperPane.setMaxHeight(Double.MAX_VALUE);
        fileSelectionWrapperPane.setCollapsible(false);
        fileSelectionWrapperPane.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(fileSelectionWrapperPane, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(selectionPane.listDisableProperty()));

        EditorPane editorPane = new EditorPane(localization);
        editorPane.setMinWidth(400d);
        editorPane.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(editorPane, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(editorPane.disableProperty()));
        Bindings.bindBidirectional(editorPane.currentFileProperty(), selection.currentFileProperty());
        Bindings.bindContent(editorPane.availableFilesProperty(), selection.availableFilesProperty());
        Bindings.bindContent(editorPane.selectedFilesProperty(), selection.selectedFilesProperty());

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(fileSelectionWrapperPane);
        splitPane.getItems().add(editorPane);
        splitPane.setDividerPositions(0.25d);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(jobExecutor, localization);
        statusPane.setPadding(new Insets(2.5, 5, 2.5, 5));

        TagTigerMenuBar menuBar = new TagTigerMenuBar(localization);

        TagTigerToolBar toolBar = new TagTigerToolBar(localization);
        toolBar.changedFilesProperty().bind(selection.changedFilesProperty());
        toolBar.setOnSaveAction(event -> jobExecutor.executeJob(new SaveChangedFilesInSelectionJob(selection, localization)));

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/application.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/64/application.png")));
        primaryStage.setScene(new Scene(new VBox(menuBar, toolBar, splitPane, statusPane)));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle(localization.applicationTitle());
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();

        log.info("Application start completed");

    }

}