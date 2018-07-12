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
package de.perdian.apps.tagtiger.fx.modules.editor;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.editor.components.ContentEditorPane;
import de.perdian.apps.tagtiger.fx.modules.editor.components.MetadataEditorPane;
import de.perdian.apps.tagtiger.fx.modules.editor.handlers.ChangeCurrentFileEventHandler;
import de.perdian.apps.tagtiger.fx.support.joblisteners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger.fx.support.jobs.SaveChangedFilesJob;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EditorPane extends VBox {

    public EditorPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        EditorComponentBuilderFactory componentBuilderFactory = new EditorComponentBuilderFactory(selection.currentFileProperty(), selection, localization);
        componentBuilderFactory.addControlCustomizer(component -> component.addEventHandler(KeyEvent.KEY_PRESSED, new ChangeCurrentFileEventHandler<>(selection.currentFileProperty(), selection.availableFilesProperty(), new ChangeCurrentFileEventHandler.KeyEventDirectionFunction())));
        componentBuilderFactory.addControlCustomizer(component -> component.addEventHandler(KeyEvent.KEY_PRESSED, event -> this.handleKeyPressedEvent(event, selection, localization, jobExecutor)));
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(this.disableProperty()));

        MetadataEditorPane metadataEditorPane = new MetadataEditorPane(componentBuilderFactory, selection, localization, jobExecutor);
        ContentEditorPane contentEditorPane = new ContentEditorPane(componentBuilderFactory, selection, localization, jobExecutor);
        VBox.setVgrow(contentEditorPane, Priority.ALWAYS);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setSpacing(5);
        this.setMinWidth(400);
        this.setPrefWidth(600);
        this.getChildren().addAll(metadataEditorPane, contentEditorPane);

    }

    private void handleKeyPressedEvent(KeyEvent event, Selection selection, Localization localization, JobExecutor jobExecutor) {
        if (event.isMetaDown() && KeyCode.ENTER.equals(event.getCode())) {
            jobExecutor.executeJob(new SaveChangedFilesJob(selection, localization));
        } else if (event.isMetaDown() && KeyCode.S.equals(event.getCode())) {
            jobExecutor.executeJob(new SaveChangedFilesJob(selection, localization));
        }
    }

}
