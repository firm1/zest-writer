package com.zds.zw.utils;

import com.zds.zw.MainApp;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ZMD {
    Context engine;
    private static final Logger logger = LoggerFactory.getLogger(ZMD.class);
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
        InputStream in = MainApp.class.getResourceAsStream("js/"+script+".js");
        String text = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(System.getProperty("line.separator")));
        return new StringBuilder(text);
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