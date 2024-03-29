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
package de.perdian.apps.tagtiger.fx;

import de.perdian.apps.tagtiger.fx.model.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.fx.jobs.JobExecutor;
import de.perdian.commons.fx.AbstractApplication;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class TagTigerApplication extends AbstractApplication {

    private static final Logger log = LoggerFactory.getLogger(TagTigerApplication.class);

    private Selection selection = null;
    private JobExecutor jobExecutor = null;

    @Override
    public void init() throws Exception {
        super.init();
        JobExecutor jobExecutor = new JobExecutor();
        Selection selection = new Selection(jobExecutor);
        this.setSelection(selection);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    protected Pane createMainPane() {
        return new TagTigerPane(this.getPreferences(), this.getSelection(), this.getJobExecutor());
    }

    @Override
    protected Scene createMainScene(Pane mainPane) {
        Scene scene = super.createMainScene(mainPane);
        scene.getStylesheets().add(this.getClass().getClassLoader().getResource("css/tagtiger.css").toString());
        return scene;
    }

    @Override
    protected void configurePrimaryStage(Stage primaryStage) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("MP3 TagTiger");
        primaryStage.setWidth(Math.min(1400, screenBounds.getWidth() - 250));
        primaryStage.setHeight(Math.min(900, screenBounds.getHeight() - 250));
    }

    public static void showError(String title, Throwable exception, Window parentWindow) {
        log.warn("An error occured: {}", title, exception);
    }

    public Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    public JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
