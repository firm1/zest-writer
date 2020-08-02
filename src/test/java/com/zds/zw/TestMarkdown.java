package com.zds.zw;

import com.zds.zw.utils.ZMD;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMarkdown {

    ZMD zmd;

    @Before
    public void Setup() {
        zmd = new ZMD();
    }

    @Test
    public void test() {
        String strBefore = "Bonjour `Set<Class<? extends Object>>`";
        String strAfter = "<p>Bonjour <code>Set&#x3C;Class&#x3C;? extends Object>></code></p>";

        assertEquals(zmd.toHtml(strBefore), strAfter);

    }

}
