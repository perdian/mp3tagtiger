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

import java.util.function.Function;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TaggablePropertyKey {

    TITLE(FieldKey.TITLE, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ARTIST(FieldKey.ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ALBUM(FieldKey.ALBUM, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ALBUM_ARTIST(FieldKey.ALBUM_ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    YEAR(FieldKey.YEAR, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    TRACK_NUMBER(FieldKey.TRACK, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    TRACKS_TOTAL(FieldKey.TRACK_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    DISC_NUMBER(FieldKey.DISC_NO, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    DISCS_TOTAL(FieldKey.DISC_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    GENRE(FieldKey.GENRE, TaggablePropertyHelper.TO_PROPERTY_GENRE_CONVERTER_FUNCTION, TaggablePropertyHelper.TO_STORAGE_GENRE_CONVERTER_FUNCTION),
    COMMENT(FieldKey.COMMENT, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    COMPOSER(FieldKey.COMPOSER, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION);

    private static final Logger log = LoggerFactory.getLogger(TaggablePropertyKey.class);
    private FieldKey fieldKey = null;
    private Function<String, String> toPropertyConverterFunction = null;
    private Function<String, String> toStorageConverterFunction = null;

    private TaggablePropertyKey(FieldKey fieldKey, Function<String, String> toPropertyConverterFunction, Function<String, String> toStorageConverterFunction) {
        this.setFieldKey(fieldKey);
        this.setToPropertyConverterFunction(toPropertyConverterFunction);
        this.setToStorageConverterFunction(toStorageConverterFunction);
    }

    public String readTagValue(Tag sourceTag) {
        return this.getToPropertyConverterFunction().apply(sourceTag.getFirst(this.getFieldKey()));
    }

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
