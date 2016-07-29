import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import com.zestedesavoir.zestwriter.utils.StorageSaver;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zestedesavoir.zestwriter.model.MetaContent;
import com.zestedesavoir.zestwriter.utils.Configuration;

public class TestConfig {
    Configuration config;

    public final static File TEST_DIR = new File(System.getProperty("java.io.tmpdir"));

    @Before
    public void setUp() {
        StorageSaver.deleteFile(new File(TEST_DIR, ".zestwriter"));
        config = new Configuration(TEST_DIR.getAbsolutePath());
    }

    @Test
    public void testConfiguration() {
        ZdsHttp api = new ZdsHttp(config);
        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, ".zestwriter"+File.separator+"conf.properties").exists(), true);
        assertEquals(config.getAdvancedServerHost(), "zestedesavoir.com");
        assertEquals(config.getAdvancedServerPort(), "80");
        assertEquals(config.getAdvancedServerProtocol(), "https");
        assertEquals(config.getAuthentificationPassword(), "");
        assertEquals(config.getAuthentificationUsername(), "");
        assertEquals(config.getDisplayTheme(), "light.css");
        assertEquals(config.getDisplayLang(), "fr_FR");
        assertEquals(config.getDisplayWindowHeight() > 0, true);
        assertEquals(config.getDisplayWindowWidth() > 0, true);
        assertEquals(config.getDisplayWindowPositionX() == 0, true);
        assertEquals(config.getDisplayWindowPositionY() == 0, true);
        assertTrue(config.getEditorSmart());
        assertFalse(config.isDisplayWindowMaximize());
        assertTrue(config.isDisplayWindowPersonnalDimension());
        assertTrue(config.isDisplayWindowPersonnalPosition());
        assertEquals(config.getEditorToolbarView(), "yes");
        assertEquals(config.getEditorFontsize(), 14);
        assertEquals(config.getEditorFont(), "Fira Mono");

        config.setAdvancedServerHost("localhost");
        assertEquals(config.getAdvancedServerHost(), "localhost");
        config.setAdvancedServerPort("8080");
        assertEquals(config.getAdvancedServerPort(), "8080");
        config.setAdvancedServerProtocol("http");
        assertEquals(config.getAdvancedServerProtocol(), "http");
        config.setDisplayWindowHeight("600");
        assertEquals(config.getDisplayWindowHeight(), 600, 0);
        config.setDisplayWindowWidth("800");
        assertEquals(config.getDisplayWindowWidth(), 800, 0);
        config.setDisplayWindowPositionX("10");
        assertEquals(config.getDisplayWindowPositionX(), 10, 0);
        config.setDisplayWindowPositionY("20");
        assertEquals(config.getDisplayWindowPositionY(), 20, 0);
        config.setAuthentificationUsername("admin");
        config.setAuthentificationPassword("admin");
        config.setDisplayLang("en");
        config.setDisplayTheme("dark.css");
        config.setEditorSmart("false");
        config.setDisplayWindowMaximize("true");
        config.setDisplayWindowPersonnalPosition("false");
        config.setDisplayWindowStandardDimension("false");
        config.setEditorToolbarView("no");
        config.setEditorFont("Arial");
        config.setEditorFontSize("13");
        config.saveConfFile();

        assertEquals(config.getAdvancedServerHost(), "localhost");
        assertEquals(config.getAdvancedServerPort(), "8080");
        assertEquals(config.getAdvancedServerProtocol(), "http");
        assertEquals(config.getAuthentificationUsername(), "admin");
        assertEquals(config.getAuthentificationPassword(), "admin");
        assertEquals(config.getDisplayTheme(), "dark.css");
        assertEquals(config.getDisplayLang(), "en");
        assertEquals(config.getDisplayWindowHeight(), 600, 0);
        assertEquals(config.getDisplayWindowWidth(), 800, 0);
        assertEquals(config.getDisplayWindowPositionX(), 10, 0);
        assertEquals(config.getDisplayWindowPositionY(), 20, 0);
        assertFalse(config.getEditorSmart());
        assertTrue(config.isDisplayWindowMaximize());
        assertFalse(config.isDisplayWindowPersonnalDimension());
        assertFalse(config.isDisplayWindowPersonnalPosition());
        assertEquals(config.getEditorToolbarView(), "no");
        assertEquals(config.getEditorFontsize(), 13);
        assertEquals(config.getEditorFont(), "Arial");

        config.resetAllOptions();
        assertEquals(config.getAdvancedServerHost(), "zestedesavoir.com");
        assertEquals(config.getAdvancedServerPort(), "80");
        assertEquals(config.getAdvancedServerProtocol(), "https");
        assertEquals(config.getAuthentificationPassword(), "");
        assertEquals(config.getAuthentificationUsername(), "");
        assertEquals(config.getDisplayTheme(), "light.css");
        assertEquals(config.getDisplayLang(), "fr_FR");
        assertEquals(config.getDisplayWindowHeight() > 0, true);
        assertEquals(config.getDisplayWindowWidth() > 0, true);
        assertEquals(config.getDisplayWindowPositionX() == 0, true);
        assertEquals(config.getDisplayWindowPositionY() == 0, true);
        assertTrue(config.getEditorSmart());
        assertFalse(config.isDisplayWindowMaximize());
        assertTrue(config.isDisplayWindowPersonnalDimension());
        assertTrue(config.isDisplayWindowPersonnalPosition());
        assertEquals(config.getEditorToolbarView(), "yes");
        assertEquals(config.getEditorFontsize(), 14);
        assertEquals(config.getEditorFont(), "Fira Mono");
    }

    @Test
    public void testActions() {
        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, ".zestwriter"+File.separator+"action.properties").exists(), true);
        assertEquals(config.getActions().size(), 0);
        config.addActionProject(new File(TEST_DIR, "tutorial-one").getAbsolutePath());
        assertEquals(config.getActions().size(), 1);
        config.addActionProject(new File(TEST_DIR, "tutorial-two").getAbsolutePath());
        assertEquals(config.getActions().size(), 2);
        config.delActionProject(new File(TEST_DIR, "tutorial-non-exist").getAbsolutePath());
        assertEquals(config.getActions().size(), 2);
        config.delActionProject(new File(TEST_DIR, "tutorial-one").getAbsolutePath());
        assertEquals(config.getActions().size(), 1);
        config.delActionProject(new File(TEST_DIR, "tutorial-two").getAbsolutePath());
        assertEquals(config.getActions().size(), 0);
    }

    @After
    public void tearDown() {
        StorageSaver.deleteFile(new File(TEST_DIR, ".zestwriter"));
    }

    @Test
    public void testRelease() {
        try {
            String last = Configuration.getLastRelease();
            assertEquals(last == null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
