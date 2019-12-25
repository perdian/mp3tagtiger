/*
 * Copyright 2014-2017 Christian Seifert
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
package de.perdian.apps.tagtiger;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.core.preferences.PreferencesLookup;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.editor.EditorPane;
import de.perdian.apps.tagtiger.fx.modules.selection.SelectionPane;
import de.perdian.apps.tagtiger.fx.modules.status.StatusPane;
import de.perdian.apps.tagtiger.fx.support.jobs.ChangeDirectoryJob;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TagTigerApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(TagTigerApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Creating TagTiger business objects");
        JobExecutor jobExecutor = new JobExecutor();
        PreferencesLookup preferences = new PreferencesLookup();
        Localization localization = new Localization() {};
        Selection selection = new Selection();
        selection.currentDirectoryProperty().addListener((o, oldValue, newValue) -> jobExecutor.executeJob(new ChangeDirectoryJob(newValue, selection, localization)));
        selection.currentDirectoryProperty().addListener((o, oldValue, newValue) -> preferences.setString(PreferencesKey.CURRENT_DIRECTORY, newValue == null ? null : newValue.toFile().getAbsolutePath()));

        log.info("Creating JavaFX UI");
        SelectionPane selectionPane = new SelectionPane(selection, localization, jobExecutor);
        EditorPane editorPane = new EditorPane(selection, localization, jobExecutor);
        SplitPane mainSplitPane = new SplitPane(selectionPane, editorPane);
        mainSplitPane.setDividerPositions(0.3d);
        StatusPane statusPane = new StatusPane(selection, localization, jobExecutor);
        BorderPane scenePane = new BorderPane();
        scenePane.setCenter(mainSplitPane);
        scenePane.setBottom(statusPane);

        String currentDirectoryValue = preferences.getString(PreferencesKey.CURRENT_DIRECTORY, null);
        File currentDirectory = currentDirectoryValue == null ? null : new File(currentDirectoryValue);
        if (currentDirectory != null && currentDirectory.exists() && currentDirectory.isDirectory()) {
            selection.currentDirectoryProperty().setValue(currentDirectory.toPath());
        }

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(new Scene(scenePane));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle(localization.applicationTitle());
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();

        log.info("Application start completed");

    }

}
