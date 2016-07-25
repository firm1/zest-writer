import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import com.zestedesavoir.zestwriter.utils.StorageSaver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeroturnaround.zip.ZipUtil;

import com.zestedesavoir.zestwriter.model.MetaContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;

public class TestApiZds {

    private final static File TEST_DIR = new File(System.getProperty("java.io.tmpdir"));
    private String login = System.getProperty("zw.username");
    private String password = System.getProperty("zw.password");
    private Configuration config;

    @Before
    public void setUp() {
        StorageSaver.deleteFile(new File(TEST_DIR, ".zestwriter"));
        StorageSaver.deleteFile(new File(TEST_DIR.getAbsolutePath(), "workspace"));
        config = new Configuration(TEST_DIR.getAbsolutePath());
        config.setWorkspacePath(TEST_DIR.getAbsolutePath()+File.separator+"workspace");
        config.loadWorkspace();
    }

    @Test
    /**
     * This test allow to check relation between Zest Writer and Website
     * For run this test you need pass Jvm Args like -Dzw.username=USER -Dzw.password=PASSWORD
     * if no argument is supplied, the test will succeed anyway,
     * but the connection to the web site will not be tested
     */
    public void testLogin() {
        assertEquals(config.getWorkspacePath(), TEST_DIR.getAbsolutePath()+File.separator+"workspace");
        if(login != null && !login.equals("") && password != null && !password.equals("")) {
            ZdsHttp api = new ZdsHttp(config);
            try {
                assertTrue("Tentative d'authentification au site", api.login(login, password));
                assertTrue("Vérification de l'authentification réussi", api.isAuthenticated());
                api.initInfoOnlineContent("tutorial");
                api.initInfoOnlineContent("article");
                api.initGalleryId("1312", "tutoriel-test");
                assertEquals(api.getGalleryId(), "3243");
                api.downloaDraft("1312", "tutorial");
                api.downloaDraft("1313", "article");
                File offlineDir = new File(config.getWorkspaceFactory().getOfflineSaver().getBaseDirectory());
                File onlineDir = new File(config.getWorkspaceFactory().getOnlineSaver().getBaseDirectory());
                assertTrue(onlineDir.isDirectory());
                assertTrue(onlineDir.list().length > 0);
                assertTrue(offlineDir.isDirectory());
                assertTrue(offlineDir.list().length == 0);

                for(File on:onlineDir.listFiles()) api.unzipOnlineContent(on.getAbsolutePath());
                assertTrue(offlineDir.list().length > 0);

                // import
                File zipfile = new File(offlineDir, "tutoriel-test.zip");
                ZipUtil.pack(new File(offlineDir, "tutoriel-test"), zipfile);
                assertTrue(api.importContent(zipfile.getAbsolutePath(), "1312", "tutoriel-test", "Message d'import"));

                api.logout();
                assertFalse(api.isAuthenticated());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    @After
    public void tearDown() {
        StorageSaver.deleteFile(new File(TEST_DIR, ".zestwriter"));
        StorageSaver.deleteFile(new File(TEST_DIR.getAbsolutePath(), "workspace"));
    }

}
