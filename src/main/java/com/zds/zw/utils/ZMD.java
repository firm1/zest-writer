package com.zds.zw.utils;

import com.zds.zw.MainApp;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

public class ZMD {
    Context engine;
    private static Logger logger = LoggerFactory.getLogger(ZMD.class);
    public ZMD() {
        engine = Context
                .newBuilder("js")
                .allowExperimentalOptions(true)
                .option("js.global-property", String.valueOf(true))
                .allowAllAccess(true)
                .build();

        try {
            StringBuilder jsCode = new StringBuilder();
            jsCode.append(readJs("zmarkdown"));
            jsCode.append(readJs("zmarkdown-zhtml"));
            jsCode.append(readJs("convert"));

            engine.eval("js", jsCode.toString());

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static StringBuilder readJs(String script) throws URISyntaxException, IOException {
        FileReader fileReader = new FileReader(MainApp.class.getResource("js/"+script+".js").toURI().getPath());
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        // delete the last new line separator
        reader.close();
        return stringBuilder;
    }

    public String subHeader(String markdown) {
        return markdown.replaceAll("(?m)^([#]+) (.*)$", "##$1 $2");
    }

    public String toHtml(String md) {
        md = subHeader(md);
        Value bindings = engine.getBindings("js");
        bindings.putMember("md", md);
        engine.eval("js", "var html_response = toHtml(md)");
        return engine.getBindings("js").getMember("html_response").asString();
    }
}