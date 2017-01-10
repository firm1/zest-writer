package com.zestedesavoir.zestwriter.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class MetadataContent {
    String id;
    String slug;
    String type;

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
        return Objects.hash(id, slug, type);
    }
}
