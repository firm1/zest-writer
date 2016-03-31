package com.zestedesavoir.zestwriter.model;

public class MetadataContent {
    String id;
    String slug;
    String type;

    public MetadataContent(String id, String slug, String type) {
        super();
        this.id = id;
        this.slug = slug;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getSlug();
    }
}
