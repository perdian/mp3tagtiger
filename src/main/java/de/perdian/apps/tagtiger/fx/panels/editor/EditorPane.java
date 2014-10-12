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
package de.perdian.apps.tagtiger.fx.panels.editor;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.TagTiger;

public class EditorPane extends VBox {

    public EditorPane(TagTiger tagTiger) {

        FileInformationPane informationPane = new FileInformationPane(tagTiger);
        informationPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane informationWrapperPane = new TitledPane(tagTiger.getLocalization().mp3File(), informationPane);
        informationWrapperPane.setExpanded(true);

        FileTagsPane tagsPane = new FileTagsPane(tagTiger);
        tagsPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane taggingWrapperPane = new TitledPane(tagTiger.getLocalization().id3Tag(), tagsPane);
        taggingWrapperPane.setMaxHeight(Double.MAX_VALUE);
        taggingWrapperPane.setCollapsible(false);
        VBox.setVgrow(taggingWrapperPane, Priority.ALWAYS);

        this.setSpacing(5);
        this.getChildren().addAll(informationWrapperPane, taggingWrapperPane);

    }

}