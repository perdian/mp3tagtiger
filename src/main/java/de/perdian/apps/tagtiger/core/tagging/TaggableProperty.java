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

import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyAccessor.FieldKeyPropertyAccessor;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyAccessor.ImagesPropertyAccessor;

public enum TaggableProperty {

    TITLE(new FieldKeyPropertyAccessor(FieldKey.TITLE, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    ARTIST(new FieldKeyPropertyAccessor(FieldKey.ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    ALBUM(new FieldKeyPropertyAccessor(FieldKey.ALBUM, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    ALBUM_ARTIST(new FieldKeyPropertyAccessor(FieldKey.ALBUM_ARTIST, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    YEAR(new FieldKeyPropertyAccessor(FieldKey.YEAR, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION)),
    TRACK_NUMBER(new FieldKeyPropertyAccessor(FieldKey.TRACK, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION)),
    TRACKS_TOTAL(new FieldKeyPropertyAccessor(FieldKey.TRACK_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION)),
    DISC_NUMBER(new FieldKeyPropertyAccessor(FieldKey.DISC_NO, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION)),
    DISCS_TOTAL(new FieldKeyPropertyAccessor(FieldKey.DISC_TOTAL, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION, TaggablePropertyHelper.TO_LENIENT_INTEGER_FUNCTION)),
    GENRE(new FieldKeyPropertyAccessor(FieldKey.GENRE, TaggablePropertyHelper.TO_PROPERTY_GENRE_CONVERTER_FUNCTION, TaggablePropertyHelper.TO_STORAGE_GENRE_CONVERTER_FUNCTION)),
    COMMENT(new FieldKeyPropertyAccessor(FieldKey.COMMENT, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    COMPOSER(new FieldKeyPropertyAccessor(FieldKey.COMPOSER, TaggablePropertyHelper.TO_STRING_FUNCTION, TaggablePropertyHelper.TO_STRING_FUNCTION)),
    IMAGES(new ImagesPropertyAccessor());

    private TaggablePropertyAccessor<?> propertyAccessor = null;

    private TaggableProperty(TaggablePropertyAccessor<?> propertyAccessor) {
        this.setPropertyAccessor(propertyAccessor);
    }

    TaggablePropertyAccessor<?> getPropertyAccessor() {
        return this.propertyAccessor;
    }
    private void setPropertyAccessor(TaggablePropertyAccessor<?> propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }

}
