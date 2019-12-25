/*
 * Copyright 2014-2018 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.ExtractionRule;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.Template;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class TemplatesPane extends FlowPane {

    public TemplatesPane(ObservableList<ExtractionRule> extractionRules, Property<Pattern> regexPattern, Localization localization) {

        List<Template> templates = new ArrayList<>();
        templates.add(new Template("Title (Artist)", Pattern.compile("(.*?) \\((.*?)\\)"), List.of(new ExtractionRule(1, TaggablePropertyKey.TITLE), new ExtractionRule(2, TaggablePropertyKey.ARTIST))));
        templates.add(new Template("Artist - Title", Pattern.compile("(.*?) - (.*?)"), List.of(new ExtractionRule(1, TaggablePropertyKey.ARTIST), new ExtractionRule(2, TaggablePropertyKey.TITLE))));

        for (Template template : templates) {
            Button button = new Button(template.getTitle());
            button.setOnAction(action -> {
                regexPattern.setValue(template.getPattern());
                extractionRules.setAll(template.getExtractionRules());
            });
            this.getChildren().add(button);
        }

        this.setHgap(5);
        this.setAlignment(Pos.CENTER);

    }

}
