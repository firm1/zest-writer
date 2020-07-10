package com.zds.zw.utils;

import com.zds.zw.model.Constant;
import com.zds.zw.model.MetadataContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.Duration;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Java Api class for exchange with ZdS
 */
public class ZdsHttp {
    private String galleryId;
    private String login;
    private String password;
    private String hostname;
    private String port;
    private String protocol;
    private boolean authenticated = false;
    private List<MetadataContent> contentListOnline;
    public String cookies;
    private HttpClient client;
    private String localSlug;
    private Configuration config;
    private static final Logger log = LoggerFactory.getLogger(ZdsHttp.class);

    /**
     * ZdsHttp Constructor with configuration object
     * @param config configuration instance
     */
    public ZdsHttp(Configuration config) {
        super();
        this.config = config;
        this.protocol = config.getAdvancedServerProtocol();
        this.hostname = config.getAdvancedServerHost();
        this.port = config.getAdvancedServerPort();
        initContext();
    }

    public String getLogin() {
        return login;
    }

    public String getLocalSlug() {
        return localSlug;
    }


    public String getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
    }


    public void setLocalSlug(String localSlug) {
        this.localSlug = localSlug;
    }

    public List<MetadataContent> getContentListOnline() {
        return contentListOnline;
    }

    private String getBaseUrl() {
        if ("80".equals(this.port) || ("443".equals(this.port) && "https".equals(this.protocol))) {
            return this.protocol + "://" + this.hostname;
        } else {
            return this.protocol + "://" + this.hostname + ":" + this.port;
        }
    }

    private String getLoginUrl() {
        return getBaseUrl() + "/membres/connexion/?next=/";
    }

    private String getPersonalTutorialUrl() {
        return getBaseUrl() + "/tutoriels/voir/" + login + "/";
    }

    private String getPersonalArticleUrl() {
        return getBaseUrl() + "/articles/voir/" + login + "/";
    }

    private String getPersonalOpinionUrl() {
        return getBaseUrl() + "billets/voir/" + login + "/";
    }

    private String getDownloadDraftContentUrl(String id, String slug) {
        return getBaseUrl() + "/contenus/telecharger/" + id + "/" + slug + "/";
    }

    private String getDraftContentUrl(String id, String slug) {
        return getBaseUrl() + "/contenus/" + id + "/" + slug + "/";
    }

    private String getImportImageUrl() {
        return getBaseUrl() + "/galerie/image/ajouter/" + getGalleryId()+ "/";
    }

    private String getImportContenttUrl(String idContent, String slugContent) {
        return getBaseUrl() + "/contenus/importer/" + idContent + "/" + slugContent + "/";
    }

    private String getImportNewContenttUrl() {
        return getBaseUrl() + "/contenus/importer/archive/nouveau/";
    }

    public String getOnlineContentPathDir() {
        return config.getOnlineSaver().getBaseDirectory();
    }

    public String getOfflineContentPathDir() {
        return config.getOfflineSaver().getBaseDirectory();
    }

    /**
     * Transform any string on slug. Just alphanumeric, dash or underscore characters.
     * @param input string to convert on slug
     * @return slug string
     */
    public static String toSlug(String input) {
        String nowhitespace = Constant.WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
        String slug = Constant.NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }


    private void initContext() {
        client = HttpClient.newHttpClient();
        contentListOnline = new ArrayList<>();
    }

    /**
     * Authentication with google account
     * @param cookies cookies list keys from google auth
     * @param login username associated to zds login
     * @param id user id on ZdS associated to login
     */
    public void authToGoogle(List<HttpCookie> cookies, String login, String id) {
        /*
        if(login != null && id != null) {
            this.login = login;
            this.idUser = id;
            log.info("L'identifiant de l'utilisateur " + this.login + " est : " + idUser);
            cookieStore = new BasicCookieStore();
            for(HttpCookie cookie:cookies) {
                BasicClientCookie c = new BasicClientCookie(cookie.getName(), cookie.getValue());
                c.setDomain(cookie.getDomain());
                c.setPath(cookie.getPath());
                c.setSecure(cookie.getSecure());
                c.setVersion(cookie.getVersion());
                c.setComment(cookie.getComment());
                cookieStore.addCookie(c);
            }
            context.setCookieStore(cookieStore);
            this.authenticated = true;
        }
        else {
            log.debug("Le login de l'utilisateur n'a pas pu être trouvé");
        }

         */
    }

    private HttpResponse postRequest(String url, Map<Object, Object> data, String referer) {
        if(referer == null) {
            referer = url;
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Referer", referer)
                .header("Accept-Language", "fr-FR")
                .header("Cache-Control", "max-age=0")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .POST(ofFormData(data));

        if(this.cookies != null) {
            builder = builder.header("Cookie", this.cookies);
        }

        HttpRequest request = builder.build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse getRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if(this.cookies != null) {
            builder = builder.header("Cookie", this.cookies);
        }
        HttpRequest request = builder.build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse getRequestFile(String url, Path file) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if(this.cookies != null) {
            builder = builder.header("Cookie", this.cookies);
        }
        HttpRequest request = builder.build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofFile(file));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private String getUniquesStringCookie(HttpHeaders headers) {
        return String.join("; ",
                headers.allValues("set-cookie").stream()
                        .map(h -> {
                            List<String> elt = new ArrayList<>();
                            String[] ss = h.split(";");
                            for(String s:ss) {
                                elt.add(s.trim());
                            }
                            return elt;
                        })
                        .map(HashSet::new)
                        .reduce(new HashSet<>(), (l1, l2) -> {l1.addAll(l2); return l1;})
        );
    }

    public boolean login(String username, String password) {
        HttpResponse getContentLoginResponse = getRequest(getLoginUrl());
        if(getContentLoginResponse.headers().firstValue("Set-Cookie").isPresent()) {
            this.cookies = getUniquesStringCookie(getContentLoginResponse.headers());
        }

        Document doc = Jsoup.parse(getContentLoginResponse.body().toString());
        Elements inputs = doc.select("form input");
        Optional<String> token = inputs.stream()
                .filter(input -> input.hasAttr("name"))
                .filter(input -> input.attr("name").equals("csrfmiddlewaretoken"))
                .map(input -> input.attr("value"))
                .findAny();

        Map<Object, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        data.put("csrfmiddlewaretoken", token.get());

        HttpResponse response = postRequest(getLoginUrl(), data, null);

        this.cookies = getUniquesStringCookie(response.headers());
        HttpResponse getContentHomeResponse = getRequest(getBaseUrl());

        if(getContentHomeResponse.body().toString().contains("my-account-dropdown")) {
            this.authenticated = true;
            this.login = username;
            log.info("Utilisateur " + this.login + " connecté");
        } else {
            log.debug("Utilisateur " + this.login + " non connecté via " + getLoginUrl());
        }
        return this.authenticated;
    }

    public void initGalleryId(String idContent, String slugContent) throws IOException {
        String url = getDraftContentUrl(idContent, slugContent);
        log.info("Tentative de récupération de la page offline du contenu "+idContent+"("+slugContent+")");

        String getContent = getRequest(url).body().toString();
        log.info("Tentative réussie");

        Document doc = Jsoup.parse(getContent);

        Elements links = doc.select("a[href^=/galerie/]");
        for (Element link : links) {
            String ref = link.attr("href").trim();
            String[] tab = ref.split("/");
            if(tab.length >= 4) {
                this.galleryId = tab[2];
            }
        }

    }


    public void logout() {
        this.login = null;
        this.password = null;
        this.galleryId = null;
        this.cookies = null;
        this.authenticated = false;
    }

    public String importImage(File file) throws IOException {
        if(getGalleryId() != null) {
            String url = getImportImageUrl();
            HttpResponse response = getRequest(getImportImageUrl());
            Document doc = Jsoup.parse(response.body().toString());
            Elements inputs = doc.select("form input");
            Optional<String> token = inputs.stream()
                    .filter(input -> input.hasAttr("name"))
                    .filter(input -> input.attr("name").equals("csrfmiddlewaretoken"))
                    .map(input -> input.attr("value"))
                    .findAny();

            MultiPartBodyPublisher publisher = new MultiPartBodyPublisher()
                    .addPart("title", "Image importée via ZestWriter")
                    .addPart(Constant.CSRF_ZDS_KEY, token.get())
                    .addPart("physical", Path.of(file.getAbsolutePath()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                    .header("Cookie", this.cookies)
                    .header("Referer", url)
                    .header("Accept-Language", "fr-FR")
                    .header("Cache-Control", "max-age=0")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(Duration.ofMinutes(1))
                    .POST(publisher.build())
                    .build();

            try {
                HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                doc = Jsoup.parse(httpResponse.body().toString());
                Elements endPoints = doc.select("input[name=avatar_url]");
                if(!endPoints.isEmpty()) {
                    return getBaseUrl() + endPoints.first().attr("value").trim();
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        return "http://";

    }

    private boolean uploadContent(String filePath, String url, String msg) throws IOException{

        HttpResponse response = getRequest(url);
        Document doc = Jsoup.parse(response.body().toString());
        Elements inputs = doc.select("form input");
        Optional<String> token = inputs.stream()
                .filter(input -> input.hasAttr("name"))
                .filter(input -> input.attr("name").equals("csrfmiddlewaretoken"))
                .map(input -> input.attr("value"))
                .findAny();

        MultiPartBodyPublisher publisher = new MultiPartBodyPublisher()
                .addPart("subcategory", "115")
                .addPart("msg_commit", msg)
                .addPart(Constant.CSRF_ZDS_KEY, token.get())
                .addPart("archive", Path.of(filePath));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                .header("Cookie", this.cookies)
                .header("Referer", url)
                .header("Accept-Language", "fr-FR")
                .header("Cache-Control", "max-age=0")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .timeout(Duration.ofMinutes(1))
                .POST(publisher.build())
                .build();
        try {
            HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse.body().toString());

            switch (httpResponse.statusCode()) {
                case 200:
                    return !httpResponse.body().toString().contains ("alert-box alert");
                case 404:
                    log.debug("L'id cible du contenu ou le slug est incorrect. Donnez de meilleur informations");
                    return false;
                case 403:
                    log.debug("Vous n'êtes pas autorisé à uploader ce contenu. Vérifiez que vous êtes connecté");
                    return false;
                case 413:
                    log.debug("Le fichier que vous essayer d'envoyer est beaucoup trop lourd. Le serveur n'arrive pas à le supporter");
                    return false;
                default:
                    log.debug("Problème d'upload du contenu. Le code http de retour est le suivant : "+httpResponse.statusCode());
                    return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean importNewContent(String filePath, String msg) throws IOException {

        log.debug("Tentative d'import via l'url : " + getImportNewContenttUrl());
        return uploadContent(filePath, getImportNewContenttUrl(), msg);
    }

    public boolean importContent(String filePath, String targetId, String targetSlug, String msg)
            throws IOException {
        log.debug("Tentative d'import via l'url : " + getImportContenttUrl(targetId, targetSlug));
        return uploadContent(filePath, getImportContenttUrl(targetId, targetSlug), msg);
    }

    public void initInfoOnlineContent(String type) throws IOException {

        HttpResponse response = null;

        log.info("Initialisation des metadonnées contenus en ligne de type " + type);

        if ("tutorial".equals(type)) {
            log.info("Tentative de joindre l'url : " + getPersonalTutorialUrl());
            response = getRequest(getPersonalTutorialUrl());
        } else if ("article".equals(type)) {
            log.info("Tentative de joindre l'url : " + getPersonalArticleUrl());
            response = getRequest(getPersonalArticleUrl());
        } else if ("opinion".equals(type)) {
            log.info("Tentative de joindre l'url : " + getPersonalOpinionUrl());
            response = getRequest(getPersonalOpinionUrl());
        }
        log.info("Tentative réussie");

        Document doc = Jsoup.parse(response.body().toString());
        Elements sections = doc.select("article > a[href^=/contenus/]");
        for (Element section : sections) {
            String ref = section.attr("href").trim();
            log.trace("Chaine à decrypter pour trouver le slug : " + ref);
            String[] tab = ref.split("/");
            MetadataContent onlineContent = new MetadataContent(tab[2], tab[3], type);
            if(!getContentListOnline().contains(onlineContent)) {
                getContentListOnline().add(onlineContent);
            }
        }
        log.info("Contenu de type " + type + " chargés en mémoire : " + getContentListOnline());
    }

    private String getTargetSlug(String targetId, String type) {
        for (MetadataContent metadata : contentListOnline) {
            if (metadata.getId().equals(targetId) && metadata.getType().equalsIgnoreCase(type)) {
                return metadata.getSlug();
            }
        }
        return null;
    }

    public void downloaDraft(String targetId, String type) throws IOException {
        String targetSlug = getTargetSlug(targetId, type);
        String url = getDownloadDraftContentUrl(targetId, targetSlug);
        String filePath = getOnlineContentPathDir() + File.separator + targetSlug + ".zip";
        getRequestFile(url, Path.of(filePath));
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

    public void unzipOnlineContent(String zipFilePath) {

        String dirname = Paths.get(zipFilePath).getFileName().toString();
        dirname = dirname.substring(0, dirname.length() - 4);
        // create output directory is not exists
        File folder = new File(getOfflineContentPathDir() + File.separator + dirname);
        log.debug("Tentative de dezippage de " + zipFilePath + " dans " + folder.getAbsolutePath());

        byte[] buffer = new byte[1024];
        if (!folder.exists()) {
            folder.mkdir();
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
                // get the zip file content

                // get the zipped file list entry
                ZipEntry ze = zis.getNextEntry();

                while (ze != null) {
                    String fileName = ze.getName();
                    log.trace("Traitement du fichier : " + fileName);
                    File newFile = new File(folder + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();

                    try(FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        ze = zis.getNextEntry();
                    } catch (IOException ioe) {
                        log.error(ioe.getMessage(), ioe);
                    }
                }
                zis.closeEntry();
                log.info("Dézippage dans " + folder.getAbsolutePath() + " réalisé avec succès");

            } catch (IOException ex) {
                log.debug("Echec de dezippage dans " + zipFilePath, ex);
            }
        } else {
            log.debug("Le répertoire dans lequel vous souhaitez dezipper existe déjà");
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getZdsZipball(String id, String slug, String type, String destFolder) throws IOException{
        String urlForGet = getBaseUrl() + "/" + type + "/zip/"+ id + "/" + slug + ".zip";
        log.debug("Tentative de téléchargement du lien "+urlForGet);
        log.debug("Répertoire de téléchargement cible : "+destFolder);

        String filePath = destFolder + File.separator + slug + ".zip";

        log.debug("Début du téléchargement");
        HttpResponse response = getRequestFile(urlForGet, Path.of(filePath));
        log.debug("Archive téléchargée : "+filePath);
        return filePath;
    }

    public static void main(String[] args) {
        /*
        ZdsHttp zdsHttp = new ZdsHttp(new Configuration("/home/willy"));
        zdsHttp.initContext();
        System.out.println("login ="+zdsHttp.login("firm1", "leshow"));
        try {
            zdsHttp.initGalleryId("1428", "la-ligne-editoriale-officielle-de-zeste-de-savoir-1");
            zdsHttp.initInfoOnlineContent("article");
            zdsHttp.downloaDraft("1428", "article");
            //zdsHttp.uploadContent("/home/willy/zwriter-workspace/online/la-ligne-editoriale-officielle-de-zeste-de-savoir-1.zip", "https://zestedesavoir.com/contenus/importer/1428/la-ligne-editoriale-officielle-de-zeste-de-savoir-1/", "update");
            zdsHttp.importImage(new File("/home/willy/Images/image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            System.out.println(GithubHttp.loadManifest("/tmp/zworkspace/france.code-civil","steeve", "france.code-civil"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
