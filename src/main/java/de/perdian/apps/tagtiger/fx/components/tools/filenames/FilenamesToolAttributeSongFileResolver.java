/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.components.tools.filenames;

import java.util.Optional;

import de.perdian.apps.tagtiger.model.SongAttribute;
import de.perdian.apps.tagtiger.model.SongFile;

public interface FilenamesToolAttributeSongFileResolver {

    Optional<String> resolveAttributeValue(SongAttribute attribute, SongFile songFile);

    static class SimpleResolver implements FilenamesToolAttributeSongFileResolver {

        @Override
        public Optional<String> resolveAttributeValue(SongAttribute attribute, SongFile songFile) {
            return Optional.of(songFile.getAttributeValueProperty(attribute, String.class).getValue());
        }

    }

    static class NumericResolver implements FilenamesToolAttributeSongFileResolver {

        private SongAttribute maxValueAttribute = null;
        private String prefixCharacter = null;

        NumericResolver(SongAttribute maxValueAttribute, String prefixCharacter) {
            this.setMaxValueAttribute(maxValueAttribute);
            this.setPrefixCharacter(prefixCharacter);
        }

        @Override
        public Optional<String> resolveAttributeValue(SongAttribute attribute, SongFile songFile) {
            StringBuilder attributeValue = new StringBuilder(songFile.getAttributeValueProperty(attribute, String.class).getValue());
            int maxValueLength = songFile.getAttributeValueProperty(this.getMaxValueAttribute(), String.class).getValue().length();
            while (attributeValue.length() < maxValueLength) {
                attributeValue.insert(0, this.getPrefixCharacter());
            }
            return Optional.of(attributeValue.toString());
        }

        private SongAttribute getMaxValueAttribute() {
            return this.maxValueAttribute;
        }
        private void setMaxValueAttribute(SongAttribute maxValueAttribute) {
            this.maxValueAttribute = maxValueAttribute;
        }

        private String getPrefixCharacter() {
            return this.prefixCharacter;
        }
        private void setPrefixCharacter(String prefixCharacter) {
            this.prefixCharacter = prefixCharacter;
        }

    }

}
