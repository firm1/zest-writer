package com.zestedesavoir.zestwriter.model;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zestedesavoir.zestwriter.utils.ContentSerializer;

public class Test {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Content content = mapper.readValue(new File("/home/willy/zwriter-workspace/offline/le-guide-du-contributeur/manifest.json"), Content.class);
            content.setBasePath("/home/willy/zwriter-workspace/offline/le-guide-du-contributeur");
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("/home/willy/zwriter-workspace/offline/le-guide-du-contributeur/manifest-old.json"), content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class ContentsSerializer extends JsonSerializer<Content>{

    @Override
    public void serialize(Content value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("object", value.getObject());
        gen.writeStringField("slug", value.getSlug());
        gen.writeStringField("title", value.getTitle());
        gen.writeStringField("introduction", ((MetaAttribute)value.getIntroduction()).getSlug());
        gen.writeStringField("conclusion", ((MetaAttribute)value.getConclusion()).getSlug());
        gen.writeArrayFieldStart("children");

    }

}
