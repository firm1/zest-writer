package com.zestedesavoir.zestwriter.utils;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.languagetool.JLanguageTool;
import org.languagetool.language.French;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Corrector {

    private JLanguageTool langTool;
    private List<String> wordsToIgnore;

    public Corrector() {
        langTool = new JLanguageTool(new French());
        wordsToIgnore = new ArrayList<>();
        langTool.disableRules(Arrays.asList("FRENCH_WHITESPACE", "WHITESPACE_RULE"));
    }


    public static String HtmlToTextWithoutCode(String htmlText) {
        AnnotatedTextBuilder builder = new AnnotatedTextBuilder();
        StringTokenizer tokenizer = new StringTokenizer(htmlText, "<>", true);
        boolean inMarkup = false;
        int CountCode = 0, CountPre = 0;
        while (tokenizer.hasMoreTokens()) {
            String part = tokenizer.nextToken();
            if (inMarkup) {
                switch (part) {
                    case "code":
                        CountCode++;
                        break;
                    case "pre":
                        CountPre++;
                        break;
                    case "/code":
                        CountCode--;
                        break;
                    case "/pre":
                        CountPre--;
                        break;
                }
            }
            if (part.startsWith("<")) {
                builder.addMarkup(part);
                inMarkup = true;
            } else if (part.startsWith(">")) {
                inMarkup = false;
                builder.addMarkup(part);
            } else {
                if (inMarkup) {
                    builder.addMarkup(part);
                } else {
                    if (CountPre == 0 && CountCode == 0) {
                        builder.addText(part);
                    }
                }
            }
        }
        return builder.build().getPlainText();
    }

    private String generate(int k) {
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < k; i++) {
            st.append(' ');
        }
        return st.toString();
    }

    public AnnotatedText makeAnnotatedText(String pseudoXml) {
        AnnotatedTextBuilder builder = new AnnotatedTextBuilder();
        StringTokenizer tokenizer = new StringTokenizer(pseudoXml, "<>", true);
        boolean inMarkup = false;
        int CountCode = 0, CountPre = 0, CountEm = 0, CountSup = 0;
        while (tokenizer.hasMoreTokens()) {
            String part = tokenizer.nextToken();
            if (inMarkup) {
                switch (part) {
                    case "code":
                        CountCode++;
                        break;
                    case "pre":
                        CountPre++;
                        break;
                    case "em":
                        CountEm++;
                        break;
                    case "sup":
                        CountSup++;
                        break;
                    case "/code":
                        CountCode--;
                        break;
                    case "/pre":
                        CountPre--;
                        break;
                    case "/em":
                        CountEm--;
                        break;
                    case "/sup":
                        CountSup--;
                        break;
                }
            }
            if (part.startsWith("<")) {
                builder.addMarkup(part);
                inMarkup = true;
            } else if (part.startsWith(">")) {
                inMarkup = false;
                builder.addMarkup(part);
            } else {
                if (inMarkup) {
                    builder.addMarkup(part);
                } else {
                    if (CountPre == 0 && CountSup == 0) { // if we aren't in inline code or not in footnote
                        builder.addText(part);
                        if (CountCode > 0 || CountEm > 0) { // ignore code or italic
                            wordsToIgnore.add(part);
                        }
                    } else {
                        builder.addText(generate(part.length()));
                    }
                }
            }
        }
        return builder.build();
    }

    public String checkHtmlContent(String htmlContent) {
        AnnotatedText markup = makeAnnotatedText(htmlContent);
        StringBuilder bf = new StringBuilder(htmlContent);

        langTool.getAllActiveRules().stream().filter(rule -> rule instanceof SpellingCheckRule).forEach(rule -> ((SpellingCheckRule) rule).acceptPhrases(wordsToIgnore));

        List<RuleMatch> matches = new ArrayList<>();
        try {
            matches = langTool.check(markup);
        }
        catch (Exception e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
        int offset = 0;
        for (RuleMatch match : matches) {
            String desc = match.getMessage();
            desc = new HtmlToPlainText().getPlainText(Jsoup.parse(desc));

            if (match.getSuggestedReplacements().size() > 0) {
                desc += Configuration.bundle.getString("ui.alert.correction.tooltip.suggestion")
                        + match.getSuggestedReplacements();
            }
            String before = "<span class=\"error-french\" title=\"" + desc + "\">";
            bf.insert(match.getFromPos() + offset, before);
            offset += before.length();

            String after = "</span> ";
            bf.insert(match.getToPos() + offset, after);
            offset += after.length();

        }
        return bf.toString();
    }

    public String checkHtmlContentToText(String htmlContent, String source) {
        AnnotatedText markup = makeAnnotatedText(htmlContent);
        StringBuilder bf = new StringBuilder();
        langTool.getAllActiveRules().stream().filter(rule -> rule instanceof SpellingCheckRule).forEach(rule -> ((SpellingCheckRule) rule).addIgnoreTokens(wordsToIgnore));
        List<RuleMatch> matches = new ArrayList<>();
        try {
            matches = langTool.check(markup);
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }

        for (RuleMatch match : matches) {
            String txt = htmlContent.substring(match.getFromPos(), match.getToPos());
            bf.append("\n\n");
            bf.append("> ");
            bf.append(markup.getPlainText().split("[\n|\r]")[match.getLine()].replace(txt, "**" + txt + "**"));
            bf.append("\n");
            bf.append(Configuration.bundle.getString("ui.alert.correction.source")).append(source);
            bf.append("\n\n");
            bf.append(match.getRule().getDescription());
            bf.append("\n\n");
            for (String s : match.getSuggestedReplacements()) {
                bf.append("- ").append(s).append("\n");
            }
        }
        return bf.toString();
    }

    public int countMistakes(MdTextController mdTextController, String markdown) {
        String htmlText = StringEscapeUtils.unescapeHtml(MenuController.markdownToHtml(mdTextController, markdown));
        AnnotatedText markup = makeAnnotatedText(htmlText);

        langTool.getAllActiveRules().stream()
                .filter(rule -> rule instanceof SpellingCheckRule).forEach(rule -> ((SpellingCheckRule) rule).acceptPhrases(wordsToIgnore));
        try {
            List<RuleMatch> matches = matches = langTool.check(markup);
            return matches.size();
        }
        catch (Exception e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
        return 0;
    }
}
