package com.zestedesavoir.zestwriter.utils.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApiMapper{
    private Logger logger = LoggerFactory.getLogger(ApiMapper.class);
    private ApiContentsResponse contents;

    public ApiMapper(String json){
        ObjectMapper mapper = new ObjectMapper();
        try{
            contents = mapper.readValue(json, ApiContentsResponse.class);

            for(ApiContentResponse content : contents.getContents()){
                logger.debug("  " + content.toString());
            }
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    public ApiContentsResponse getContents(){
        return contents;
    }
}
