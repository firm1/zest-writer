import com.zestedesavoir.zestwriter.MainApp;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import static org.testfx.api.FxToolkit.*;

public class AddNewContent extends FxRobot {

    // Name of the created new content in the test
    public static final String NEW_CONTENT_TITLE = "Nouveau contenu";
    public static final String NEW_CONTENT_SUBTITLE = "Sous titre du nouveau contenu";


    @BeforeClass
    public static void setupSpec() throws Exception {
        Stage primaryStage = registerPrimaryStage();
        setupStage(stage -> stage.show());
    }

    @Before
    public void setup() throws Exception {
        setupApplication(MainApp.class);
    }

    /*
     * The goal of this integration test is to create new tutorial and check if we can see his name on the left.
     *
     * This test doesn't check if tutorial file (manifest and other file) are created.
     */
    @Test
    public void createNewTutorialTest(){

        createNewContent ();

        clickOn("#type").clickOn("Tutoriel");

        clickOn("Enregistrer");

        clickOn(NEW_CONTENT_TITLE);
    }

    /*
     * The goal of this integration test is to create new article and check if we can see his name on the left.
     *
     * This test doesn't check if article file (manifest and other file) are created.
     */
    @Test
    public void createNewArticleTest() {
        createNewContent();

        clickOn("#type").clickOn("Article");

        clickOn("Enregistrer");

        clickOn(NEW_CONTENT_TITLE);
    }

    /*
     * Create new content from a visible main window.
     *
     * This method is created for sharing code between createNewTutorialTest and createNewTutorialArticle
     */
    private void createNewContent() {
        clickOn("Fichier");
        clickOn("Nouveau");

        clickOn("#title").write(NEW_CONTENT_TITLE);
        clickOn("#subtitle").write(NEW_CONTENT_SUBTITLE);
    }

}
