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
package de.perdian.apps.tagtiger.business.framework.tagging;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public interface TaggableFileTagDelegate {

    void updateTagFromProperty(Tag tag, Property<Object> property, FieldKey fieldKey) throws Exception;

    Property<Object> createPropertyForTag(Tag tag, FieldKey fieldKey, ChangeListener<Object> changeListener);

    default void copyPropertyValue(Property<Object> sourceProperty, Property<Object> targetProperty) {
        targetProperty.setValue(sourceProperty.getValue());
    }

}