package com.victorbg.racofib.data.model.notes;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Attachment {
    @SerializedName("tipus_mime")
    public String mime;

    @SerializedName("nom")
    public String name;

    @SerializedName("url")
    public String url;

    @SerializedName("mida")
    public float size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment)) return false;
        Attachment that = (Attachment) o;
        return Float.compare(that.size, size) == 0 &&
                Objects.equals(mime, that.mime) &&
                Objects.equals(name, that.name) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mime, name, url, size);
    }
}
