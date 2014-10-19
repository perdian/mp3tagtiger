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
package de.perdian.apps.tagtiger.fx.handlers;

import java.util.function.Function;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

/**
 * Updates the selection to a specific record
 *
 * @author Christian Robert
 */

public class ChangeCurrentFileEventHandler<E extends Event> implements EventHandler<E> {

    private Property<TaggableFile> currentFileProperty = null;
    private ListProperty<TaggableFile> availableFilesProperty = null;
    private Function<E, ChangeCurrentFileDirection> directionFunction = null;

    public ChangeCurrentFileEventHandler(Property<TaggableFile> currentFileProperty, ListProperty<TaggableFile> availableFilesProperty, Function<E, ChangeCurrentFileDirection> directionFunction) {
        this.setCurrentFileProperty(currentFileProperty);
        this.setAvailableFilesProperty(availableFilesProperty);
        this.setDirectionFunction(directionFunction);
    }

    @Override
    public void handle(E event) {
        ChangeCurrentFileDirection direction = this.getDirectionFunction().apply(event);
        if (direction != null) {
            this.getCurrentFileProperty().setValue(direction.resolveRecord(this.getCurrentFileProperty().getValue(), this.getAvailableFilesProperty().get()));
        }
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    public static class KeyEventDirectionFunction implements Function<KeyEvent, ChangeCurrentFileDirection> {

        @Override
        public ChangeCurrentFileDirection apply(KeyEvent event) {
            switch (event.getCode()) {
                case PAGE_UP:
                    return event.isShiftDown() ? ChangeCurrentFileDirection.FIRST : ChangeCurrentFileDirection.PREVIOUS;
                case PAGE_DOWN:
                    return event.isShiftDown() ? ChangeCurrentFileDirection.LAST : ChangeCurrentFileDirection.NEXT;
                 default:
                     return null;
            }
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private Property<TaggableFile> getCurrentFileProperty() {
        return this.currentFileProperty;
    }
    private void setCurrentFileProperty(Property<TaggableFile> currentFileProperty) {
        this.currentFileProperty = currentFileProperty;
    }

    private ListProperty<TaggableFile> getAvailableFilesProperty() {
        return this.availableFilesProperty;
    }
    private void setAvailableFilesProperty(ListProperty<TaggableFile> availableFilesProperty) {
        this.availableFilesProperty = availableFilesProperty;
    }

    private Function<E, ChangeCurrentFileDirection> getDirectionFunction() {
        return this.directionFunction;
    }
    private void setDirectionFunction(Function<E, ChangeCurrentFileDirection> directionFunction) {
        this.directionFunction = directionFunction;
    }

}