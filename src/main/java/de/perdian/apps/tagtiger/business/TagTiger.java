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
package de.perdian.apps.tagtiger.business;


/**
 * Central accessor class for all non-ui specific functions. This call is
 * designed to be used as facade/delegate for all business related functions
 * that need to be called from the UI.
 *
 * @author Christian Robert
 */

public class TagTiger {

    private TagTigerLocalization localization = new TagTigerLocalization() {};
    private TagTigerSelection selection = new TagTigerSelection();

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public TagTigerLocalization getLocalization() {
        return this.localization;
    }
    void setLocalization(TagTigerLocalization localization) {
        this.localization = localization;
    }

    public TagTigerSelection getSelection() {
        return this.selection;
    }
    void setSelection(TagTigerSelection selection) {
        this.selection = selection;
    }

}