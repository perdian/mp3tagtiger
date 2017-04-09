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

import java.util.ArrayList;
import java.util.List;

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

    private Property<Boolean> disableProperty = null;
    private Control control = null;
    private List<ButtonDefinition> buttonDefinitions = null;

    static ComponentBuilder create() {
        return new ComponentBuilder();
    }

    private ComponentBuilder() {
        this.setButtonDefinitions(new ArrayList<>());
    }

    /**
     * Builds the target component that will be placed upon the panel and will
     * perform the interaction with the user
     */
    Pane buildControlPane() {

        HBox controlWrapper = new HBox();
        controlWrapper.setSpacing(5);
        GridPane.setHgrow(controlWrapper, Priority.ALWAYS);

        controlWrapper.getChildren().add(this.getControl());
        HBox.setHgrow(this.getControl(), Priority.ALWAYS);

        for (ButtonDefinition buttonDefinition : this.getButtonDefinitions()) {

           Button button = new Button();
           button.setOnAction(buttonDefinition.getActionEventHandler());
           if (buttonDefinition.getButtonIconLocation() != null) {
               button.setGraphic(new ImageView(new Image(ComponentBuilder.class.getClassLoader().getResourceAsStream(buttonDefinition.getButtonIconLocation()))));
           }
           if (buttonDefinition.getButtonIconTooltipText() != null) {
               button.setTooltip(new Tooltip(buttonDefinition.getButtonIconTooltipText()));
           }
           if (this.getDisableProperty() != null) {
               button.setDisable(this.getDisableProperty().getValue());
               button.disableProperty().bind(this.getDisableProperty());
           }

           controlWrapper.getChildren().add(button);
           if (buttonDefinition.isPrimary()) {
               this.getControl().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                   if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                       button.fire();
                   }
               });
           }

        }
        return controlWrapper;

    }

    static class ButtonDefinition {

        private EventHandler<ActionEvent> actionEventHandler = null;
        private String buttonIconLocation = null;
        private String buttonIconTooltipText = null;
        private boolean primary = false;

        EventHandler<ActionEvent> getActionEventHandler() {
            return this.actionEventHandler;
        }
        void setActionEventHandler(EventHandler<ActionEvent> actionEventHandler) {
            this.actionEventHandler = actionEventHandler;
        }

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

        boolean isPrimary() {
            return this.primary;
        }
        void setPrimary(boolean primary) {
            this.primary = primary;
        }

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

    ComponentBuilder button(boolean primary, String iconLocation, String iconTooltipText, EventHandler<ActionEvent> eventHandler) {
        ButtonDefinition buttonDefinition = new ButtonDefinition();
        buttonDefinition.setActionEventHandler(eventHandler);
        buttonDefinition.setButtonIconLocation(iconLocation);
        buttonDefinition.setButtonIconTooltipText(iconTooltipText);
        buttonDefinition.setPrimary(primary);
        this.getButtonDefinitions().add(buttonDefinition);
        return this;
    }
    private List<ButtonDefinition> getButtonDefinitions() {
        return this.buttonDefinitions;
    }
    private void setButtonDefinitions(List<ButtonDefinition> buttonDefinitions) {
        this.buttonDefinitions = buttonDefinitions;
    }

}