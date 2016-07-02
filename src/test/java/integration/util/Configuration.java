package integration.util;

import com.zestedesavoir.zestwriter.MainApp;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {

    public static final String username = "ZestWriterQa";
    public static final String password = "zestwriterqa";

    public static String getWorkspacePath (MainApp mainApp) {
        return com.zestedesavoir.zestwriter.utils.Configuration.getDefaultWorkspace() + File.separator + "offline" ;
    }

    public static String getPathFixtureLeGuideDuContributeur(){
        URL location = UtilFixtureLeGuideDuContributeur.class.getResource("/fixtures/le-guide-du-contributeur/manifest.json");
        String pathWithFileName = location.getPath().toString().replaceFirst("^/(.:/)", "$1");

        return Paths.get(pathWithFileName).getParent().toString();
    }

}
