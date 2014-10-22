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

import java.util.Optional;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

class EditorPaneHelper {

    public static Pane createControlWrapper(Control control, EditorGroupAction<?> groupAction, Property<TaggableFile> currentFileProperty, ListProperty<TaggableFile> selectedFilesProperty) {

        HBox controlWrapper = new HBox();
        controlWrapper.setSpacing(2);
        GridPane.setHgrow(controlWrapper, Priority.ALWAYS);

        controlWrapper.getChildren().add(control);
        HBox.setHgrow(control, Priority.ALWAYS);

        if (groupAction != null) {
           Button groupActionButton = new Button(null, new ImageView(new Image(EditorPaneHelper.class.getClassLoader().getResourceAsStream(groupAction.getIconLocation()))));
           groupActionButton.setTooltip(new Tooltip(groupAction.getTooltipText()));
           groupActionButton.setOnAction(event -> {
               groupAction.execute(currentFileProperty.getValue(), selectedFilesProperty.get());
           });
           selectedFilesProperty.addListener((Change<? extends TaggableFile> change) -> {
               groupActionButton.setDisable(change.getList().size() < groupAction.getMinimumFiles());
           });
           controlWrapper.getChildren().add(groupActionButton);
           control.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
               if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                   Optional.ofNullable(groupActionButton.onActionProperty().get()).ifPresent(handler -> handler.handle(new ActionEvent(control, null)));
               }
           });
        }
        return controlWrapper;

    }

}