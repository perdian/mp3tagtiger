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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static helper methods to centralize reading properties from tags and writing
 * properties into tags.
 *
 * @author Christian Robert
 */

class TaggableFileHelper {

    private static final Logger log = LoggerFactory.getLogger(TaggableFileHelper.class);

    static ChangeListener<Object> createMarkDirtyChangeListener(BooleanProperty targetProperty) {
        return (o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                targetProperty.setValue(true);
            }
        };
    }

    static void initializePropertyFromStringValue(Property<String> targetProperty, String value, ChangeListener<Object> markAsDirtyChangeListener) {
        targetProperty.setValue(value);
        targetProperty.addListener(markAsDirtyChangeListener);
    }

    static void initializePropertyFromStringTag(Property<String> targetProperty, Tag sourceTag, FieldKey fieldKey, ChangeListener<Object> markAsDirtyChangeListener) {
        TaggableFileHelper.initializePropertyFromStringValue(targetProperty, sourceTag.getFirst(fieldKey), markAsDirtyChangeListener);
    }

    static void initializePropertyFromGenreTag(Property<String> targetProperty, Tag sourceTag, FieldKey fieldKey, ChangeListener<Object> markAsDirtyChangeListener) {
        String tagValue = sourceTag.getFirst(fieldKey);
        Pattern idPattern = Pattern.compile("\\(([0-9]+)\\)");
        Matcher idMatcher = idPattern.matcher(tagValue == null ? "" : tagValue);
        if (idMatcher.matches()) {
            int idValue = Integer.parseInt(idMatcher.group(1));
            TaggableFileHelper.initializePropertyFromStringValue(targetProperty, GenreTypes.getInstanceOf().getValueForId(idValue), markAsDirtyChangeListener);
        } else {
            TaggableFileHelper.initializePropertyFromStringValue(targetProperty, tagValue, markAsDirtyChangeListener);
            targetProperty.setValue(tagValue);
        }
    }

    static void initializePropertyFromImagesTag(Property<TagImageList> targetProperty, Tag sourceTag, ChangeListener<Object> markAsDirtyChangeListener) {

        List<TagImage> tagImages = Optional.ofNullable(sourceTag.getArtworkList()).orElse(Collections.emptyList()).stream().map(artwork -> {
            try {
                TagImage tagImage = new TagImage(artwork);
                tagImage.changedProperty().addListener(markAsDirtyChangeListener);
                return tagImage;
            } catch (Exception e) {
                return null;
            }
        }).filter(image -> image != null).collect(Collectors.toList());

        targetProperty.setValue(new TagImageList(tagImages, markAsDirtyChangeListener));

    }

    static void updateTagFromStringValue(Tag targetTag, FieldKey fieldKey, String sourceValue) throws Exception {
        if (sourceValue == null || sourceValue.length() <= 0) {
            targetTag.deleteField(fieldKey);
        } else {
            targetTag.setField(fieldKey, sourceValue);
        }
    }

    static void updateTagFromStringProperty(Tag targetTag, FieldKey fieldKey, Property<String> sourceProperty) throws Exception {
        TaggableFileHelper.updateTagFromStringValue(targetTag, fieldKey, sourceProperty.getValue());
    }

    static void updateTagFromGenreProperty(Tag targetTag, Property<String> sourceProperty) throws Exception {
        String sourceValue = sourceProperty.getValue();
        Integer idForValue = sourceValue == null ? null : GenreTypes.getInstanceOf().getIdForName(sourceValue);
        String resolvedValue = idForValue != null ? ("(" + idForValue.toString() + ")") : sourceValue;
        TaggableFileHelper.updateTagFromStringValue(targetTag, FieldKey.GENRE, resolvedValue);
    }

    static void updateTagFromTagImageListProperty(Tag targetTag, Property<TagImageList> property) throws Exception {
        TagImageList imageList = property.getValue();
        if (imageList.changedProperty().get()) {

            // Delete the artwork so that we'll re-add it avain
            targetTag.deleteArtworkField();

            imageList.getTagImages().forEach(tagImage -> {
                try {
                    Artwork artwork = tagImage.toArtwork();
                    if (artwork != null) {
                        targetTag.addField(artwork);
                    }
                } catch (Exception e) {
                    log.warn("Cannot update artwork with image value: " + property.getValue(), e);
                }
            });

        }
    }

}