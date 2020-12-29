/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jaudiotagger.tag.images.Artwork;

public class SongImage implements Serializable {

    static final long serialVersionUID = 1L;

    private String description = null;
    private SongImageType type = null;
    private byte[] bytes = null;

    public SongImage(String description, SongImageType type, byte[] bytes) {
        this.setDescription(description);
        this.setType(type);
        this.setBytes(bytes);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof SongImage) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getDescription(), ((SongImage)that).getDescription());
            equalsBuilder.append(this.getType(), ((SongImage)that).getType());
            equalsBuilder.append(this.getBytes(), ((SongImage)that).getBytes());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getDescription());
        hashCodeBuilder.append(this.getType());
        hashCodeBuilder.append(this.getBytes());
        return hashCodeBuilder.toHashCode();
    }

    Artwork toArtwork() {
        return null;
    }

    public SongImage withDescription(String description) {
        return new SongImage(description, this.getType(), this.getBytes());
    }
    public String getDescription() {
        return this.description;
    }
    private void setDescription(String description) {
        this.description = description;
    }

    public SongImage withType(SongImageType type) {
        return new SongImage(this.getDescription(), type, this.getBytes());
    }
    public SongImageType getType() {
        return this.type;
    }
    private void setType(SongImageType type) {
        this.type = type;
    }

    public SongImage withBytes(byte[] bytes) {
        return new SongImage(this.getDescription(), this.getType(), bytes);
    }
    public byte[] getBytes() {
        return this.bytes;
    }
    private void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
