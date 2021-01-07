/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.components.tools.filenames;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class FilenamesToolItemValue {

    private final StringProperty originalValue = new SimpleStringProperty();
    private final StringProperty newValue = new SimpleStringProperty();

    FilenamesToolItemValue(String attributeValue) {
        this.getOriginalValue().setValue(attributeValue);
        this.getNewValue().setValue(attributeValue);
    }

    StringProperty getOriginalValue() {
        return this.originalValue;
    }

    StringProperty getNewValue() {
        return this.newValue;
    }

}
