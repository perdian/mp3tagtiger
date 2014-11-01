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
package de.perdian.apps.tagtiger.fx.panels.editor.groupactions;

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.Property;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorGroupAction;

public class CopyTracksCountGroupAction extends EditorGroupAction<String> {

    public CopyTracksCountGroupAction(String iconLocation, String tooltipText, Function<TaggableFile, Property<String>> propertyFunction) {
        super(iconLocation, tooltipText, propertyFunction, 1);
    }

    @Override
    public void execute(TaggableFile currentFile, List<TaggableFile> otherFiles) {
        otherFiles.forEach(file -> this.getPropertyFunction().apply(file).setValue(String.valueOf(otherFiles.size())));
    }

}