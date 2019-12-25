/*
 * Copyright 2014-2017 Christian Seifert
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

import de.perdian.apps.tagtiger.fx.localization.Localization;

public enum TaggablePropertyKey {

    TITLE("title", Localization::title, FieldKey.TITLE, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ARTIST("artist", Localization::artist, FieldKey.ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ALBUM("album", Localization::album, FieldKey.ALBUM, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    ALBUM_ARTIST("albumArtist", Localization::albumArtist, FieldKey.ALBUM_ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    YEAR("year", Localization::year, FieldKey.YEAR, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    TRACK_NUMBER("track", Localization::track, FieldKey.TRACK, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    TRACKS_TOTAL("tracksTotal", Localization::tracks, FieldKey.TRACK_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    DISC_NUMBER("disc", Localization::disc, FieldKey.DISC_NO, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    DISCS_TOTAL("discsTotal", Localization::discs, FieldKey.DISC_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION),
    GENRE("genre", Localization::genre, FieldKey.GENRE, TaggablePropertyHelper.TO_PROPERTY_GENRE_CONVERTER_FUNCTION, TaggablePropertyHelper.TO_STORAGE_GENRE_CONVERTER_FUNCTION),
    COMMENT("comment", Localization::comment, FieldKey.COMMENT, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION),
    COMPOSER("composer", Localization::composer, FieldKey.COMPOSER, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION);

    private static final Logger log = LoggerFactory.getLogger(TaggablePropertyKey.class);
    private String name = null;
    private Function<Localization, String> titleKey = null;
    private FieldKey fieldKey = null;
    private Function<String, String> toPropertyConverterFunction = null;
    private Function<String, String> toStorageConverterFunction = null;

    private TaggablePropertyKey(String name, Function<Localization, String> titleKey, FieldKey fieldKey, Function<String, String> toPropertyConverterFunction, Function<String, String> toStorageConverterFunction) {
        this.setName(name);
        this.setTitleKey(titleKey);
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

    public String getName() {
        return this.name;
    }
    private void setName(String name) {
        this.name = name;
    }

    public Function<Localization, String> getTitleKey() {
        return this.titleKey;
    }
    private void setTitleKey(Function<Localization, String> titleKey) {
        this.titleKey = titleKey;
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
