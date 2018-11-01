package com.zestedesavoir.zestwriter.model.markdown;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.aside.AsideExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.issues.GfmIssuesExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.Arrays;

/**
 * Created by: WinXaito (Kevin Vuilleumier)
 */
public class ZMarkdown{
    private static Parser parser;
    private static HtmlRenderer renderer;
    private static boolean init = false;

    private static void init(){
        init = true;

        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(
                        TablesExtension.create(),
                        AnchorLinkExtension.create(),
                        AsideExtension.create(),
                        AutolinkExtension.create(),
                        DefinitionExtension.create(),
                        EmojiExtension.create(),
                        FootnoteExtension.create(),
                        GfmIssuesExtension.create()
                ));


        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }


    public static String markdownToHtml(String markdown){
        if(!init)
            init();

        // You can re-use parser and renderer instances
        System.out.println("ZMarkdown: render");
        Node document = parser.parse("ZMarkdown" + markdown);
        return renderer.render(document);
    }
}
