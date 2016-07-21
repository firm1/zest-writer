package integration;

import annotation.MediumTest;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import integration.util.ConfigurationUI;
import integration.util.UtilFixtureLeGuideDuContributeur;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxToolkit.*;

public class EditorTest  extends FxRobot {

    private MainApp mainApp;

    // First key contains id of the editor button. Value contains the expected resul.
    private Map<String, String> actionButtonAndResult;

    @BeforeClass
    public static void setupSpec() throws Exception {
        Stage primaryStage = registerPrimaryStage();
        setupStage(stage -> stage.show());
    }

    @Before
    public void setup() throws Exception {
        mainApp = (MainApp) setupApplication(MainApp.class);

        addActions();
    }

    @Test
    @MediumTest
    public void testButtonsEditorTest() throws IOException, InterruptedException, URISyntaxException {
        UtilFixtureLeGuideDuContributeur.loadFixtureLeGuideDuContributeur(mainApp);

        sleep(4, TimeUnit.SECONDS);

        doubleClickOn("Introduction");

        sleep(4, TimeUnit.SECONDS);

        clickOn(ConfigurationUI.ID_EDITOR);

        // For bold, italic, strikethrough, keyboard, superscript, subscript, center, right, bullet, numbered, header and quote button.
        for (Map.Entry<String,String> e : actionButtonAndResult.entrySet()){

            resetEditor ();

            try {
                clickOn(e.getKey());
            }
            catch (Exception exception) {
                clickOn(ConfigurationUI.CLASS_OVERFLOW_BUTTON_TITLE_BAR);
                clickOn(e.getKey());
            }

            clickOn(ConfigurationUI.ID_EDITOR_SAVEBUTTON);

            assert(mainApp.getContents().get(0).getIntroduction().getMarkdown().toString().contains(e.getValue()));
        }

        // For link button
        linkTest();
    }

    private void addActions() {
        actionButtonAndResult = new LinkedHashMap<>();
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_BOLD, "**"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"**");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_ITALIC, "*"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"*");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_STRIKETHROUGH, "~~"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"~~");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_KEYBOARD, "||"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"||");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_SUPERSCRIPT, "^"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"^");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_SUBSCRIPT, "~"+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"~");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_CENTER, "-> "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+" <-");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_RIGHT, "-> "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+" ->");
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_BULLET, "- "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_NUMBERED, "1. "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_HEADER, "# "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
        actionButtonAndResult.put(ConfigurationUI.ID_EDITOR_QUOTE, "> "+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
    }

    private void resetEditor () {
        push(CONTROL, A);
        push(DELETE);
        write(ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
        push(CONTROL, A);
    }

    private void linkTest () {
        resetEditor();
        clickOn(ConfigurationUI.ID_EDITOR_LINK);

        sleep(1, TimeUnit.SECONDS);

        write(ConfigurationUI.SAMPLE_URL);
        press(KeyCode.TAB);
        write(ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD);
        clickOn(ConfigurationUI.LABEL_BUTTON_OK_LINK_WINDOW);

        clickOn(ConfigurationUI.ID_EDITOR_SAVEBUTTON);
        assert(mainApp.getContents().get(0).getIntroduction().getMarkdown().toString().contains("["+ConfigurationUI.SAMPLE_TEXT_HELLO_WORLD+"]("+ConfigurationUI.SAMPLE_URL+")"));
    }
}
