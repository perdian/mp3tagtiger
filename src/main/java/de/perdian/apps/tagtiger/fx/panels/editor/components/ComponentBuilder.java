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
package de.perdian.apps.tagtiger.fx.panels.editor.components;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

class ComponentBuilder {

    private EventHandler<ActionEvent> actionEventHandler = null;
    private String buttonText = null;
    private String buttonIconLocation = null;
    private String buttonIconTooltipText = null;
    private Property<Boolean> disableProperty = null;
    private Control control = null;

    static ComponentBuilder create() {
        return new ComponentBuilder();
    }

    /**
     * Builds the target component that will be placed upon the panel and will
     * perform the interaction with the user
     */
    Pane buildControlPane() {

        HBox controlWrapper = new HBox();
        controlWrapper.setSpacing(2);
        GridPane.setHgrow(controlWrapper, Priority.ALWAYS);

        controlWrapper.getChildren().add(this.control);
        HBox.setHgrow(this.control, Priority.ALWAYS);

        if (this.getActionEventHandler() != null) {

           Button button = new Button(this.getButtonText());
           button.setOnAction(this.getActionEventHandler());
           if (this.getButtonIconLocation() != null) {
               button.setGraphic(new ImageView(new Image(ComponentBuilder.class.getClassLoader().getResourceAsStream(this.getButtonIconLocation()))));
           }
           if (this.getButtonIconTooltipText() != null) {
               button.setTooltip(new Tooltip(this.getButtonIconTooltipText()));
           }
           if (this.getDisableProperty() != null) {
               button.setDisable(this.getDisableProperty().getValue());
               button.disableProperty().bind(this.getDisableProperty());
           }

           controlWrapper.getChildren().add(button);
           this.getControl().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
               if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                   this.getActionEventHandler().handle(new ActionEvent(button, button));
               }
           });
        }
        return controlWrapper;

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ComponentBuilder actionEventHandler(EventHandler<ActionEvent> actionEventHandler) {
        this.setActionEventHandler(actionEventHandler);
        return this;
    }
    private EventHandler<ActionEvent> getActionEventHandler() {
        return this.actionEventHandler;
    }
    private void setActionEventHandler(EventHandler<ActionEvent> actionEventHandler) {
        this.actionEventHandler = actionEventHandler;
    }

    ComponentBuilder buttonText(String buttonText) {
        this.setButtonText(buttonText);
        return this;
    }
    private String getButtonText() {
        return this.buttonText;
    }
    private void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    ComponentBuilder buttonIconLocation(String buttonIconLocation) {
        this.setButtonIconLocation(buttonIconLocation);
        return this;
    }
    private String getButtonIconLocation() {
        return this.buttonIconLocation;
    }
    private void setButtonIconLocation(String buttonIconLocation) {
        this.buttonIconLocation = buttonIconLocation;
    }

    ComponentBuilder buttonIconTooltipText(String buttonIconTooltipText) {
        this.setButtonIconTooltipText(buttonIconTooltipText);
        return this;
    }
    private String getButtonIconTooltipText() {
        return this.buttonIconTooltipText;
    }
    private void setButtonIconTooltipText(String buttonIconTooltipText) {
        this.buttonIconTooltipText = buttonIconTooltipText;
    }

    ComponentBuilder disableProperty(Property<Boolean> disableProperty) {
        this.setDisableProperty(disableProperty);
        return this;
    }
    private Property<Boolean> getDisableProperty() {
        return this.disableProperty;
    }
    private void setDisableProperty(Property<Boolean> disableProperty) {
        this.disableProperty = disableProperty;
    }

    ComponentBuilder control(Control control) {
        this.setControl(control);
        return this;
    }
    private Control getControl() {
        return this.control;
    }
    private void setControl(Control control) {
        this.control = control;
    }

}