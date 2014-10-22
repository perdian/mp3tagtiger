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
package de.perdian.apps.tagtiger.fx.util;

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

/**
 * Contains information about components that should be used to edit data
 *
 * @author Christian Robert
 */

class EditorComponentWrapper<T, U> {

    private ChangeListener<?> beanPropertyChangeListener = null;
    private Function<T, Property<U>> beanPropertySupplier = null;

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ChangeListener<?> getBeanPropertyChangeListener() {
        return this.beanPropertyChangeListener;
    }
    void setBeanPropertyChangeListener(ChangeListener<?> beanPropertyChangeListener) {
        this.beanPropertyChangeListener = beanPropertyChangeListener;
    }

    Function<T, Property<U>> getBeanPropertySupplier() {
        return this.beanPropertySupplier;
    }
    void setBeanPropertySupplier(Function<T, Property<U>> beanPropertySupplier) {
        this.beanPropertySupplier = beanPropertySupplier;
    }

}