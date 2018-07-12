/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.editor.actions.ClearPropertyValueAction;
import de.perdian.apps.tagtiger.fx.modules.editor.actions.CopyPropertyValueAction;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class EditorComponentBuilder {

    private Selection selection = null;
    private Localization localization = null;
    private Property<String> editProperty = null;
    private Property<Boolean> disabledProperty = null;
    private List<Consumer<Control>> controlCustomizers = null;
    private List<EditorComponentActionWrapper> actions = null;
    private Supplier<Control> controlSupplier = null;
    private Function<TaggableFile, Property<String>> propertyResolver = null;

    EditorComponentBuilder(Property<String> editProperty) {
        this.setEditProperty(editProperty);
        this.setActions(new ArrayList<>());
    }

    public EditorComponentBuilder useNumericTextField() {
        return this.useNumericTextField(null);
    }

    public EditorComponentBuilder useNumericTextField(Consumer<TextField> textFieldConsumer) {
        return this.useTextField(textField -> {
            textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("\\d+")) {
                    event.consume();
                }
            });
            if (textFieldConsumer != null) {
                textFieldConsumer.accept(textField);
            }
        });
    }

    public EditorComponentBuilder useTextField() {
        return this.useTextField(null);
    }

    public EditorComponentBuilder useTextField(Consumer<TextField> textFieldConsumer) {
        return this.useControl(() -> {
            TextField textField = new TextField(this.getEditProperty().getValue());
            textField.setMinWidth(50);
            textField.setPrefWidth(0);
            textField.focusedProperty().addListener((o, oldValue, newValue) -> {
                if (newValue) {
                    Platform.runLater(() -> textField.selectAll());
                }
            });
            textField.textProperty().addListener((o, oldValue, newValue) -> this.getEditProperty().setValue(newValue));
            this.getEditProperty().addListener((o, oldValue, newValue) -> {
                textField.setText(newValue == null ? null : newValue.toString());
                if (textField.focusedProperty().get()) {
                    textField.selectAll();
                }
            });
            Optional.ofNullable(textFieldConsumer).ifPresent(consumer -> consumer.accept(textField));
            return textField;
        });
    }

    public EditorComponentBuilder useComboBox(List<String> values) {
        return this.useControl(() -> {
            ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
            comboBox.setPrefWidth(0);
            comboBox.setMaxWidth(Double.MAX_VALUE);
            comboBox.setEditable(true);
            comboBox.focusedProperty().addListener((o, oldValue, newValue) -> {
                Platform.runLater(() -> comboBox.getEditor().selectAll());
            });
            comboBox.valueProperty().addListener((o, oldValue, newValue) -> this.getEditProperty().setValue(newValue));
            comboBox.getEditor().textProperty().addListener((o, oldValue, newValue) -> {
                this.getEditProperty().setValue(newValue);
            });
            this.getEditProperty().addListener((o, oldValue, newValue) -> {
                comboBox.setValue(newValue == null ? null : newValue.toString());
            });
            return comboBox;
        });
    }

    public EditorComponentBuilder useControl(Supplier<Control> controlSupplier) {
        this.setControlSupplier(controlSupplier);
        return this;
    }

    public EditorComponentBuilder actionCopyPropertyValue() {
        return this.actionCopyPropertyValue((property, newValue) -> property.setValue(newValue));
    }

    public EditorComponentBuilder actionCopyPropertyValue(BiConsumer<Property<String>, String> copyValueConsumer) {
        return this.action("icons/16/copy.png", this.getLocalization().copyToAllOtherSelectedFiles(), new CopyPropertyValueAction<>(this.getPropertyResolver(), copyValueConsumer));
    }

    public EditorComponentBuilder actionClearPropertyValues() {
        return this.action("icons/16/delete.png", this.getLocalization().clearAllValues(), new ClearPropertyValueAction<>(this.getPropertyResolver()));
    }

    public EditorComponentBuilder action(String iconLocation, String tooltipText, EditorComponentAction action) {
        EditorComponentActionWrapper actionWrapper = new EditorComponentActionWrapper();
        actionWrapper.setButtonIconLocation(iconLocation);
        actionWrapper.setButtonIconTooltipText(tooltipText);;
        actionWrapper.setAction(action);
        this.getActions().add(actionWrapper);
        return this;
    }

    public Parent build() {
        if (this.getControlSupplier() == null) {
            throw new IllegalArgumentException("Control supplier has not been provided for this component builder");
        } else {

            HBox componentWrapper = new HBox();
            componentWrapper.setSpacing(2);
            GridPane.setHgrow(componentWrapper, Priority.ALWAYS);

            Control control = this.getControlSupplier().get();
            control.setDisable(true);
            HBox.setHgrow(control, Priority.ALWAYS);
            this.getControlCustomizers().forEach(customer -> customer.accept(control));
            this.getDisabledProperty().addListener((o, oldValue, newValue) -> control.setDisable(newValue));
            componentWrapper.getChildren().add(control);

            for (int i=0; i < this.getActions().size(); i++) {

                EditorComponentActionWrapper actionWrapper = this.getActions().get(i);

                BooleanProperty selectionEmptyProperty = new SimpleBooleanProperty(true);
                this.getSelection().selectedFilesProperty().addListener((o, oldValue, newValue) -> selectionEmptyProperty.setValue(newValue == null || newValue.size() <= 1));
                BooleanBinding actionButtonDisableBinding = BooleanProperty.booleanProperty(this.getDisabledProperty()).or(selectionEmptyProperty);

                Button actionButton = new Button();
                actionButton.setDisable(true);
                actionButton.disableProperty().bind(actionButtonDisableBinding);
                actionButton.setOnAction(event -> actionWrapper.getAction().execute(this.getSelection().currentFileProperty(), this.getSelection().selectedFilesProperty(), this.getSelection().availableFilesProperty().getValue()));
                if (actionWrapper.getButtonIconLocation() != null) {
                    actionButton.setGraphic(new ImageView(new Image(EditorComponentBuilder.class.getClassLoader().getResourceAsStream(actionWrapper.getButtonIconLocation()))));
                }
                if (actionWrapper.getButtonIconTooltipText() != null) {
                    actionButton.setTooltip(new Tooltip(actionWrapper.getButtonIconTooltipText()));
                }
                componentWrapper.getChildren().add(actionButton);
                if (i == 0) {
                    control.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                            actionWrapper.getAction().execute(this.getSelection().currentFileProperty(), this.getSelection().availableFilesProperty(), this.getSelection().availableFilesProperty());
                        } else if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
                            actionWrapper.getAction().execute(this.getSelection().currentFileProperty(), this.getSelection().selectedFilesProperty(), this.getSelection().availableFilesProperty());
                        }
                    });
                }

            }

            return componentWrapper;

        }
    }

    private List<EditorComponentActionWrapper> getActions() {
        return this.actions;
    }
    private void setActions(List<EditorComponentActionWrapper> actions) {
        this.actions = actions;
    }

    private Property<String> getEditProperty() {
        return this.editProperty;
    }
    private void setEditProperty(Property<String> editProperty) {
        this.editProperty = editProperty;
    }

    private Supplier<Control> getControlSupplier() {
        return this.controlSupplier;
    }
    private void setControlSupplier(Supplier<Control> controlSupplier) {
        this.controlSupplier = controlSupplier;
    }

    Selection getSelection() {
        return this.selection;
    }
    void setSelection(Selection selection) {
        this.selection = selection;
    }

    Localization getLocalization() {
        return this.localization;
    }
    void setLocalization(Localization localization) {
        this.localization = localization;
    }

    Function<TaggableFile, Property<String>> getPropertyResolver() {
        return this.propertyResolver;
    }
    void setPropertyResolver(Function<TaggableFile, Property<String>> propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    Property<Boolean> getDisabledProperty() {
        return this.disabledProperty;
    }
    void setDisabledProperty(Property<Boolean> disabledProperty) {
        this.disabledProperty = disabledProperty;
    }

    List<Consumer<Control>> getControlCustomizers() {
        return this.controlCustomizers;
    }
    void setControlCustomizers(List<Consumer<Control>> controlCustomizers) {
        this.controlCustomizers = controlCustomizers;
    }

}
