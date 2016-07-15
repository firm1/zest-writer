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

            InputStream is = MainApp.class.getResourceAsStream(HTML_TEMPLATE_LOCATION);

            String template = "";
            try {
                template= IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                logger.error("Error when reading the template stream.", e);
            }

            Matcher pathMatcher = Pattern.compile("%%(.*)%%").matcher(template);

            StringBuffer sbCheatSheet = new StringBuffer();
            while (pathMatcher.find()) {
                String path = MainApp.class.getResource("assets" + pathMatcher.group(1)).toExternalForm();
                pathMatcher.appendReplacement(sbCheatSheet, path);
            }
            pathMatcher.appendTail(sbCheatSheet);
            htmlTemplate = new String(sbCheatSheet);
        }
        return htmlTemplate;
    }

    public String addHeaderAndFooter(String content) {
        return getHTMLTemplate().replaceFirst(CONTENT_KEYWORD, Matcher.quoteReplacement(content));
    }
}
