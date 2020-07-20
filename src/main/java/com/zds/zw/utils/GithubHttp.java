package com.zds.zw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.zds.zw.utils.ZdsHttp.toSlug;
import static com.zds.zw.view.com.FunctionTreeFactory.generateMetadataAttributes;

public class GithubHttp {
    private static final Logger log = LoggerFactory.getLogger(GithubHttp.class);

    private GithubHttp() {
    }

    public static String getGithubZipball(String owner, String repo, String destFolder) throws IOException {
        HttpClient client = getHttpClient();
        String urlForGet = "https://api.github.com/repos/" + owner + "/" + repo + "/zipball/";
        String filePath = destFolder + File.separator + repo + ".zip";

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(urlForGet))
                .GET();
        HttpRequest request = builder.build();
        try {
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofFile(Path.of(filePath)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static HttpClient getHttpClient() {
        String githubUser = System.getProperty("zw.github_user");
        String githubToken = System.getProperty("zw.github_token");

        if(githubUser != null && !"".equals(githubUser) && githubToken != null && !"".equals(githubToken)) {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(githubUser, githubToken.toCharArray());
                }
            }).build();
        } else {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }
    }

    public static File unzipOnlineContent(String zipFilePath, String destFolder) {
        String dirname = Paths.get(zipFilePath).getFileName().toString();
        dirname = dirname.substring(0, dirname.length() - 4);
        // create output directory is not exists
        File folder = new File(destFolder + File.separator + dirname);
        log.debug("Tentative de dezippage de " + zipFilePath + " dans " + folder.getAbsolutePath());
        if (!folder.exists()) {
            folder.mkdir();
            log.info("Dézippage dans " + folder.getAbsolutePath() + " réalisé avec succès");
        } else {
            log.debug("Le répertoire dans lequel vous souhaitez dezipper existe déjà ");
        }
        ZipUtil.unpack(new File(zipFilePath), folder);
        return folder;
    }

    public static Content loadManifest(String folder, String owner, String repo) throws IOException {
        String projecUrl = "https://api.github.com/repos/"+owner+"/"+repo;
        String title = null;
        log.debug("Tentative de connexion à l'url : "+projecUrl);
        HttpClient client = getHttpClient();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(projecUrl))
                .GET();

        HttpRequest request = builder.build();
        try {
            String json = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(json, Map.class);
            if(map.containsKey("description")) {
                title = (String) map.get("description");
            }
            Content current = new Content("container",
                    toSlug(title),
                    title,
                    Constant.DEFAULT_INTRODUCTION_FILENAME, Constant.DEFAULT_CONCLUSION_FILENAME,
                    new ArrayList<>(),
                    "2.1",
                    "CC-BY",
                    title,
                    "TUTORIAL",
                    "true");
            // read all directory
            current.getChildren ().addAll (loadDirectory (folder.length () + File.separator.length (), new File(folder)));
            current.setBasePath (folder);
            generateMetadataAttributes(current.getFilePath());
            return current;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<MetaContent> loadDirectory(int countBase, File folder) {
        List<MetaContent> metas = new ArrayList<> ();
        File[] listF = folder.listFiles();
        for(File file : listF != null ? listF : new File[0]) {
            String name = file.getName();
            if(file.isDirectory ()) {
                String intro = file.getAbsolutePath ().substring (countBase)+File.separator+Constant.DEFAULT_INTRODUCTION_FILENAME;
                String conclu = file.getAbsolutePath ().substring (countBase)+File.separator+Constant.DEFAULT_CONCLUSION_FILENAME;
                intro = intro.replace (File.separator, "/");
                conclu = conclu.replace (File.separator, "/");
                MetaContent container = new Container("container", toSlug (name), name, intro ,conclu, new ArrayList<>(), "true");
                ((Container)container).getChildren().addAll (loadDirectory (countBase, file));
                metas.add(container);
                generateMetadataAttributes(file.getAbsolutePath());
            } else if(file.isFile ()) {
                int pos = name.lastIndexOf('.');
                if (name.endsWith ("md")) {
                    String[] meta = {Constant.DEFAULT_INTRODUCTION_FILENAME, Constant.DEFAULT_CONCLUSION_FILENAME};
                    if(!Arrays.asList(meta).contains(name)) {
                        if (pos > 0) {
                            name = name.substring (0, pos);
                        }
                        String text = file.getAbsolutePath ().substring (countBase);
                        text = text.replace (File.separator, "/");
                        MetaContent extract = new Extract("extract", toSlug (name), name, text);
                        metas.add (extract);
                    }
                }
            }
        }
        return metas;
    }
}
