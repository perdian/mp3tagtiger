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
package de.perdian.apps.tagtiger.fx.panels.editor.support;

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.files.Action;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
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

public class ComponentBuilder {

    private Property<TaggableFile> currentFileProperty = null;
    private ObservableList<TaggableFile> selectedFiles = null;
    private ObservableList<TaggableFile> availableFiles = null;
    private Control control = null;
    private Property<Boolean> actionsDisabledProperty = null;
    private List<ComponentBuilderActionDefinition> actionDefinitions = null;

    public static ComponentBuilder create(Property<TaggableFile> currentFileProperty, ObservableList<TaggableFile> selectedFiles, ObservableList<TaggableFile> availableFiles) {
        return new ComponentBuilder(currentFileProperty, selectedFiles, availableFiles);
    }

    private ComponentBuilder(Property<TaggableFile> currentFileProperty, ObservableList<TaggableFile> selectedFiles, ObservableList<TaggableFile> availableFiles) {
        this.setActionDefinitions(new ArrayList<>());
        this.setCurrentFileProperty(currentFileProperty);
        this.setSelectedFiles(selectedFiles);
        this.setAvailableFiles(availableFiles);
    }

    /**
     * Builds the target component that will be placed upon the panel and will
     * perform the interaction with the user
     */
    public Pane buildControlPane() {

        HBox controlWrapper = new HBox();
        controlWrapper.setSpacing(5);
        GridPane.setHgrow(controlWrapper, Priority.ALWAYS);

        controlWrapper.getChildren().add(this.getControl());
        HBox.setHgrow(this.getControl(), Priority.ALWAYS);

        for (ComponentBuilderActionDefinition actionDefinition : this.getActionDefinitions()) {

            Button button = new Button();
            button.setOnAction(actionDefinition.getAction().asEventHandler(this.getCurrentFileProperty(), this.getAvailableFiles()));
            if (actionDefinition.getButtonIconLocation() != null) {
                button.setGraphic(new ImageView(new Image(ComponentBuilder.class.getClassLoader().getResourceAsStream(actionDefinition.getButtonIconLocation()))));
            }
            if (actionDefinition.getButtonIconTooltipText() != null) {
                button.setTooltip(new Tooltip(actionDefinition.getButtonIconTooltipText()));
            }
            if (this.getDisableProperty() != null) {
                button.setDisable(this.getDisableProperty().getValue());
                button.disableProperty().bind(this.getDisableProperty());
            }

            controlWrapper.getChildren().add(button);
            if (actionDefinition.isPrimary()) {
                this.getControl().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                        actionDefinition.getAction().execute(this.getCurrentFileProperty(), this.getAvailableFiles());
                    } else if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
                        actionDefinition.getAction().execute(this.getCurrentFileProperty(), this.getSelectedFiles());
                    }
                });
            }

        }
        return controlWrapper;

    }

    private Property<TaggableFile> getCurrentFileProperty() {
        return this.currentFileProperty;
    }
    private void setCurrentFileProperty(Property<TaggableFile> currentFileProperty) {
        this.currentFileProperty = currentFileProperty;
    }

    private ObservableList<TaggableFile> getSelectedFiles() {
        return this.selectedFiles;
    }
    private void setSelectedFiles(ObservableList<TaggableFile> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    private ObservableList<TaggableFile> getAvailableFiles() {
        return this.availableFiles;
    }
    private void setAvailableFiles(ObservableList<TaggableFile> availableFiles) {
        this.availableFiles = availableFiles;
    }

    public ComponentBuilder control(Control control) {
        this.setControl(control);
        return this;
    }
    private Control getControl() {
        return this.control;
    }
    private void setControl(Control control) {
        this.control = control;
    }

    public ComponentBuilder actionsDisabledProperty(Property<Boolean> actionsDisabledProperty) {
        this.setDisableProperty(actionsDisabledProperty);
        return this;
    }
    private Property<Boolean> getDisableProperty() {
        return this.actionsDisabledProperty;
    }
    private void setDisableProperty(Property<Boolean> disableProperty) {
        this.actionsDisabledProperty = disableProperty;
    }

    public ComponentBuilder primaryAction(String iconLocation, String iconTooltipText, Action action) {
        ComponentBuilderActionDefinition actionDefinition = new ComponentBuilderActionDefinition();
        actionDefinition.setButtonIconLocation(iconLocation);
        actionDefinition.setButtonIconTooltipText(iconTooltipText);
        actionDefinition.setAction(action);
        actionDefinition.setPrimary(true);
        this.getActionDefinitions().add(actionDefinition);
        return this;
    }
    public ComponentBuilder secondaryAction(String iconLocation, String iconTooltipText, Action action) {
        ComponentBuilderActionDefinition actionDefinition = new ComponentBuilderActionDefinition();
        actionDefinition.setButtonIconLocation(iconLocation);
        actionDefinition.setButtonIconTooltipText(iconTooltipText);
        actionDefinition.setAction(action);
        actionDefinition.setPrimary(false);
        this.getActionDefinitions().add(actionDefinition);
        return this;
    }
    private List<ComponentBuilderActionDefinition> getActionDefinitions() {
        return this.actionDefinitions;
    }
    private void setActionDefinitions(List<ComponentBuilderActionDefinition> ActionDefinitions) {
        this.actionDefinitions = ActionDefinitions;
    }

}