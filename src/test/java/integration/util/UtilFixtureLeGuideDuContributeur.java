package integration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import org.testfx.api.FxRobot;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        // absolute path to le guide du contributeur's fixatures
        URL location = UtilFixtureLeGuideDuContributeur.class.getResource("/fixtures/le-guide-du-contributeur/introduction.md");
        String pathWithFileName = location.getPath().toString().replaceFirst("^/(.:/)", "$1");

        Path path = Paths.get(pathWithFileName);
        File from = new File(path.getParent().toString());

        FileUtils.copyDirectory(from, to);
    }

    private static void loadFixture(MainApp mainApp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        File manifest = new File(getWorkspacePath(mainApp)+ File.separator + "le-guide-du-contributeur" + File.separator+"manifest.json");
        Content content = mapper.readValue(manifest, Content.class);
        content.setRootContent(content, manifest.getParentFile().getAbsolutePath());
        FunctionTreeFactory.switchContent(content, mainApp.getContents());
    }

}
