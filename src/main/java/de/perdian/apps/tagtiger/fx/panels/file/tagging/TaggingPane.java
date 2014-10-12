/*
 * Copyright 2014 Christian Robert
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
package de.perdian.apps.tagtiger.fx.panels.file.tagging;

import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.fx.panels.file.FileDataPane;
import de.perdian.apps.tagtiger.fx.panels.file.FilePropertyControlFactory;

public class TaggingPane extends FileDataPane {

    public TaggingPane(TagTiger tagTiger) {
        super(tagTiger);
    }

    @Override
    protected void initializePane(FilePropertyControlFactory propertyFactory, TagTiger tagTiger) {

        this.setHgap(10);
        this.setVgap(5);

        TaggingGuiBuilder guiBuilder = new TaggingGuiBuilder(propertyFactory, tagTiger.getSelection(), tagTiger.getLocalization());
        guiBuilder.addTextfield(0, 3, tagTiger.getLocalization().title(), FieldKey.TITLE, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(1, 3, tagTiger.getLocalization().artist(), FieldKey.ARTIST, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(2, 2, tagTiger.getLocalization().album(), FieldKey.ALBUM, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(2, 1, tagTiger.getLocalization().year(), FieldKey.YEAR, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(3, 1, tagTiger.getLocalization().disc(), FieldKey.DISC_NO, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(3, 1, tagTiger.getLocalization().track(), FieldKey.TRACK, TaggingAction.CREATE_TRACK_LIST);
        guiBuilder.addTextfield(3, 1, tagTiger.getLocalization().tracks(), FieldKey.TRACK_TOTAL, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(4, 3, tagTiger.getLocalization().comment(), FieldKey.COMMENT, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(5, 3, tagTiger.getLocalization().composer(), FieldKey.COMPOSER, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.addTextfield(6, 3, tagTiger.getLocalization().originalArtist(), FieldKey.ORIGINAL_ARTIST, TaggingAction.COPY_TO_OTHERS);
        guiBuilder.applyTo(this);

    }

}