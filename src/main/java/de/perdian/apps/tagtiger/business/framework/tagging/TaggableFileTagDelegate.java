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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.Property;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface TaggableFileTagDelegate {

    static final Logger log = LoggerFactory.getLogger(TaggableFileTagDelegate.class);

    void tagToProperty(Tag tag, Property<Object> property, FieldKey fieldKey);
    void propertyToTag(Property<Object> property, Tag tag, FieldKey fieldKey);

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class StringDelegate implements TaggableFileTagDelegate {

        @Override
        public void tagToProperty(Tag tag, Property<Object> property, FieldKey fieldKey) {
            property.setValue(tag.getFirst(fieldKey));
        }

        @Override
        public void propertyToTag(Property<Object> property, Tag tag, FieldKey fieldKey) {
            try {
                String propertyValue = (String)property.getValue();
                if(propertyValue == null || propertyValue.length() <= 0) {
                    tag.deleteField(fieldKey);
                } else {
                    tag.setField(fieldKey, propertyValue);
                }
            } catch (Exception e) {
                log.warn("Cannot update field '" + fieldKey + "' with value: " + property.getValue(), e);
            }
        }

    }

    static class IntegerDelegate implements TaggableFileTagDelegate {

        @Override
        public void tagToProperty(Tag tag, Property<Object> property, FieldKey fieldKey) {
            try {
                String tagValue = tag.getFirst(fieldKey);
                Integer integerValue = tagValue == null || tagValue.length() <= 0 ? null : Integer.valueOf(tagValue);
                property.setValue(integerValue == null ? null : integerValue.toString());
            } catch (Exception e) {
                property.setValue(null);
            }
        }

        @Override
        public void propertyToTag(Property<Object> property, Tag tag, FieldKey fieldKey) {
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

    static class GenreTypeDelegate implements TaggableFileTagDelegate {

        @Override
        public void tagToProperty(Tag tag, Property<Object> property, FieldKey fieldKey) {
            String tagValue = tag.getFirst(fieldKey);
            Pattern idPattern = Pattern.compile("\\(([0-9]+)\\)");
            Matcher idMatcher = idPattern.matcher(tagValue == null ? "" : tagValue);
            if (idMatcher.matches()) {
                int idValue = Integer.parseInt(idMatcher.group(1));
                property.setValue(GenreTypes.getInstanceOf().getValueForId(idValue));
            } else {
                property.setValue(tagValue);
            }
        }

        @Override
        public void propertyToTag(Property<Object> property, Tag tag, FieldKey fieldKey) {
            try {
                String fileTagValue = (String)property.getValue();
                if (fileTagValue == null || fileTagValue.length() <= 0) {
                    tag.deleteField(fieldKey);
                } else {
                    Integer idForValue = GenreTypes.getInstanceOf().getIdForName(fileTagValue);
                    tag.setField(fieldKey, idForValue != null ? ("(" + idForValue.toString() + ")") : fileTagValue);
                }
            } catch (Exception e) {
                log.warn("Cannot update field '" + fieldKey + "' with value: " + property.getValue(), e);
            }
        }

    }

}