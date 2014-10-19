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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.tagging.TagImage;
import de.perdian.apps.tagtiger.business.framework.tagging.TagImageList;
import de.perdian.apps.tagtiger.business.framework.tagging.TagHandlerDelegate;

public class TagImageListDelegate implements TagHandlerDelegate {

    static final Logger log = LoggerFactory.getLogger(TagImageListDelegate.class);

    @Override
    public Property<Object> createPropertyForTag(Tag tag, FieldKey fieldKey, ChangeListener<Object> changeListener) {

        List<TagImage> tagImages = Optional.ofNullable(tag.getArtworkList()).orElse(Collections.emptyList()).stream()
            .map(artwork -> {
                try {
                    TagImage tagImage = new TagImage(artwork);
                    tagImage.changedProperty().addListener(changeListener);
                    return tagImage;
                } catch(Exception e) {
                    return null;
                }
            })
            .filter(image -> image != null)
            .collect(Collectors.toList());

        return new SimpleObjectProperty<>(new TagImageList(tagImages, changeListener));

    }

    @Override
    public void updateTagFromProperty(Tag tag, Property<Object> property, FieldKey fieldKey) throws Exception {
        TagImageList imageList = (TagImageList)property.getValue();
        if (imageList.changedProperty().get()) {

            // Delete the artwork so that we'll re-add it avain
            tag.deleteArtworkField();

            imageList.getTagImages().forEach(tagImage -> {
                try {
                    Artwork artwork = tagImage.toArtwork();
                    if (artwork != null) {
                        tag.addField(artwork);
                    }
                } catch (Exception e) {
                    log.warn("Cannot update artwork with image value: " + property.getValue(), e);
                }
            });

        }
    }

    @Override
    public void copyPropertyValue(Property<Object> sourceProperty, Property<Object> targetProperty) {
        TagImageList sourceImageList = (TagImageList)sourceProperty.getValue();
        TagImageList targetImageList = (TagImageList)sourceProperty.getValue();
        targetImageList.getTagImages().setAll(sourceImageList.getTagImages());
    }

}