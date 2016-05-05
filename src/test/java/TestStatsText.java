import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.zestedesavoir.zestwriter.utils.readability.Readability;

public class TestStatsText {

    String text = "Bonjour mon nom est toto, et toi ?\n\nBah moi je suis un jeune ami\ntrès important.";
    @Before
    public void setUp() throws Exception {

    }
    @Test
    public void testBasicStatistics() {
        Readability readText = new Readability(this.text);
        assertEquals(readText.getWords().intValue(), 17);
        assertEquals(readText.getSentences().intValue(), 2);
        assertEquals(readText.getSyllables().intValue(), 22);
        assertEquals(readText.getCharacters().intValue(), 59);
    }

}
