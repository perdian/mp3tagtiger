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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.reference.GenreTypes;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyAccessor.FieldKeyPropertyAccessor;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyAccessor.ImagesPropertyAccessor;

public enum TaggableProperty {

    TITLE(new FieldKeyPropertyAccessor(FieldKey.TITLE, Function.identity(), Function.identity())),
    ARTIST(new FieldKeyPropertyAccessor(FieldKey.ARTIST, Function.identity(), Function.identity())),
    ALBUM(new FieldKeyPropertyAccessor(FieldKey.ALBUM, Function.identity(), Function.identity())),
    ALBUM_ARTIST(new FieldKeyPropertyAccessor(FieldKey.ALBUM_ARTIST, Function.identity(), Function.identity())),
    YEAR(new FieldKeyPropertyAccessor(FieldKey.YEAR, Function.identity(), Function.identity())),
    TRACK_NUMBER(new FieldKeyPropertyAccessor(FieldKey.TRACK, Function.identity(), Function.identity())),
    TRACKS_TOTAL(new FieldKeyPropertyAccessor(FieldKey.TRACK_TOTAL, Function.identity(), Function.identity())),
    DISC_NUMBER(new FieldKeyPropertyAccessor(FieldKey.DISC_NO, Function.identity(), Function.identity())),
    DISCS_TOTAL(new FieldKeyPropertyAccessor(FieldKey.DISC_TOTAL, Function.identity(), Function.identity())),
    GENRE(new FieldKeyPropertyAccessor(FieldKey.GENRE, new ToPropertyGenreConverterFunction(), new ToStorageGenreConverterFunction())),
    COMMENT(new FieldKeyPropertyAccessor(FieldKey.COMMENT, Function.identity(), Function.identity())),
    COMPOSER(new FieldKeyPropertyAccessor(FieldKey.COMPOSER, Function.identity(), Function.identity())),
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

    static class ToPropertyGenreConverterFunction implements Function<String, String> {

        @Override
        public String apply(String tagValue) {
            Pattern idPattern = Pattern.compile("\\(([0-9]+)\\)");
            Matcher idMatcher = idPattern.matcher(tagValue == null ? "" : tagValue);
            if (idMatcher.matches()) {
                return GenreTypes.getInstanceOf().getValueForId(Integer.parseInt(idMatcher.group(1)));
            } else {
                return tagValue;
            }
        }

    }

    static class ToStorageGenreConverterFunction implements Function<String, String> {

        @Override
        public String apply(String sourceValue) {
            Integer idForValue = sourceValue == null ? null : GenreTypes.getInstanceOf().getIdForName(sourceValue);
            return idForValue != null ? ("(" + idForValue.toString() + ")") : sourceValue;
        }

    }

}
