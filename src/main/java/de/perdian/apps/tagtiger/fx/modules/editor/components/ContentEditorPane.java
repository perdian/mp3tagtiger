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
package de.perdian.apps.tagtiger.fx.modules.editor.components;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.editor.EditorComponentBuilderFactory;
import de.perdian.apps.tagtiger.fx.support.joblisteners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ContentEditorPane extends BorderPane {

    public ContentEditorPane(EditorComponentBuilderFactory componentBuilderFactory, Selection selection, Localization localization, JobExecutor jobExecutor) {

        TagsEditorTab tagsEditorTab = new TagsEditorTab(componentBuilderFactory, selection, localization);
        ImagesEditorTab imagesEditorTab = new ImagesEditorTab(componentBuilderFactory, selection, localization);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tagsEditorTab, imagesEditorTab);
        tabPane.setPadding(new Insets(5, 5, 5, 5));

        TitledPane taggingWrapperPane = new TitledPane(localization.tags(), tabPane);
        taggingWrapperPane.setCollapsible(false);
        taggingWrapperPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(taggingWrapperPane, Priority.ALWAYS);
        this.setCenter(taggingWrapperPane);;

        jobExecutor.addListener(new DisableWhileJobRunningJobListener(this.disableProperty()));

    }

}
