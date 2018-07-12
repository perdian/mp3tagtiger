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
package de.perdian.apps.tagtiger.fx.modules.tools.updatefilenames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.text.StringSubstitutor;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

class UpdateFileNamesComputeChangeListener implements ChangeListener<String> {

    private Map<TaggablePropertyKey, Function<String, String>> valueConverterFunctions = null;
    private List<UpdateFileNamesItem> items = null;

    UpdateFileNamesComputeChangeListener(List<UpdateFileNamesItem> items) {

        Map<TaggablePropertyKey, Function<String, String>> valueConverterFunctions = new HashMap<>();
        valueConverterFunctions.put(TaggablePropertyKey.TRACK_NUMBER, new ResolveTracksTotalValueFunction());
        this.setValueConverterFunctions(valueConverterFunctions);
        this.setItems(items);

    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldFileNamePattern, String newFileNamePattern) {
        for (UpdateFileNamesItem item : this.getItems()) {

            Map<String, String> replacementValues = new HashMap<>();
            for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {
                String sourceValue = item.getFile().property(propertyKey).getValue();
                Function<String, String> converterFunction = this.getValueConverterFunctions().get(propertyKey);
                String convertedValue = converterFunction == null ? sourceValue : converterFunction.apply(sourceValue);
                replacementValues.put(propertyKey.getName(), convertedValue);
            }

            StringSubstitutor substitutor = new StringSubstitutor(replacementValues);
            String substitutionEvaluationResult = substitutor.replace(newFileNamePattern);
            String substitutionSanitizedResult = this.sanitizeFileName(substitutionEvaluationResult);

            if (substitutionSanitizedResult != null && !substitutionSanitizedResult.isEmpty() && !Objects.equals(substitutionSanitizedResult, item.getNewFileName().getValue())) {
                item.getNewFileName().setValue(substitutionSanitizedResult);
            }

        }
    }

    private String sanitizeFileName(String fileName) {
        StringBuilder result = new StringBuilder(fileName.length());
        boolean lastCharacterWasIllegal = false;
        String invalidCharacters = ":/\\";
        for (char c : fileName.toCharArray()) {
            if (invalidCharacters.indexOf(c) > -1) {
                if(!lastCharacterWasIllegal) {
                    lastCharacterWasIllegal = true;
                    result.append("_");
                }
            } else {
                lastCharacterWasIllegal = false;
                result.append(c);
            }
        }
        return result.toString().trim();
    }

    class ResolveTracksTotalValueFunction implements Function<String, String> {

        @Override
        public String apply(String value) {
            StringBuilder resultValue = new StringBuilder(value == null ? "" : value);
            for (UpdateFileNamesItem item : UpdateFileNamesComputeChangeListener.this.getItems()) {
                String itemTrackValue = item.getFile().property(TaggablePropertyKey.TRACK_NUMBER).getValue();
                int itemTrackValueLength = itemTrackValue == null ? 0 : itemTrackValue.length();
                while (resultValue.length() < itemTrackValueLength) {
                    resultValue.insert(0, "0");
                }
            }
            return resultValue.toString();
        }

    }


    Map<TaggablePropertyKey, Function<String, String>> getValueConverterFunctions() {
        return this.valueConverterFunctions;
    }
    private void setValueConverterFunctions(Map<TaggablePropertyKey, Function<String, String>> valueConverterFunctions) {
        this.valueConverterFunctions = valueConverterFunctions;
    }

    List<UpdateFileNamesItem> getItems() {
        return this.items;
    }
    private void setItems(List<UpdateFileNamesItem> items) {
        this.items = items;
    }

}
