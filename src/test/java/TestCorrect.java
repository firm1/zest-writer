import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TestCorrect {

    Corrector corrector;

    @Before
    public void setUp() {
        Configuration config = new Configuration (System.getProperty("java.io.tmpdir"));
        corrector = new Corrector();
    }

    @Test
    public void testOrtho() {
        String txt="<p>Ce text et plin 2 fotes lol.</p>";
        int expectedError = 3;

        String s = StringEscapeUtils.unescapeXml(txt);
        String result = corrector.checkHtmlContent(s);
        assertEquals(result.split("error-french").length, expectedError+1);
    }

    @Test
    public void testEscapeCodeRejectIfNotInMarkup() {
        String txt = "<p>Tapez sudo apt-get install vim</p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeXml(txt));
        assertNotSame(result, txt);
    }

    @Test
    public void testEscapeCodeAcceptIfInMarkup() {
        String txt="<p>Tapez <code>sudo apt-get install vim</code></p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeXml(txt));
        assertEquals(result, txt);
    }

    @Test
    public void testEscapeCodeAcceptNotInMarkupButRejectWhenOut() {
        String txt="<p>Tapez <code>sudo apt-get install vim</code> mon sudo est dehors</p>";
        String result = corrector.checkHtmlContent(StringEscapeUtils.unescapeXml(txt));
        assertNotSame(result, txt);
    }

    @Test
    public void testEscapeItalic() {
        String txt="<p>Est-ce que tu voudrais <em>pusher</em> ton code ?</p>";
        String s = StringEscapeUtils.unescapeXml(txt);
        String result = corrector.checkHtmlContent(s);
        assertEquals(result, txt);
    }
}
