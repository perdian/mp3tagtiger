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
package de.perdian.apps.tagtiger.fx.panels.editor;

import javafx.scene.control.TextField;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.editor.properties.PropertiesPaneBuilder;

class FileTagsPane extends FileDataPanel {

    FileTagsPane(TagTiger tagTiger) {
        super(tagTiger);
    }

    @Override
    protected void initializePane(EditorPropertyFactory propertyFactory, TagTiger tagTiger) {

        this.setHgap(10);
        this.setVgap(5);

        PropertiesPaneBuilder paneBuilder = new PropertiesPaneBuilder(tagTiger.getSelection());
        paneBuilder.add(0, 2, null, tagTiger.getLocalization().title(), TaggableFile::getTagTitle, () -> propertyFactory.createTextField(TaggableFile::getTagTitle), control -> ((TextField)control).textProperty(), true);
        paneBuilder.add(1, 2, null, tagTiger.getLocalization().artist(),TaggableFile::getTagArtist,  () -> propertyFactory.createTextField(TaggableFile::getTagArtist), control -> ((TextField)control).textProperty(), true);
        paneBuilder.add(2, 1, null, tagTiger.getLocalization().album(), TaggableFile::getTagAlbum, () -> propertyFactory.createTextField(TaggableFile::getTagAlbum), control -> ((TextField)control).textProperty(), true);
        paneBuilder.add(2, 1, 66d, tagTiger.getLocalization().year(), TaggableFile::getTagYear, () -> propertyFactory.createTextField(TaggableFile::getTagYear), control -> ((TextField)control).textProperty(), true);
        paneBuilder.applyTo(this);

    }

}