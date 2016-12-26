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

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public boolean isArticle() {return "article".equalsIgnoreCase(type);}

    public boolean isTutorial() {return "tutorial".equalsIgnoreCase(type);}

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
        return super.hashCode();
    }

}
