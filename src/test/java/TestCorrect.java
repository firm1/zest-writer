import com.zestedesavoir.zestwriter.utils.Corrector;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.French;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.rules.RuleMatch;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCorrect {
    String txt="<p>Ce text et plin 2 fotes lol.</p>";

    @Test
    public void test() {
        String s = StringEscapeUtils.unescapeHtml(txt);
        Corrector corrector = new Corrector();
        String result = corrector.checkHtmlContent(s);
        assertEquals(result, "<p>Ce <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">text</span>  et <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">plin</span>  2 <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">fotes</span>  lol.</p>");
    }
}
