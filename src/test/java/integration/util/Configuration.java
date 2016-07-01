package integration.util;

import com.zestedesavoir.zestwriter.MainApp;

import java.io.File;

public class Configuration {

    public static final String username = "ZestWriterQa";
    public static final String password = "zestwriterqa";

    public static String getWorkspacePath (MainApp mainApp) {
        return com.zestedesavoir.zestwriter.utils.Configuration.getDefaultWorkspace() + File.separator + "offline" ;
    }

}
