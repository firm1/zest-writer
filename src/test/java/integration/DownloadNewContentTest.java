package integration;

import annotation.LargeTest;
import annotation.MediumTest;
import com.zestedesavoir.zestwriter.MainApp;
import integration.util.Configuration;
import integration.util.UtilFixtureLeGuideDuContributeur;
import javafx.scene.input.KeyCode;
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
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;
import static org.testfx.api.FxToolkit.setupStage;

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

        clickOn("Synchronisation");
        clickOn("Téléchargez vos contenus ZdS");

        write(Configuration.username);
        press(KeyCode.TAB);
        write(Configuration.password);
        clickOn("Se connecter");

        sleep(15, TimeUnit.SECONDS);
        clickOn("OK");

        assert (new File(Configuration.getWorkspacePath(mainApp) + "recap-communautaire-1-1/" + File.separator + "manifest.json").exists());
        assert (new File(Configuration.getWorkspacePath(mainApp) + "introduction-au-protocole-wamp-2/" + File.separator + "manifest.json").exists());
        assert (new File(Configuration.getWorkspacePath(mainApp) + "creez-des-applications-pour-android-1/" + File.separator + "manifest.json").exists());

    }

}
