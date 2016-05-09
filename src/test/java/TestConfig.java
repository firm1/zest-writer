import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.zestedesavoir.zestwriter.utils.Configuration;

public class TestConfig {

    public final static File TEST_DIR = new File(System.getProperty("java.io.tmpdir"));
    @Test
    public void testConfiguration() {

        Configuration config = new Configuration(TEST_DIR.getAbsolutePath());

        assertEquals(new File(TEST_DIR, ".zestwriter").exists(), true);
        assertEquals(new File(TEST_DIR, ".zestwriter"+File.separator+"conf.properties").exists(), true);
    }

}
