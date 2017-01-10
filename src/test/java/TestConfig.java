import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.StorageSaver;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestConfig {
    public final static File TEST_DIR = new File(System.getProperty("java.io.tmpdir"));
    Configuration config;

    @Before
    public void setUp() {
        StorageSaver.deleteFile(new File(TEST_DIR, ".zestwriter"));
        config = new Configuration(TEST_DIR.getAbsolutePath());
        config.setWorkspacePath(TEST_DIR.getAbsolutePath());
    }

    @Test
    public void testConfiguration() {
        ZdsHttp api = new ZdsHttp(config);
        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, ".zestwriter"+File.separator+"conf.properties").exists(), true);
        assertEquals(config.getAdvancedServerHost(), "zestedesavoir.com");
        assertEquals(config.getAdvancedServerPort(), "443");
        assertEquals(config.getAdvancedServerProtocol(), "https");
        assertEquals(config.getAuthentificationPassword(), "");
        assertEquals(config.getAuthentificationUsername(), "");
        assertEquals(config.getDisplayTheme(), "light.css");
        assertEquals(config.getDisplayLang(), "fr_FR");
        assertTrue(config.getDisplayWindowHeight() > 0);
        assertTrue(config.getDisplayWindowWidth() > 0);
        assertTrue(config.getDisplayWindowPositionX() == 0);
        assertTrue(config.getDisplayWindowPositionY() == 0);
        assertTrue(config.isEditorSmart());
        assertFalse(config.isDisplayWindowMaximize());
        assertTrue(config.isDisplayWindowPersonnalDimension());
        assertTrue(config.isDisplayWindowPersonnalPosition());
        assertTrue(config.isEditorToolbarView());
        assertTrue(config.isEditorLinenoView());
        assertTrue(config.isEditorRenderView());
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
        config.setEditorToolbarView(false);
        config.setEditorLinenoView(false);
        config.setEditorRenderView(false);
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
        assertFalse(config.isEditorSmart());
        assertTrue(config.isDisplayWindowMaximize());
        assertFalse(config.isDisplayWindowPersonnalDimension());
        assertFalse(config.isDisplayWindowPersonnalPosition());
        assertFalse(config.isEditorToolbarView());
        assertFalse(config.isEditorLinenoView());
        assertFalse(config.isEditorRenderView());
        assertEquals(config.getEditorFontsize(), 13);
        assertEquals(config.getEditorFont(), "Arial");

        config.resetAllOptions();
        assertEquals(config.getAdvancedServerHost(), "zestedesavoir.com");
        assertEquals(config.getAdvancedServerPort(), "443");
        assertEquals(config.getAdvancedServerProtocol(), "https");
        assertEquals(config.getAuthentificationPassword(), "");
        assertEquals(config.getAuthentificationUsername(), "");
        assertEquals(config.getDisplayTheme(), "light.css");
        assertEquals(config.getDisplayLang(), "fr_FR");
        assertTrue(config.getDisplayWindowHeight() > 0);
        assertTrue(config.getDisplayWindowWidth() > 0);
        assertTrue(config.getDisplayWindowPositionX() == 0);
        assertTrue(config.getDisplayWindowPositionY() == 0);
        assertTrue(config.isEditorSmart());
        assertFalse(config.isDisplayWindowMaximize());
        assertTrue(config.isDisplayWindowPersonnalDimension());
        assertTrue(config.isDisplayWindowPersonnalPosition());
        assertTrue(config.isEditorToolbarView());
        assertTrue(config.isEditorLinenoView());
        assertTrue(config.isEditorRenderView());
        assertEquals(config.getEditorFontsize(), 14);
        assertEquals(config.getEditorFont(), "Fira Mono");
    }

    @Test
    public void testActions() {
        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, "action.properties").exists(), true);
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
