package com.zestedesavoir.zestwriter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Extract;
import com.zestedesavoir.zestwriter.model.MetaContent;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zestedesavoir.zestwriter.utils.ZdsHttp.toSlug;

public class GithubHttp {
    static Logger logger = LoggerFactory.getLogger(GithubHttp.class);

    public static String getGithubZipball(String owner, String repo, String destFolder) throws IOException {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        String urlForGet = "https://api.github.com/repos/" + owner + "/" + repo + "/zipball/";

        HttpGet get = new HttpGet(urlForGet);

        HttpResponse response = httpclient.execute(get);

        InputStream is = response.getEntity().getContent();
        String filePath = destFolder + File.separator + repo + ".zip";
        FileOutputStream fos = new FileOutputStream(new File(filePath));

        int inByte;
        while ((inByte = is.read()) != -1)
            fos.write(inByte);
        is.close();
        fos.close();
        return filePath;
    }

    public static File unzipOnlineContent(String zipFilePath, String destFolder) {
        String dirname = Paths.get(zipFilePath).getFileName().toString();
        dirname = dirname.substring(0, dirname.length() - 4);
        // create output directory is not exists
        System.out.println("dirname = "+dirname);
        File folder = new File(destFolder + File.separator + dirname);
        logger.debug("Tentative de dezippage de " + zipFilePath + " dans " + folder.getAbsolutePath());
        if (!folder.exists()) {
            folder.mkdir();
            logger.info("Dézippage dans " + folder.getAbsolutePath() + " réalisé avec succès");
        } else {
            logger.debug("Le répertoire dans lequel vous souhaitez dezipper existe déjà ");
        }
        ZipUtil.unpack(new File(zipFilePath), folder);
        return folder;
    }

    public static Content loadManifest(String folder, String owner, String repo) throws IOException {
        String projecUrl = "https://api.github.com/repos/"+owner+"/"+repo;
        String title = null;

        String json = Request.Get(projecUrl).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(json, Map.class);
        if(map.containsKey("description")) {
            title = (String) map.get("description");
        }
        Content current = new Content("container", toSlug(title), title, "introduction.md", "conclusion.md", new ArrayList<MetaContent>(), 2, "CC-BY", title, "TUTORIAL");
        // read all directory
        current.getChildren ().addAll (loadDirectory (folder.length () + File.separator.length (), new File(folder)));
        current.setBasePath (folder);
        File fileIntro = new File (current.getFilePath (), "introduction.md");
        File fileConclu = new File (current.getFilePath (), "conclusion.md");
        try {
            if(!fileIntro.exists ()) {
                fileIntro.createNewFile ();
            }
            if(!fileConclu.exists ()) {
                fileConclu.createNewFile ();
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }

        return current;
    }

    private static List<MetaContent> loadDirectory(int countBase, File folder) {
        List<MetaContent> metas = new ArrayList<> ();
        File[] listF = folder.listFiles();
        for(File file : listF) {
            String name = file.getName();
            if(file.isDirectory ()) {
                String intro = file.getAbsolutePath ().substring (countBase)+File.separator+"introduction.md";
                String conclu = file.getAbsolutePath ().substring (countBase)+File.separator+"conclusion.md";
                intro = intro.replace (File.separator, "/");
                conclu = conclu.replace (File.separator, "/");
                MetaContent container = new Container ("container", ZdsHttp.toSlug (name), name, intro ,conclu, new ArrayList<MetaContent> ());
                ((Container)container).getChildren().addAll (loadDirectory (countBase, file));
                metas.add(container);
                File fileIntro = new File (file, "introduction.md");
                File fileConclu = new File (file, "conclusion.md");
                try {
                    if(!fileIntro.exists ()) {
                        fileIntro.createNewFile ();
                    }
                    if(!fileConclu.exists ()) {
                        fileConclu.createNewFile ();
                    }
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            } else if(file.isFile ()) {
                int pos = name.lastIndexOf(".");
                if (name.endsWith ("md")) {
                    if((! name.equals ("introduction.md")) && (! name.equals ("conclusion.md")) ) {
                        if (pos > 0) {
                            name = name.substring (0, pos);
                        }
                        String text = file.getAbsolutePath ().substring (countBase);
                        text = text.replace (File.separator, "/");
                        MetaContent extract = new Extract ("extract", ZdsHttp.toSlug (name), name, text);
                        metas.add (extract);
                    }
                }
            }
        }
        return metas;
    }
}
