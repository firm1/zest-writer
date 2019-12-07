package com.zestedesavoir.zestwriter.model;

import java.util.Objects;

public class MetadataContent {
    String id;
    String slug;
    String type;

    public MetadataContent(String id, String slug, String type) {
        this.id = id;
        this.slug = slug;
        this.type = type;
    }

    public boolean isArticle() {return "article".equalsIgnoreCase(type);}
    public boolean isOpinion() {return "opinion".equalsIgnoreCase(type);}
    public boolean isTutorial() {return "tutorial".equalsIgnoreCase(type);}

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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MetadataContent) {
                MetadataContent ob = (MetadataContent) obj;
                if(getId() == null) {
                        if(getType() == null) {
                                return ob.getId() == null && getSlug().equals(ob.getSlug()) && ob.getType() == null;
                            } else {
                                return ob.getId() == null && getSlug().equals(ob.getSlug()) && getType().equals(ob.getType());
                            }
                    } else {
                        if(getType() == null) {
                                return getId().equals(ob.getId()) && getSlug().equals(ob.getSlug()) && ob.getType() == null;
                            } else {
                                return getId().equals(ob.getId()) && getSlug().equals(ob.getSlug()) && getType().equals(ob.getType());
                            }
                    }
            }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug, type);
    }
}
