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

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface TaggableFileTagDelegate {

    static final Logger log = LoggerFactory.getLogger(TaggableFileTagDelegate.class);

    void tagToProperty(Tag audioTag, Property<Object> fileTagProperty, FieldKey fieldKey);
    void propertyToTag(Property<Object> fileProperty, Tag tag, FieldKey fieldKey);

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class StringDelegate implements TaggableFileTagDelegate {

        @Override
        public void tagToProperty(Tag audioTag, Property<Object> property, FieldKey fieldKey) {
            property.setValue(audioTag.getFirst(fieldKey));
        }

        @Override
        public void propertyToTag(Property<Object> fileProperty, Tag tag, FieldKey fieldKey) {
            try {
                tag.setField(fieldKey, (String)fileProperty.getValue());
            } catch(Exception e) {
                log.warn("Cannot update field '" + fieldKey + "' with value: " + fileProperty.getValue(), e);
            }
        }

    }


}