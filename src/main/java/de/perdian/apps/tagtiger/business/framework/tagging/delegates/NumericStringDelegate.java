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
package de.perdian.apps.tagtiger.business.framework.tagging.delegates;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import de.perdian.apps.tagtiger.business.framework.tagging.TagHandlerDelegate;

public class NumericStringDelegate implements TagHandlerDelegate {

    @Override
    public Property<Object> createPropertyForTag(Tag tag, FieldKey fieldKey, ChangeListener<Object> changeListener) {
        Property<Object> property = new SimpleObjectProperty<>();
        try {
            String tagValue = tag.getFirst(fieldKey);
            Integer integerValue = tagValue == null || tagValue.length() <= 0 ? null : Integer.valueOf(tagValue);
            property.setValue(integerValue == null ? null : integerValue.toString());
        } catch (Exception e) {
            // Ignore here
        }
        property.addListener(changeListener);
        return property;
    }

    @Override
    public void updateTagFromProperty(Tag tag, Property<Object> property, FieldKey fieldKey) throws Exception {
        try {
            String propertyValue = (String)property.getValue();
            Integer integerValue = propertyValue == null || propertyValue.length() <= 0 ? null : Integer.valueOf(propertyValue);
            if (integerValue == null) {
                tag.deleteField(fieldKey);
            } else {
                tag.setField(fieldKey, integerValue.toString());
            }
        } catch (Exception e) {
            try {
                tag.deleteField(fieldKey);
            } catch (Exception e2) {
                // Ignore here
            }
        }
    }

}