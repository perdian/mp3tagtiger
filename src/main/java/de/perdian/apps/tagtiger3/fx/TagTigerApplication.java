/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx;

import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.commons.fx.AbstractApplication;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TagTigerApplication extends AbstractApplication {

    @Override
    protected Pane createMainPane() {
        TagTigerPane mainPane = new TagTigerPane(this.getPreferences(), new JobExecutor());
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        return mainPane;
    }

    @Override
    protected void configurePrimaryStage(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("MP3 TagTiger");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
    }

}
