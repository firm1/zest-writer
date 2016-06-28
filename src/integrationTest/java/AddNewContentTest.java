import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import java.io.File;

import static org.testfx.api.FxToolkit.*;

public class AddNewContentTest extends FxRobot {

    // Name of the created new content in the test
    public static final String NEW_CONTENT_TITLE = "Nouveau contenu";
    public static final String NEW_CONTENT_SUBTITLE = "Sous titre du nouveau contenu";

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

    /*
     * The goal of this integration test is to create new article and check if we can see his name on the left.
     *
     * This test doesn't check if article file (manifest and other file) are created.
     */
    @Test
    public void createNewArticleTest() {
        assertContentHaveBeenCreated("Article");
    }

    /*
     * Create new content from a visible main window.
     *
     * This method is created for sharing code between createNewTutorialTest and createNewTutorialArticle
     */
    @Test
    public void createNewTutorialTest() {
        assertContentHaveBeenCreated("Tutoriel");
    }

    private void assertContentHaveBeenCreated (String typeContent) {

        clickOn("Fichier");
        clickOn("Nouveau");

        clickOn("#title").write(NEW_CONTENT_TITLE);
        clickOn("#subtitle").write(NEW_CONTENT_SUBTITLE);
        clickOn("#type").clickOn(typeContent);

        clickOn("Enregistrer");

        clickOn(NEW_CONTENT_TITLE);

        // Assert that main information have been added to the list
        ObservableList<Content> content = mainApp.getContents();

        assert (content.size() == 1);
        assert (content.get(0).getDescription().equals(NEW_CONTENT_SUBTITLE));
        assert (content.get(0).getTitle().equals(NEW_CONTENT_TITLE));
        assert (content.get(0).getLicence().equals("Tous droits réservés"));

        // Assert file have been created
        assert (new File(content.get(0).getBasePath() + File.separator + "manifest.json").exists());
        assert (new File(content.get(0).getBasePath() + File.separator + "introduction.md").exists());
        assert (new File(content.get(0).getBasePath() + File.separator + "conclusion.md").exists());
    }

}
