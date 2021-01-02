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
package de.perdian.apps.tagtiger3.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;

public class SongPropertyValue<T> {

    private ObjectProperty<T> persistedValue = new SimpleObjectProperty<>();
    private ObjectProperty<T> value = new SimpleObjectProperty<>();
    private ObservableBooleanValue dirty = null;
    private T clearValue = null;

    SongPropertyValue() {
        ObjectProperty<T> persistedValue = new SimpleObjectProperty<>();
        ObjectProperty<T> value = new SimpleObjectProperty<>();
        this.setPersistedValue(persistedValue);
        this.setValue(value);
        this.setDirty(Bindings.notEqual(persistedValue, value));
    }

    @Override
    public String toString() {
        return String.valueOf(this.getValue().getValue());
    }

    ObjectProperty<T> getPersistedValue() {
        return this.persistedValue;
    }
    private void setPersistedValue(ObjectProperty<T> persistedValue) {
        this.persistedValue = persistedValue;
    }

    public void clearValue() {
        this.getValue().setValue(this.getClearValue());
    }
    public void resetValue() {
        this.getValue().setValue(this.getPersistedValue().getValue());
    }
    public ObjectProperty<T> getValue() {
        return this.value;
    }
    private void setValue(ObjectProperty<T> value) {
        this.value = value;
    }

    ObservableBooleanValue getDirty() {
        return this.dirty;
    }
    private void setDirty(ObservableBooleanValue dirty) {
        this.dirty = dirty;
    }

    T getClearValue() {
        return this.clearValue;
    }
    void setClearValue(T clearValue) {
        this.clearValue = clearValue;
    }

}
