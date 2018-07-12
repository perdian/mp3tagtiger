/*
 * Copyright 2014-2018 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model;

import java.util.List;
import java.util.regex.Pattern;

public class Template {

    private String title = null;
    private Pattern pattern = null;
    private List<ExtractionRule> extractionRules = null;

    public Template(String title, Pattern compile, List<ExtractionRule> extractionRules) {
        this.setTitle(title);
        this.setPattern(compile);
        this.setExtractionRules(extractionRules);
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    public Pattern getPattern() {
        return this.pattern;
    }
    private void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<ExtractionRule> getExtractionRules() {
        return this.extractionRules;
    }
    private void setExtractionRules(List<ExtractionRule> extractionRules) {
        this.extractionRules = extractionRules;
    }

}
