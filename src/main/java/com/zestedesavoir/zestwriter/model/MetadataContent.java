package com.zestedesavoir.zestwriter.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MetadataContent {
    String id;
    String slug;
    String type;

    public boolean isArticle() {return "article".equalsIgnoreCase(type);}
    public boolean isTutorial() {return "tutorial".equalsIgnoreCase(type);}
}
