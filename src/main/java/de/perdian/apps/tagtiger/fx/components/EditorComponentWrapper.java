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
package de.perdian.apps.tagtiger.fx.components;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

/**
 * Contains information about components that should be used to edit data
 *
 * @author Christian Robert
 */

class EditorComponentWrapper<T> {

    private ChangeListener<String> beanPropertyChangeListener = null;
    private ChangeListener<String> controlValueChangeListener = null;
    private Function<T, StringProperty> beanPropertySupplier = null;
    private Supplier<StringProperty> controlValueSupplier = null;

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ChangeListener<String> getBeanPropertyChangeListener() {
        return this.beanPropertyChangeListener;
    }
    void setBeanPropertyChangeListener(ChangeListener<String> beanPropertyChangeListener) {
        this.beanPropertyChangeListener = beanPropertyChangeListener;
    }

    ChangeListener<String> getControlValueChangeListener() {
        return this.controlValueChangeListener;
    }
    void setControlValueChangeListener(ChangeListener<String> controlValueChangeListener) {
        this.controlValueChangeListener = controlValueChangeListener;
    }

    Function<T, StringProperty> getBeanPropertySupplier() {
        return this.beanPropertySupplier;
    }
    void setBeanPropertySupplier(Function<T, StringProperty> beanPropertySupplier) {
        this.beanPropertySupplier = beanPropertySupplier;
    }

    Supplier<StringProperty> getControlValueSupplier() {
        return this.controlValueSupplier;
    }
    void setControlValueSupplier(Supplier<StringProperty> controlValueSupplier) {
        this.controlValueSupplier = controlValueSupplier;
    }

}