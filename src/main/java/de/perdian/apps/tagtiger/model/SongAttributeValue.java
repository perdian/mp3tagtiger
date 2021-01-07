/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger.model;

import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;

class SongAttributeValue<T> {

    private ObjectProperty<T> persistedValue = new SimpleObjectProperty<>();
    private ObjectProperty<T> value = new SimpleObjectProperty<>();
    private Supplier<T> clearSupplier = () -> null;
    private ObservableBooleanValue dirty = null;

    SongAttributeValue(T value) {
        this.getPersistedValue().setValue(value);
        this.getValue().setValue(value);
        this.setDirty(Bindings.notEqual(this.getPersistedValue(), this.getValue()));
    }

    @Override
    public String toString() {
        return String.valueOf(this.getValue().getValue());
    }

    ObjectProperty<T> getPersistedValue() {
        return this.persistedValue;
    }

    void clear() {
        this.getValue().setValue(this.getClearSupplier().get());
    }
    void reset() {
        this.getValue().setValue(this.getPersistedValue().getValue());
    }
    public ObjectProperty<T> getValue() {
        return this.value;
    }

    Supplier<T> getClearSupplier() {
        return this.clearSupplier;
    }
    void setClearSupplier(Supplier<T> clearSupplier) {
        this.clearSupplier = clearSupplier;
    }

    ObservableBooleanValue getDirty() {
        return this.dirty;
    }
    private void setDirty(ObservableBooleanValue dirty) {
        this.dirty = dirty;
    }

}
