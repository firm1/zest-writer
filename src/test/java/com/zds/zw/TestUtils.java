package com.zds.zw;

import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.FlipTable;
import com.zds.zw.utils.Markdown;
import com.zds.zw.utils.ZdsHttp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {
    Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration (System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void testHTMLHeaderAndFooter() {
        String content = "<p>Hello</p>";

        Markdown mdUtil = new Markdown(new ZdsHttp(config));
        String fullContent = mdUtil.addHeaderAndFooter(content);
        assertTrue (fullContent.contains ("<p>Hello</p>"));
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

    @Test(expected = IllegalArgumentException.class)
    public void testFlipTableWithNoData() {
        String[] headers = {"H1", "H2", "H3"};
        String[] emptyHeaders = {};
        String[][] data = {};
        String expected = "+----+----+----+\n"
                        +"| H1 | H2 | H3 |\n"
                        +"+====+====+====+\n"
                        +"| (empty)      |\n"
                        +"+==============+\n";
        String res = FlipTable.of(headers, data);
        assertEquals(expected, res);
        FlipTable.of(emptyHeaders, data);
    }

    @Test(expected = NullPointerException.class)
    public void testFlipTableNullValues() {
        String[] headers = {"H1", "H2", "H3"};
        String[][] data = {};
        FlipTable.of(headers, null);
        FlipTable.of(null, data);
    }
}
