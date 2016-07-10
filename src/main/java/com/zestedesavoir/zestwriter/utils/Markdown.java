package com.zestedesavoir.zestwriter.utils;

import com.zestedesavoir.zestwriter.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private static Logger logger;
    private final String CONTENT_KEYWORD = "<!--content-->";
    private String htmlTemplate;

    public Markdown() {
        logger = LoggerFactory.getLogger(Markdown.class);
    }

    private String getHTMLTemplate() {
        if(htmlTemplate == null) {
            final String HTML_TEMPLATE_LOCATION = "assets/static/html/template.html";

            InputStream cheatSheetStream = MainApp.class.getResourceAsStream(HTML_TEMPLATE_LOCATION);

            String csStr = "";
            try {
                csStr = IOUtils.toString(cheatSheetStream, "UTF-8");
            } catch (IOException e) {
                logger.error("Error when reading the cheatSheet stream.", e);
            }

            Matcher m = Pattern.compile("%%(.*)%%").matcher(csStr);

            StringBuffer sbCheatSheet = new StringBuffer();
            while (m.find()) {
                String path = MainApp.class.getResource("assets" + m.group(1)).toExternalForm();
                m.appendReplacement(sbCheatSheet, path);
            }
            m.appendTail(sbCheatSheet);
            htmlTemplate = new String(sbCheatSheet);
        }
        return htmlTemplate;
    }

    public String addHeaderAndFooter(String content) {
        return getHTMLTemplate().replaceFirst(CONTENT_KEYWORD, content);
    }
}
