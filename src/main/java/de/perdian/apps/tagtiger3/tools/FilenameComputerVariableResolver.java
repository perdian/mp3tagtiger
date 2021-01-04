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
package de.perdian.apps.tagtiger3.tools;

import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.model.SongFile;

interface FilenameComputerVariableResolver {

    String resolveVariableValue(SongFile songFile);

    static class SimpleAttributeResolver implements FilenameComputerVariableResolver {

        private SongAttribute attribute = null;

        SimpleAttributeResolver(SongAttribute attribute) {
            this.setAttribute(attribute);
        }

        @Override
        public String resolveVariableValue(SongFile songFile) {
            return songFile.getAttributeValueProperty(this.getAttribute(), String.class).getValue();
        }

        private SongAttribute getAttribute() {
            return this.attribute;
        }
        private void setAttribute(SongAttribute attribute) {
            this.attribute = attribute;
        }

    }

    static class NumericAttributeResolver implements FilenameComputerVariableResolver {

        private SongAttribute attribute = null;
        private SongAttribute maxValueAttribute = null;
        private String prefixCharacter = null;

        NumericAttributeResolver(SongAttribute attribute, SongAttribute maxValueAttribute, String prefixCharacter) {
            this.setAttribute(attribute);
            this.setMaxValueAttribute(maxValueAttribute);
            this.setPrefixCharacter(prefixCharacter);
        }

        @Override
        public String resolveVariableValue(SongFile songFile) {
            StringBuilder attributeValue = new StringBuilder(songFile.getAttributeValueProperty(this.getAttribute(), String.class).getValue());
            int maxValueLength = songFile.getAttributeValueProperty(this.getMaxValueAttribute(), String.class).getValue().length();
            while (attributeValue.length() < maxValueLength) {
                attributeValue.insert(0, this.getPrefixCharacter());
            }
            return attributeValue.toString();
        }

        private SongAttribute getAttribute() {
            return this.attribute;
        }
        private void setAttribute(SongAttribute attribute) {
            this.attribute = attribute;
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
