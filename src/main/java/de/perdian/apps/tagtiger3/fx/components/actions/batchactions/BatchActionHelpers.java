/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.actions.batchactions;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

class BatchActionHelpers {

    static <T> Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> createIconCellCallback(FontAwesomeIcon trueIcon, FontAwesomeIcon falseIcon) {
        return column -> {
            TableCell<T, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        if (item != null && item.booleanValue()) {
                            this.setGraphic(trueIcon == null ? null : new Label("", new FontAwesomeIconView(trueIcon)));
                        } else {
                            this.setGraphic(falseIcon == null ? null : new Label("", new FontAwesomeIconView(falseIcon)));
                        }
                    } else {
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;
        };
    }

}
