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
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorGroupAction;

public class CopyValuesGroupAction<T> extends EditorGroupAction<T> {

    private BiConsumer<Property<T>, T> copyValueConsumer = null;

    public CopyValuesGroupAction(String iconLocation, String tooltipText, Function<TaggableFile, Property<T>> propertyFunction) {
        this(iconLocation, tooltipText, propertyFunction, null);
    }

    public CopyValuesGroupAction(String iconLocation, String tooltipText, Function<TaggableFile, Property<T>> propertyFunction, BiConsumer<Property<T>, T> copyValueConsumer) {
        super(iconLocation, tooltipText, propertyFunction, 2);
        this.setCopyValueConsumer(copyValueConsumer == null ? (property, newValue) -> property.setValue(newValue) : copyValueConsumer);
    }

    @Override
    public void execute(TaggableFile currentFile, List<TaggableFile> otherFiles) {
        otherFiles.stream()
            .filter(otherFile -> !currentFile.equals(otherFile))
            .forEach(otherFile-> this.getCopyValueConsumer().accept(this.getPropertyFunction().apply(otherFile), this.getPropertyFunction().apply(currentFile).getValue()));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private BiConsumer<Property<T>, T> getCopyValueConsumer() {
        return this.copyValueConsumer;
    }
    private void setCopyValueConsumer(BiConsumer<Property<T>, T> copyValueConsumer) {
        this.copyValueConsumer = copyValueConsumer;
    }

}