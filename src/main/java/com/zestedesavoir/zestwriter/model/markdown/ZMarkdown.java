package com.zestedesavoir.zestwriter.model.markdown;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Created by: WinXaito (Kevin Vuilleumier)
 */
public class ZMarkdown{
    private static MutableDataSet options = new MutableDataSet();
    private static Parser parser = Parser.builder(options).build();
    private static HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    public static String markdownToHtml(String markdown){
        // You can re-use parser and renderer instances
        System.out.println("ZMarkdown: render");
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
