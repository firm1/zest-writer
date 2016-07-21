package integration;

import annotation.MediumTest;
import com.zestedesavoir.zestwriter.MainApp;
import integration.util.UtilFixtureLeGuideDuContributeur;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;
import static org.testfx.api.FxToolkit.setupStage;

public class AddNewContainerTest extends FxRobot {

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
    @MediumTest
    public void createNewContainerTest() throws IOException, InterruptedException, URISyntaxException {
        UtilFixtureLeGuideDuContributeur.loadFixtureLeGuideDuContributeur(mainApp);

        sleep(2, TimeUnit.SECONDS);
        clickOn("Contribuer au contenu", MouseButton.SECONDARY);
        clickOn("Ajouter un conteneur");
        clickOn("OK");

        doubleClickOn("Contribuer au contenu");
        doubleClickOn("Conteneur");
    }


}
