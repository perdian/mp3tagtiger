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
package de.perdian.apps.tagtiger.fx.panels.tools.updatetags;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateTagsOpenDialogEventHandler implements EventHandler<ActionEvent> {

    private Selection selection = null;
    private Localization localization = null;

public static class DummyApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Selection selection = new Selection();
        Localization localization = new Localization() { };
        primaryStage.setScene(new Scene(new UpdateTagsEditorPane(selection, localization)));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle(localization.applicationTitle());
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }
}
public static void main(String[] args) {
    Application.launch(DummyApplication.class, args);
}


    public UpdateTagsOpenDialogEventHandler(Selection selection, Localization localization) {
        this.setSelection(selection);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        Stage dialogStage = new Stage();
        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(100);
        dialogStage.setTitle(this.getLocalization().updateFileNames());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(new UpdateTagsEditorPane(this.getSelection(), this.getLocalization())));
        dialogStage.show();

    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
