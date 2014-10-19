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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.tagging.TagHandlerDelegate;

public class GenreTypeDelegate implements TagHandlerDelegate {

    static final Logger log = LoggerFactory.getLogger(GenreTypeDelegate.class);

    @Override
    public Property<Object> createPropertyForTag(Tag tag, FieldKey fieldKey, ChangeListener<Object> changeListener) {
        SimpleObjectProperty<Object> property = new SimpleObjectProperty<>();
        String tagValue = tag.getFirst(fieldKey);
        Pattern idPattern = Pattern.compile("\\(([0-9]+)\\)");
        Matcher idMatcher = idPattern.matcher(tagValue == null ? "" : tagValue);
        if (idMatcher.matches()) {
            int idValue = Integer.parseInt(idMatcher.group(1));
            property.setValue(GenreTypes.getInstanceOf().getValueForId(idValue));
        } else {
            property.setValue(tagValue);
        }
        property.addListener(changeListener);
        return property;
    }

    @Override
    public void updateTagFromProperty(Tag tag, Property<Object> property, FieldKey fieldKey) throws Exception {
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