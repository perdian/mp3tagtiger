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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import javafx.scene.Parent;

public class BatchUpdateDialog {

    private int dialogPrefWidth = 500;
    private String dialogTitle = null;
    private Parent actionPane = null;

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public String getDialogTitle() {
        return this.dialogTitle;
    }
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public Parent getActionPane() {
        return this.actionPane;
    }
    public void setActionPane(Parent actionPane) {
        this.actionPane = actionPane;
    }

    public int getDialogPrefWidth() {
        return this.dialogPrefWidth;
    }
    public void setDialogPrefWidth(int dialogPrefWidth) {
        this.dialogPrefWidth = dialogPrefWidth;
    }

}