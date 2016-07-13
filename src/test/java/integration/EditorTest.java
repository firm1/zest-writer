package integration;

import annotation.MediumTest;
import com.zestedesavoir.zestwriter.MainApp;
import integration.util.UtilFixtureLeGuideDuContributeur;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxToolkit.*;

public class EditorTest  extends FxRobot {

    private MainApp mainApp;
    private Map<String, String> actionButtonAndResult;

    private static String TO_WRITE = "Hello World";
    private static String ID_EDITOR = "#editor";

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
    public void bold() throws IOException, InterruptedException, URISyntaxException {
        UtilFixtureLeGuideDuContributeur.loadFixtureLeGuideDuContributeur(mainApp);

        sleep(4, TimeUnit.SECONDS);

        doubleClickOn("Introduction");

        sleep(4, TimeUnit.SECONDS);

        clickOn(ID_EDITOR);

        for (Map.Entry<String,String> e : actionButtonAndResult.entrySet()){

            push(CONTROL, A);
            push(DELETE);
            write(TO_WRITE);
            push(CONTROL, A);

            clickOn(e.getKey());
            clickOn("#SaveButton");

            assert(mainApp.getContents().get(0).getIntroduction().getMarkdown().toString().contains(e.getValue()));
        }
    }

    private void addActions() {
        actionButtonAndResult = new LinkedHashMap<>();
        actionButtonAndResult.put("#bold", "**"+TO_WRITE+"**");
        actionButtonAndResult.put("#italic", "*"+TO_WRITE+"*");
        actionButtonAndResult.put("#strikethrough", "~~"+TO_WRITE+"~~");
        actionButtonAndResult.put("#keyboard", "||"+TO_WRITE+"||");
        actionButtonAndResult.put("#superscript", "^"+TO_WRITE+"^");
        actionButtonAndResult.put("#subscript", "~"+TO_WRITE+"~");
        actionButtonAndResult.put("#center", "->"+TO_WRITE+"<-");
        actionButtonAndResult.put("#right", "->"+TO_WRITE+"->");
        actionButtonAndResult.put("#bullet", "-"+TO_WRITE);
        actionButtonAndResult.put("#numbered", "1. "+TO_WRITE);
        actionButtonAndResult.put("#header", "# "+TO_WRITE);
        actionButtonAndResult.put("#quote", "> "+TO_WRITE);
    }


}
