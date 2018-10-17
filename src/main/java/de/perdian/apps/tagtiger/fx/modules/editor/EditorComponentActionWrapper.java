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

class EditorComponentActionWrapper {

    private String buttonIconLocation = null;
    private String buttonIconTooltipText = null;
    private EditorComponentAction action = null;

    String getButtonIconLocation() {
        return this.buttonIconLocation;
    }
    void setButtonIconLocation(String buttonIconLocation) {
        this.buttonIconLocation = buttonIconLocation;
    }

    String getButtonIconTooltipText() {
        return this.buttonIconTooltipText;
    }
    void setButtonIconTooltipText(String buttonIconTooltipText) {
        this.buttonIconTooltipText = buttonIconTooltipText;
    }

    EditorComponentAction getAction() {
        return this.action;
    }
    void setAction(EditorComponentAction action) {
        this.action = action;
    }

}