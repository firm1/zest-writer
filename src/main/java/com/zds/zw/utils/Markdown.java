package com.zds.zw.utils;

import com.zds.zw.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private static String CONTENT_KEYWORD_BEFORE;
    private static String CONTENT_KEYWORD_AFTER;
    private static String CONTENT_STRICT_BEFORE;
    private static String CONTENT_STRICT_AFTER;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Markdown() {
        try(InputStream is = MainApp.class.getResourceAsStream("assets/static/html/template-begin.html"))  {
            String template= IOUtils.toString(is, "UTF-8");
            Matcher pathMatcher = Pattern.compile("%%(.*)%%").matcher(template);
            StringBuffer sb = new StringBuffer();
            while (pathMatcher.find()) {
                String path = MainApp.class.getResource("assets" + pathMatcher.group(1)).toExternalForm();
                pathMatcher.appendReplacement(sb, path);
            }
            pathMatcher.appendTail(sb);
            CONTENT_KEYWORD_BEFORE = new String(sb);
        } catch (IOException e) {
            log.error("Error when reading the template stream.", e);
        }

        try(InputStream is = MainApp.class.getResourceAsStream("assets/static/html/template-end.html"))  {
            String template= IOUtils.toString(is, "UTF-8");
            Matcher pathMatcher = Pattern.compile("%%(.*)%%").matcher(template);
            StringBuffer sb = new StringBuffer();
            while (pathMatcher.find()) {
                String path = MainApp.class.getResource("assets" + pathMatcher.group(1)).toExternalForm();
                pathMatcher.appendReplacement(sb, path);
            }
            pathMatcher.appendTail(sb);
            CONTENT_KEYWORD_AFTER = new String(sb);
            CONTENT_STRICT_AFTER = new String(sb);
        } catch (IOException e) {
            log.error("Error when reading the template stream.", e);
        }
    }

    public String addHeaderAndFooter(String content) {
        return CONTENT_KEYWORD_BEFORE+content+CONTENT_KEYWORD_AFTER;
    }

    public String addHeaderAndFooterStrict(String content, String title) {

        try(InputStream is = MainApp.class.getResourceAsStream("assets/static/html/template-strict-begin.html"))  {
            String template= IOUtils.toString(is, "UTF-8");
            Matcher pathMatcher = Pattern.compile("%%(.*)%%").matcher(template);
            StringBuffer sb = new StringBuffer();
            while (pathMatcher.find()) {
                String path = MainApp.class.getResource("assets" + pathMatcher.group(1)).toExternalForm();
                pathMatcher.appendReplacement(sb, path);
            }
            pathMatcher.appendTail(sb);
            CONTENT_STRICT_BEFORE = new String(sb).replace("##title##", title.toUpperCase());
        } catch (IOException e) {
            log.error("Error when reading the template stream.", e);
        }

        return CONTENT_STRICT_BEFORE+content+CONTENT_STRICT_AFTER;
    }
}
