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
import org.jaudiotagger.tag.images.StandardArtwork;
import org.jaudiotagger.tag.reference.PictureTypes;

public class SongImage implements Serializable {

    static final long serialVersionUID = 1L;

    private String description = null;
    private String type = null;
    private byte[] bytes = null;
    private String mimeType = null;

    public SongImage(String description, String type, byte[] bytes, String mimeType) {
        this.setDescription(description);
        this.setType(type);
        this.setBytes(bytes);
        this.setMimeType(mimeType);
    }

    SongImage(Artwork artwork) {
        this.setDescription(artwork.getDescription());
        this.setType(PictureTypes.getInstanceOf().getIdToValueMap().get(artwork.getPictureType()));
        this.setBytes(artwork.getBinaryData());
        this.setMimeType(artwork.getMimeType());
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
            equalsBuilder.append(this.getMimeType(), ((SongImage)that).getMimeType());
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
        hashCodeBuilder.append(this.getMimeType());
        return hashCodeBuilder.toHashCode();
    }

    Artwork toArtwork() {
        Integer pictureTypeId = PictureTypes.getInstanceOf().getIdForValue(this.getType());
        Artwork artwork = new StandardArtwork();
        artwork.setBinaryData(this.getBytes());
        artwork.setDescription(this.getDescription());
        artwork.setMimeType(this.getMimeType());
        artwork.setPictureType(pictureTypeId == null ? PictureTypes.DEFAULT_ID : pictureTypeId);
        return artwork;
    }

    public SongImage withDescription(String description) {
        return new SongImage(description, this.getType(), this.getBytes(), this.getMimeType());
    }
    public String getDescription() {
        return this.description;
    }
    private void setDescription(String description) {
        this.description = description;
    }

    public SongImage withType(String type) {
        return new SongImage(this.getDescription(), type, this.getBytes(), this.getMimeType());
    }
    public String getType() {
        return this.type;
    }
    private void setType(String type) {
        this.type = type;
    }

    public SongImage withBytes(byte[] bytes, String mimeType) {
        return new SongImage(this.getDescription(), this.getType(), bytes, mimeType);
    }
    public byte[] getBytes() {
        return this.bytes;
    }
    private void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getMimeType() {
        return this.mimeType;
    }
    private void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
