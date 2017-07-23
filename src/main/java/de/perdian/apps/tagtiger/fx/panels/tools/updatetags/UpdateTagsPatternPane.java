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

import java.util.regex.Pattern;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

class UpdateTagsPatternPane extends BorderPane {

    UpdateTagsPatternPane(Pattern pattern, Selection selection, Localization localization) {

        int newGroupCount = pattern.matcher("").groupCount();
        GridPane groupPane = new GridPane();
        groupPane.setPadding(new Insets(5, 5, 5, 5));
        for (int i=0; i < newGroupCount; i++) {
            groupPane.add(new Label(localization.group() + " " + (i+1)), 0, i, 1, 1);
        }
        this.setCenter(groupPane);

    }

}
