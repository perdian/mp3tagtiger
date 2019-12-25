/*
 * Copyright 2014-2018 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ExtractionRule {

    private int patternGroupIndex = 0;
    private Property<TaggablePropertyKey> targetPropertyKey = null;

    public ExtractionRule(int patternGroupIndex, TaggablePropertyKey propertyKey) {
        this.setPatternGroupIndex(patternGroupIndex);
        this.setTargetPropertyKey(new SimpleObjectProperty<>(propertyKey));
    }

    public int getPatternGroupIndex() {
        return this.patternGroupIndex;
    }
    private void setPatternGroupIndex(int patternGroupIndex) {
        this.patternGroupIndex = patternGroupIndex;
    }

    public Property<TaggablePropertyKey> getTargetPropertyKey() {
        return this.targetPropertyKey;
    }
    private void setTargetPropertyKey(Property<TaggablePropertyKey> targetPropertyKey) {
        this.targetPropertyKey = targetPropertyKey;
    }

}
