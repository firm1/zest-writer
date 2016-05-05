import static org.junit.Assert.*;

import org.junit.Test;

import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.FlipTable;

public class TestUtils {

    @Test
    public void testCorrectorTextToHtml() {
        String text = "Bonjour, je suis persuade que tu n'aime pas les frites.\nCest normal je ne suis pas belge";
        String expected = "Bonjour, je suis <span class=\"error-french\" title=\"Note : Auxiliaire suivi d’un verbe conjugué\">persuade</span>  que <span class=\"error-french\" title=\"Note : tu + 2 tokens + verbe\">tu n'aime</span>  pas les frites.\n<span class=\"error-french\" title=\"Note : Faute d'orthographe possible (sans suggestions)\">Cest</span>  normal je ne suis pas belge";
        Corrector corrector = new Corrector();
        String res = corrector.checkHtmlContent(text);
        assertEquals(res, expected);
    }

    @Test
    public void testGetCorrectableText() {
        String text = "<p>En java on utilise <code>System.out.println()</code> de cette façon :</p>"
                    +"<table class=\"codehilitetable\"><tbody><tr><td class=\"linenos\"><div class=\"linenodiv\"><pre>1"
                    +"2"
                    +"3</pre></div></td><td class=\"code\"><div class=\"codehilite\"><pre><span></span><span class=\"kd\">public</span> <span class=\"kd\">static</span> <span class=\"kt\">void</span> <span class=\"nf\">main</span> <span class=\"o\">(</span><span class=\"n\">String</span><span class=\"o\">[]</span> <span class=\"n\">args</span><span class=\"o\">)</span> <span class=\"o\">{</span>"
                    +"<span class=\"n\">System</span><span class=\"o\">.</span><span class=\"na\">out</span><span class=\"o\">.</span><span class=\"na\">println</span><span class=\"o\">(</span><span class=\"s\">\"Hello Word\"</span><span class=\"o\">);</span>"
                    +"<span class=\"o\">}</span>"
                    +"</pre></div>"
                    +"</td></tr></tbody></table>";
        String expected = "En java on utilise  de cette façon :";
        String res = Corrector.HtmlToTextWithoutCode(text);
        assertEquals(expected, res.trim());
    }

    @Test
    public void testFlipTable() {
        String[] headers = {"H1", "H2", "H3"};
        String[][] data = {
                {"C11", "C12", "C13"},
                {"C21", "C22", "C23"},
        };
        String expected = "+-----+-----+-----+\n"
                        +"| H1  | H2  | H3  |\n"
                        +"+=====+=====+=====+\n"
                        +"| C11 | C12 | C13 |\n"
                        +"+-----+-----+-----+\n"
                        +"| C21 | C22 | C23 |\n"
                        +"+-----+-----+-----+\n";
        String res = FlipTable.of(headers, data);
        assertEquals(expected, res);
    }
}
