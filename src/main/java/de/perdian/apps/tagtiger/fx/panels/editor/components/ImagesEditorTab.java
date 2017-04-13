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
package de.perdian.apps.tagtiger.fx.panels.editor.components;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.support.EditorComponentBuilderFactory;
import javafx.scene.control.Tab;

class ImagesEditorTab extends Tab {

    ImagesEditorTab(EditorComponentBuilderFactory componentBuilderFactory, Selection selection, Localization localization) {

//      this.currentFileProperty().addListener((o, oldValue, newValue) -> imagesEditorPane.imagesProperty().set(newValue == null ? null : newValue.imagesProperty().getValue().getTagImages()));

        this.setText(localization.images());
        this.setClosable(false);


    }

}
