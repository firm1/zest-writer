package integration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import org.testfx.api.FxRobot;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static integration.util.Configuration.getWorkspacePath;

public class UtilFixtureLeGuideDuContributeur extends FxRobot {

    public static void loadFixtureLeGuideDuContributeur (MainApp mainApp) throws IOException, URISyntaxException {
        addNewFixture(mainApp);
        loadFixture(mainApp);
    }

    private static void addNewFixture(MainApp mainApp) throws IOException, URISyntaxException {
        File to = new File(getWorkspacePath(mainApp) + File.separator + "le-guide-du-contributeur");

        if (to.exists()) {
            FileUtils.deleteDirectory(to);
        }

        // absolute path to le guide du contributeur's fixtures
        File from = new File(integration.util.Configuration.getPathFixtureLeGuideDuContributeur());

        FileUtils.copyDirectory(from, to);
    }

    private static void loadFixture(MainApp mainApp) throws IOException {
        File manifest = new File (getManifest (mainApp));
        Content content = getContent (manifest);
        content.setRootContent(content, getBasePath(mainApp).getAbsolutePath());
        FunctionTreeFactory.switchContent(content, mainApp.getContents());
    }

    public static Content getContent(File manifest) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(manifest, Content.class);
    }

    private static File getBasePath (MainApp mainApp) {
        return new File(getWorkspacePath(mainApp)+ File.separator + "le-guide-du-contributeur");
    }

    public static String getManifest(MainApp mainApp) {
        return getBasePath (mainApp) + File.separator+"manifest.json";
    }
}
