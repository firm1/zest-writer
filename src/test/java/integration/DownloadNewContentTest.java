package integration;

import annotation.LargeTest;
import com.zestedesavoir.zestwriter.MainApp;
import integration.util.Configuration;
import integration.util.UtilFixtureLeGuideDuContributeur;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.loadui.testfx.GuiTest.waitUntil;
import static org.loadui.testfx.controls.Commons.hasText;
import static org.testfx.api.FxToolkit.*;

public class DownloadNewContentTest extends FxRobot {

    private MainApp mainApp;

    @BeforeClass
    public static void setupSpec() throws Exception {
        Stage primaryStage = registerPrimaryStage();
        setupStage(stage -> stage.show());
    }

    @Before
    public void setup() throws Exception {
        mainApp = (MainApp) setupApplication(MainApp.class);
    }

    @Test
    @LargeTest
    public void downloadNewContent() throws IOException, InterruptedException, URISyntaxException {
        UtilFixtureLeGuideDuContributeur.loadFixtureLeGuideDuContributeur(mainApp);

        sleep(2, TimeUnit.SECONDS);

        clickOn("Synchronisation");
        clickOn("Téléchargez vos contenus ZdS");

        clickOn("#username").write(Configuration.username);
        waitUntil("#username", hasText(Configuration.username));

        clickOn("#password").write(Configuration.password);
        waitUntil("#password", hasText(Configuration.password));

        clickOn("Se connecter");

        sleep(45, TimeUnit.SECONDS);
        clickOn("OK");


        assert (new File(Configuration.getWorkspacePath(mainApp) +  File.separator + "recap-communautaire-1-1/" + File.separator + "manifest.json").exists());
        assert (new File(Configuration.getWorkspacePath(mainApp) +  File.separator + "introduction-au-protocole-wamp-2/" + File.separator + "manifest.json").exists());
        assert (new File(Configuration.getWorkspacePath(mainApp) +  File.separator + "creez-des-applications-pour-android-1/" + File.separator + "manifest.json").exists());

    }

}
