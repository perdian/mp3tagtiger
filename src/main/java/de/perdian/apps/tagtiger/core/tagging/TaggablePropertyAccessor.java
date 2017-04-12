/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.core.tagging;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

interface TaggablePropertyAccessor<T> {

    static class FieldKeyPropertyAccessor implements TaggablePropertyAccessor<String> {

        private static final Logger log = LoggerFactory.getLogger(FieldKeyPropertyAccessor.class);
        private FieldKey fieldKey = null;
        private Function<String, String> toPropertyConverterFunction = null;
        private Function<String, String> toStorageConverterFunction = null;

        FieldKeyPropertyAccessor(FieldKey fieldKey, Function<String, String> toPropertyConverterFunction, Function<String, String> toStorageConverterFunction) {
            this.setFieldKey(fieldKey);
            this.setToPropertyConverterFunction(toPropertyConverterFunction);
            this.setToStorageConverterFunction(toStorageConverterFunction);
        }

        @Override
        public String readTagValue(Tag sourceTag) {
            return this.getToPropertyConverterFunction().apply(sourceTag.getFirst(this.getFieldKey()));
        }

        @Override
        public void writeValue(Tag targetTag, String value) {
            try {
                String convertedValue = this.getToStorageConverterFunction().apply(value);
                if (convertedValue == null || convertedValue.isEmpty()) {
                    targetTag.deleteField(this.getFieldKey());
                } else {
                    targetTag.setField(this.getFieldKey(), this.getToStorageConverterFunction().apply(convertedValue));
                }
            } catch (FieldDataInvalidException e) {
                log.info("Invalid value for field '{}': {}", this.getFieldKey().name(), value, e);
            }
        }

        @Override
        public void installChangeListener(Property<String> property, ChangeListener<Object> changeListener) {
            property.addListener((o, oldValue, newValue) -> {
                if (!Objects.equals(oldValue, newValue)) {
                    changeListener.changed(o, oldValue, newValue);
                }
            });
        }

        private FieldKey getFieldKey() {
            return this.fieldKey;
        }
        private void setFieldKey(FieldKey fieldKey) {
            this.fieldKey = fieldKey;
        }

        private Function<String, String> getToPropertyConverterFunction() {
            return this.toPropertyConverterFunction;
        }
        private void setToPropertyConverterFunction(Function<String, String> toPropertyConverterFunction) {
            this.toPropertyConverterFunction = toPropertyConverterFunction;
        }

        private Function<String, String> getToStorageConverterFunction() {
            return this.toStorageConverterFunction;
        }
        private void setToStorageConverterFunction(Function<String, String> toStorageConverterFunction) {
            this.toStorageConverterFunction = toStorageConverterFunction;
        }

    }

    static class ImagesPropertyAccessor implements TaggablePropertyAccessor<TagImageList> {

        @Override
        public TagImageList readTagValue(Tag sourceTag) {
            return new TagImageList(this.readTagImages(sourceTag));
        }

        private List<TagImage> readTagImages(Tag sourceTag) {
            if (sourceTag != null && sourceTag.getArtworkList() != null) {
                return sourceTag.getArtworkList().stream()
                    .map(this::readTagImage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }

        private TagImage readTagImage(Artwork artwork) {
            try {
                return new TagImage(artwork);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void writeValue(Tag targetTag, TagImageList value) {
        }

        @Override
        public void installChangeListener(Property<TagImageList> property, ChangeListener<Object> changeListener) {

            // First install the listener on the property itself
            property.addListener(changeListener);

            // Now install the change listener to all items within the image list
            property.getValue().changedProperty().addListener(changeListener);

        }

    }

    /**
     * Read the actual value from the underlying MP3 tag object
     */
    T readTagValue(Tag sourceTag);

    /**
     * Writes the value into the underlying tag
     */
    void writeValue(Tag targetTag, T value);

    /**
     * Installs the given change listener on the property
     */
    void installChangeListener(Property<T> property, ChangeListener<Object> changeListener);


}
