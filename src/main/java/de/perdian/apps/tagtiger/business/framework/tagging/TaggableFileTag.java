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
package de.perdian.apps.tagtiger.business.framework.tagging;

import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileTagDelegate.GenreTypeDelegate;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileTagDelegate.IntegerDelegate;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileTagDelegate.StringDelegate;

public enum TaggableFileTag {

    TITLE(FieldKey.TITLE, new StringDelegate(), TaggableFileTagGroupAction.COPY),
    ARTIST(FieldKey.ARTIST, new StringDelegate(), TaggableFileTagGroupAction.COPY),
    ALBUM(FieldKey.ALBUM, new StringDelegate(), TaggableFileTagGroupAction.COPY),
    DISC_NO(FieldKey.DISC_NO, new IntegerDelegate(), TaggableFileTagGroupAction.COPY),
    DISCS_TOTAL(FieldKey.DISC_TOTAL, new IntegerDelegate(), TaggableFileTagGroupAction.COPY),
    YEAR(FieldKey.YEAR, new StringDelegate(), TaggableFileTagGroupAction.COPY),
    TRACK_NO(FieldKey.TRACK, new IntegerDelegate(), TaggableFileTagGroupAction.GENERATE_FROM_POSITION),
    TRACKS_TOTAL(FieldKey.TRACK_TOTAL, new IntegerDelegate(), TaggableFileTagGroupAction.COPY),
    GENRE(FieldKey.GENRE, new GenreTypeDelegate(), TaggableFileTagGroupAction.COPY),
    COMMENT(FieldKey.COMMENT, new StringDelegate(), TaggableFileTagGroupAction.COPY),
    COMPOSER(FieldKey.COMPOSER, new StringDelegate(), TaggableFileTagGroupAction.COPY);

    private FieldKey fieldKey = null;
    private TaggableFileTagDelegate delegate = null;
    private TaggableFileTagGroupAction groupAction = null;

    private TaggableFileTag(FieldKey fieldKey, TaggableFileTagDelegate delegate, TaggableFileTagGroupAction groupAction) {
        this.setFieldKey(fieldKey);
        this.setDelegate(delegate);
        this.setGroupAction(groupAction);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    FieldKey getFieldKey() {
        return this.fieldKey;
    }
    private void setFieldKey(FieldKey fieldKey) {
        this.fieldKey = fieldKey;
    }

    TaggableFileTagDelegate getDelegate() {
        return this.delegate;
    }
    private void setDelegate(TaggableFileTagDelegate delegate) {
        this.delegate = delegate;
    }

    public TaggableFileTagGroupAction getGroupAction() {
        return this.groupAction;
    }
    private void setGroupAction(TaggableFileTagGroupAction groupAction) {
        this.groupAction = groupAction;
    }

}