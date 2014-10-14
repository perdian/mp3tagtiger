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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.fx.panels.MainApplicationPane;

/**
 * The main JavaFX application that makes up the TagTiger application
 *
 * @author Christian Robert
 */

public class TagTigerApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(TagTigerApplication.class);
    private static TagTiger tagTiger = null;

    public static void main(String[] args) {
        log.info("Starting application");
        Application.launch(TagTigerApplication.class);
    }

    @Override
    public void init() throws Exception {

        log.info("Creating TagTiger business delegate");
        TagTiger tagTiger = new TagTiger();
        tagTiger.getMessageDistributor().addConsumer(new TagTigerMessageConsumer());
        this.setTagTiger(tagTiger);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Creating JavaFX UI");
        Scene scene = new Scene(new MainApplicationPane(this.getTagTiger()));

        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/application.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/64/application.png")));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle(this.getTagTiger().getLocalization().applicationTitle());
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();

        String currentDirectoryValue = this.getTagTiger().getPreferences().getString(PreferencesKey.CURRENT_DIRECTORY, null);
        File currentDirectory = currentDirectoryValue == null ? null : new File(currentDirectoryValue);
        if (currentDirectory != null && currentDirectory.exists() && currentDirectory.isDirectory()) {
            this.getTagTiger().getSelection().selectedDirectoryProperty().set(currentDirectory);
        }

        log.info("Application start completed");

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private TagTiger getTagTiger() {
        return TagTigerApplication.tagTiger;
    }
    private void setTagTiger(TagTiger tagTiger) {
        TagTigerApplication.tagTiger = tagTiger;
    }

}