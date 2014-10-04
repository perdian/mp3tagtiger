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
package de.perdian.apps.tagtiger.fx.panels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import de.perdian.apps.tagtiger.business.TagTiger;
import de.perdian.apps.tagtiger.business.model.DirectoryWrapper;

/**
 * The listener that notifies the TagTiger when a directory has been selected
 *
 * @author Christian Robert
 */

class FileSelectionDirectoryListener implements ChangeListener<TreeItem<DirectoryWrapper>> {

    private TagTiger tagTiger = null;

    FileSelectionDirectoryListener(TagTiger tagTiger) {
        this.setTagTiger(tagTiger);
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<DirectoryWrapper>> observable, TreeItem<DirectoryWrapper> oldValue, TreeItem<DirectoryWrapper> newValue) {
        this.getTagTiger().getSelection().loadFilesFromDirectory(newValue.getValue().getPath());
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private TagTiger getTagTiger() {
        return this.tagTiger;
    }
    private void setTagTiger(TagTiger tagTiger) {
        this.tagTiger = tagTiger;
    }

}