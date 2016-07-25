import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import com.zestedesavoir.zestwriter.utils.StorageSaver;
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
        config.saveConfFile();

        config = new Configuration(TEST_DIR.getAbsolutePath());
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
