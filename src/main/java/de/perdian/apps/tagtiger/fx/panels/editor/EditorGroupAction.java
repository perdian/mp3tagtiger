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
package de.perdian.apps.tagtiger.fx.panels.editor;

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.Property;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;

public abstract class EditorGroupAction<T> {

    private String iconLocation = null;
    private String tooltipText = null;
    private Function<TaggableFile, Property<T>> propertyFunction = null;
    private int minimumFiles = 1;

    public EditorGroupAction(String iconLocation, String tooltipText, Function<TaggableFile, Property<T>> propertyFunction, int minimumFiles) {
        this.setIconLocation(iconLocation);
        this.setTooltipText(tooltipText);
        this.setPropertyFunction(propertyFunction);
        this.setMinimumFiles(minimumFiles);
    }

    public abstract void execute(TaggableFile currentFile, List<TaggableFile> otherFiles);

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public String getIconLocation() {
        return this.iconLocation;
    }
    private void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }

    public String getTooltipText() {
        return this.tooltipText;
    }
    private void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    public Function<TaggableFile, Property<T>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<T>> propertyFunction) {
        this.propertyFunction = propertyFunction;
    }

    public int getMinimumFiles() {
        return this.minimumFiles;
    }
    private void setMinimumFiles(int minimumFiles) {
        this.minimumFiles = minimumFiles;
    }

}