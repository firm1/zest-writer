import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.zestedesavoir.zestwriter.utils.Configuration;

public class TestConfig {

    public final static File TEST_DIR = new File(System.getProperty("java.io.tmpdir"));
    @Test
    public void testConfiguration() {

        Configuration config = new Configuration(TEST_DIR.getAbsolutePath());

        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, ".zestwriter"+File.separator+"conf.properties").exists(), true);
        assertEquals(config.getAdvancedServerHost(), "zestedesavoir.com");
        assertEquals(config.getAdvancedServerPort(), "80");
        assertEquals(config.getAdvancedServerProtocol(), "https");
        assertEquals(config.getAuthentificationPassword(), "");
        assertEquals(config.getAuthentificationUsername(), "");
        assertEquals(config.getDisplayTheme(), "Standard");
        assertEquals(config.getDisplayWindowHeight() > 0, true);
        assertEquals(config.getDisplayWindowWidth() > 0, true);
        assertEquals(config.getDisplayWindowPositionX() == 0, true);
        assertEquals(config.getDisplayWindowPositionY() == 0, true);
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
