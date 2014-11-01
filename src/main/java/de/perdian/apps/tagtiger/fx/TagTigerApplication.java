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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.jobs.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.messages.MessageDistributor;
import de.perdian.apps.tagtiger.core.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.core.preferences.PreferencesLookup;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.selection.DirectorySelectJob;
import de.perdian.apps.tagtiger.fx.handlers.selection.SaveChangedFilesInSelectionJob;
import de.perdian.apps.tagtiger.fx.panels.directories.DirectorySelectionPane;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorPane;
import de.perdian.apps.tagtiger.fx.panels.files.FileSelectionPane;
import de.perdian.apps.tagtiger.fx.panels.status.StatusPane;

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
        Parent mainPanel = this.createMainPanel(selection, jobExecutor, localization);

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/application.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/64/application.png")));
        primaryStage.setScene(new Scene(mainPanel));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle(localization.applicationTitle());
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();

        log.info("Application start completed");

    }

    private Parent createMainPanel(Selection selection, JobExecutor jobExecutor, Localization localization) {

        TagTigerMenuBar menuBar = this.createMenuBar(selection, jobExecutor, localization);
        TagTigerToolBar toolBar = this.createToolBar(selection, jobExecutor, localization);
        StatusPane statusPane = this.createStatusPane(selection, jobExecutor, localization);

        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.getItems().add(this.createSelectionPane(selection, jobExecutor, localization));
        mainSplitPane.getItems().add(this.createEditorPane(selection, jobExecutor, localization));
        mainSplitPane.setDividerPositions(0.25d);
        VBox.setVgrow(mainSplitPane, Priority.ALWAYS);

        return new VBox(menuBar, toolBar, mainSplitPane, statusPane);

    }

    private TagTigerMenuBar createMenuBar(Selection selection, JobExecutor jobExecutor, Localization localization) {
        TagTigerMenuBar menuBar = new TagTigerMenuBar(localization);
        Bindings.bindBidirectional(menuBar.currentFileProperty(), selection.currentFileProperty());
        Bindings.bindContent(menuBar.availableFilesProperty(), selection.availableFilesProperty());
        Bindings.bindContent(menuBar.selectedFilesProperty(), selection.selectedFilesProperty());
        return menuBar;
    }

    private TagTigerToolBar createToolBar(Selection selection, JobExecutor jobExecutor, Localization localization) {
        TagTigerToolBar toolBar = new TagTigerToolBar(localization);
        toolBar.changedFilesProperty().bind(selection.changedFilesProperty());
        toolBar.setOnSaveAction(event -> jobExecutor.executeJob(new SaveChangedFilesInSelectionJob(selection, localization)));
        Bindings.bindBidirectional(toolBar.currentFileProperty(), selection.currentFileProperty());
        Bindings.bindContent(toolBar.availableFilesProperty(), selection.availableFilesProperty());
        return toolBar;
    }

    private StatusPane createStatusPane(Selection selection, JobExecutor jobExecutor, Localization localization) {
        StatusPane statusPane = new StatusPane(localization);
        statusPane.setOnCancelAction(event -> jobExecutor.cancelCurrentJob());
        statusPane.setPadding(new Insets(2.5, 5, 2.5, 5));
        jobExecutor.addListener(statusPane);
        return statusPane;
    }

    private Parent createSelectionPane(Selection selection, JobExecutor jobExecutor, Localization localization) {

        TextField directoryField = new TextField();
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));

        DirectorySelectionPane directorySelectionPane = new DirectorySelectionPane(localization);
        directorySelectionPane.selectedDirectoryProperty().bindBidirectional(selection.currentDirectoryProperty());

        FileSelectionPane fileSelectionPane = new FileSelectionPane(localization);
        fileSelectionPane.availableFilesProperty().bindBidirectional(selection.availableFilesProperty());
        fileSelectionPane.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> selection.selectedFilesProperty().setAll(change.getList()));
        fileSelectionPane.selectedFileProperty().addListener((o, oldValue, newValue) -> selection.currentFileProperty().set(newValue));

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(directorySelectionPane);
        splitPane.getItems().add(fileSelectionPane);
        splitPane.setDividerPositions(0.4d);

        BorderPane selectionPane = new BorderPane();
        selectionPane.setTop(directoryFieldWrapper);
        selectionPane.setCenter(splitPane);
        selectionPane.setPadding(new Insets(5, 5, 5, 5));
        selectionPane.setMinWidth(250d);
        selectionPane.setPrefWidth(250d);

        selection.currentFileProperty().addListener((o, oldValue, newValue) -> fileSelectionPane.selectedFileProperty().set(newValue));

        // Add listeners to connect the GUI components with the underlying
        // data structures
        directoryField.setOnAction(event -> {
            String directoryValue = ((TextField)event.getSource()).getText();
            directorySelectionPane.selectedDirectoryProperty().set(new File(directoryValue));
        });
        directoryField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> directoryField.selectAll());
            }
        });
        directorySelectionPane.selectedDirectoryProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                directoryField.setText(newValue == null ? "" : newValue.getAbsolutePath());
                if (directoryField.isFocused()) {
                    directoryField.selectAll();
                }
            });
        });

        TitledPane fileSelectionWrapperPane = new TitledPane(localization.selectFiles(), selectionPane);
        fileSelectionWrapperPane.setMaxHeight(Double.MAX_VALUE);
        fileSelectionWrapperPane.setCollapsible(false);
        fileSelectionWrapperPane.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(fileSelectionWrapperPane, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(fileSelectionPane.disableProperty()));
        return selectionPane;

    }

    private Parent createEditorPane(Selection selection, JobExecutor jobExecutor, Localization localization) {
        EditorPane editorPane = new EditorPane(localization);
        editorPane.setMinWidth(400d);
        editorPane.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(editorPane, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(editorPane.disableProperty()));
        Bindings.bindBidirectional(editorPane.currentFileProperty(), selection.currentFileProperty());
        Bindings.bindContent(editorPane.availableFilesProperty(), selection.availableFilesProperty());
        Bindings.bindContent(editorPane.selectedFilesProperty(), selection.selectedFilesProperty());
        return editorPane;
    }

}