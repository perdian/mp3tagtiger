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

import org.jaudiotagger.tag.reference.GenreTypes;

class TaggablePropertyHelper {

    static final Function<String, String> TO_LENIENT_INTEGER_FUNCTION = new Function<>() {

        @Override
        public String apply(String value) {
            try {
                return value == null || value.isEmpty() ? null : Integer.valueOf(value.trim()).toString();
            } catch (Exception e) {
                return null;
            }
        }

    };

    static final Function<String, String> TO_STRING_FUNCTION = new Function<>() {

        @Override
        public String apply(String value) {
            return value;
        }

    };

    static final Function<String, String> TO_PROPERTY_GENRE_CONVERTER_FUNCTION = new Function<>() {

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

    };

    static final Function<String, String> TO_STORAGE_GENRE_CONVERTER_FUNCTION = new Function<>() {

        @Override
        public String apply(String sourceValue) {
            Integer idForValue = sourceValue == null ? null : GenreTypes.getInstanceOf().getIdForName(sourceValue);
            return idForValue != null ? ("(" + idForValue.toString() + ")") : sourceValue;
        }

    };

}
