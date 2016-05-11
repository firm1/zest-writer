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

    @Test
    public void checkIndex() {
        String GreenEggsAndHam =    "I do not like them in a box. " +
                "I do not like them with a fox. " +
                "I do not like them in a house. " +
                "I do not like them with a mouse. " +
                "I do not like them here or there. " +
                "I do not like them anywhere. " +
                "I do not like green eggs and ham. " +
                "I do not like them, Sam-I-am.";


        Readability r = new Readability(GreenEggsAndHam);
        assertEquals(r.getSMOG(), new Double(5.149));
        assertEquals(r.getSMOGIndex(), new Double(4.936));
        assertEquals(r.getFleschReadingEase(), new Double(111.64));
        assertEquals(r.getFleschKincaidGradeLevel(), new Double(-0.387));
        assertEquals(r.getARI(), new Double(-3.881));
        assertEquals(r.getGunningFog(), 3.2, 1);
        assertEquals(r.getColemanLiau(), new Double(-1.7));

        String logorrhea = "The word logorrhoea is often used pejoratively " +
            "to describe prose that is highly abstract and " +
            "contains little concrete language. Since abstract " +
            "writing is hard to visualize, it often seems as though " +
            "it makes no sense and all the words are excessive. " +
            "Writers in academic fields that concern themselves mostly " +
            "with the abstract, such as philosophy and especially " +
            "postmodernism, often fail to include extensive concrete " +
            "examples of their ideas, and so a superficial examination " +
            "of their work might lead one to believe that it is all nonsense.";


        r = new Readability(logorrhea);

        assertEquals(r.getSMOG(), new Double(15.021));
        assertEquals(r.getSMOGIndex(), new Double(14.402));
        assertEquals(r.getFleschReadingEase(), new Double(36.083));
        assertEquals(r.getFleschKincaidGradeLevel(), new Double(15.348));
        assertEquals(r.getARI(), new Double(17.33));
        assertEquals(r.getGunningFog(), 17.2, 1);
        assertEquals(r.getColemanLiau(), new Double(13.746));
    }

}
