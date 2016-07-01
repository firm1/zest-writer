package unit;

import com.zestedesavoir.zestwriter.utils.Corrector;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import static com.aliasi.test.unit.Asserts.assertNotEquals;
import static org.junit.Assert.assertEquals;

public class TestCorrect {

    Corrector corrector;

    @Before
    public void setUp() {
        corrector = new Corrector();
    }

    @Test
    public void testOrtho() {
        String txt="<p>Ce text et plin 2 fotes lol.</p>";
        String s = StringEscapeUtils.unescapeHtml(txt);
        String result = corrector.checkHtmlContent(s);
        assertEquals(result, "<p>Ce <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">text</span>  et <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">plin</span>  2 <span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">fotes</span>  lol.</p>");
    }

    @Test
    public void testEscapeCodeRejectIfNotInMarkup() {
        String txt = "<p>Tapez sudo apt-get install vim</p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeHtml(txt));
        assertNotEquals(result, txt);
    }

    @Test
    public void testEscapeCodeAcceptIfInMarkup() {
        String txt="<p>Tapez <code>sudo apt-get install vim</code></p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeHtml(txt));
        assertEquals(result, txt);
    }

    @Test
    public void testEscapeCodeAcceptNotInMarkupButRejectWhenOut() {
        String txt="<p>Tapez <code>sudo apt-get install vim</code> mon sudo est dehors</p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeHtml(txt));
        assertNotEquals(result, txt);
    }

    @Test
    public void testEscapeItalic() {
        String txt="<p>Est-ce que tu voudrais <em>pusher</em> ton code ?</p>";
        String s = StringEscapeUtils.unescapeHtml(txt);
        String result = corrector.checkHtmlContent(s);
        assertEquals(result, txt);
    }
}
