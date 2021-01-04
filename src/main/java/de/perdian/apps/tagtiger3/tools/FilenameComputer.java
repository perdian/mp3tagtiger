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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import de.perdian.apps.tagtiger3.model.SongFile;

public class FilenameComputer {

    public Optional<String> computeFilename(String pattern, SongFile songFile) {

        Map<String, String> replacementValues = Arrays.stream(FilenameComputerVariable.values())
            .collect(Collectors.toMap(variable -> variable.name().toLowerCase(), variable -> variable.getVariableResolver().resolveVariableValue(songFile)));

        StringSubstitutor substitutor = new StringSubstitutor(replacementValues);
        String substitutionEvaluationResult = substitutor.replace(pattern);
        String substitutionSanitizedResult = this.sanitizeFileName(substitutionEvaluationResult);
        return StringUtils.isEmpty(substitutionSanitizedResult) ? Optional.empty() : Optional.of(substitutionSanitizedResult);

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

}
