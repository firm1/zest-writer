package com.zestedesavoir.zestwriter.contents.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.utils.api.ApiContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InternalMapper{
    private Logger logger = LoggerFactory.getLogger(InternalMapper.class);
    private ApiContentResponse content;

    public InternalMapper(String json){
        ObjectMapper mapper = new ObjectMapper();
        try{
            content = mapper.readValue(json, ApiContentResponse.class);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    public ApiContentResponse getContent(){
        return content;
    }
}
