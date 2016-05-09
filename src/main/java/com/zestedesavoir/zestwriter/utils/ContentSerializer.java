package com.zestedesavoir.zestwriter.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.MetaAttribute;

import java.io.IOException;

public class ContentSerializer extends JsonSerializer<Container>{

    @Override
    public void serialize(Container value, JsonGenerator gen, SerializerProvider serializers)
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