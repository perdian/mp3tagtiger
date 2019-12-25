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
package de.perdian.apps.tagtiger.fx.modules.editor.actions;

import java.util.List;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.modules.editor.EditorComponentAction;
import javafx.beans.property.Property;

public class EnumerateTracksAction implements EditorComponentAction {

    @Override
    public void execute(Property<TaggableFile> sourceFileProperty, List<TaggableFile> targetFiles, List<TaggableFile> allFiles) {
        for (int i=0; i < targetFiles.size(); i++) {
            targetFiles.get(i).property(TaggablePropertyKey.TRACK_NUMBER).setValue(String.valueOf(i+1));
            targetFiles.get(i).property(TaggablePropertyKey.TRACKS_TOTAL).setValue(String.valueOf(targetFiles.size()));
        }
    }

}
