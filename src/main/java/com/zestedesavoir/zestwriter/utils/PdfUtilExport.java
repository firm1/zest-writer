package com.zestedesavoir.zestwriter.utils;

import com.openhtmltopdf.DOMBuilder;
import com.openhtmltopdf.extend.HttpStream;
import com.openhtmltopdf.extend.HttpStreamFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;
import com.zestedesavoir.zestwriter.MainApp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PdfUtilExport {
    private String titleContent;
    private String authorContent;
    private String srcHtmlPath;
    private String destPdfPath;
    private final static int FONT_SIZE_TITLE = 20;
    private final static int FONT_SIZE_AUTHOR = 12;
    private static PDFont FONT_STYLE_COVER;
    private static File FONT_MERRIWEATHER_REGULAR;
    private static File FONT_MERRIWEATHER_BOLD;
    private static File FONT_MERRIWEATHER_ITALIC;
    private static File FONT_SOURCE_CODE_PRO;

    public PdfUtilExport(String titleContent, String authorContent, String srcHtmlPath, String destPdfPath) {
        this.titleContent = titleContent.toUpperCase();
        this.authorContent = authorContent;
        this.srcHtmlPath = srcHtmlPath;
        this.destPdfPath = destPdfPath;

        FONT_MERRIWEATHER_REGULAR = new File(MainApp.class.getResource("assets/static/fonts/Merriweather-Regular.ttf").getFile());
        FONT_MERRIWEATHER_BOLD = new File(MainApp.class.getResource("assets/static/fonts/Merriweather-Bold.ttf").getFile());
        FONT_MERRIWEATHER_ITALIC = new File(MainApp.class.getResource("assets/static/fonts/Merriweather-Italic.ttf").getFile());
        FONT_SOURCE_CODE_PRO = new File(MainApp.class.getResource("assets/static/fonts/SourceCodePro-Regular.ttf").getFile());
        XRLog.setLoggingEnabled(false);
    }

    private List<String> wrapText(float width) throws IOException {

        List<String> lines = new ArrayList<>();
        int lastSpace = -1;
        while (titleContent.length() > 0) {
            int spaceIndex = titleContent.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = titleContent.length();
            String subString = titleContent.substring(0, spaceIndex);
            float size = FONT_SIZE_TITLE * FONT_STYLE_COVER.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0)
                    lastSpace = spaceIndex;
                subString = titleContent.substring(0, lastSpace);
                lines.add(subString);
                titleContent = titleContent.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == titleContent.length()) {
                lines.add(titleContent);
                titleContent = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }

    private void addCoverpage() throws IOException {
        float leading = 1.5f * FONT_SIZE_TITLE;
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        FONT_STYLE_COVER = PDTrueTypeFont.loadTTF(document, FONT_MERRIWEATHER_BOLD);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setNonStrokingColor(25, 81, 107);
        contentStream.fillRect(0, 0, page.getMediaBox().getWidth(), (page.getMediaBox().getHeight() / 2) - 10);
        contentStream.fillRect(0, (page.getMediaBox().getHeight() / 2) + 10, page.getMediaBox().getWidth(), (page.getMediaBox().getHeight() / 2) - 10);
        contentStream.setNonStrokingColor(248, 173, 50);
        contentStream.fillRect(0, (page.getMediaBox().getHeight() / 2) - 10, page.getMediaBox().getWidth(), 20);

        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(FONT_STYLE_COVER, FONT_SIZE_AUTHOR);
        contentStream.newLineAtOffset(20, 20);
        contentStream.showText(authorContent);
        contentStream.setFont(FONT_STYLE_COVER, FONT_SIZE_TITLE);
        contentStream.newLineAtOffset((page.getMediaBox().getWidth() / 2) - 20, 600);
        List<String> lines = wrapText((page.getMediaBox().getWidth() / 2) - 20);
        for (String line : lines) {
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -leading);
        }
        contentStream.endText();

        contentStream.close();
        File temp = File.createTempFile("coverpage-zds", ".pdf");
        document.save(temp);
        document.close();

        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        mergerUtility.addSource(temp);
        mergerUtility.addSource(destPdfPath);
        mergerUtility.setDestinationFileName(destPdfPath);
        mergerUtility.mergeDocuments();
    }

    public boolean exportToPdf() {
        log.info("Tentative d'export PDF du fichier '"+srcHtmlPath+"' vers '"+destPdfPath+"'");
        try(OutputStream os = new FileOutputStream(destPdfPath)) {
                // There are more options on the builder than shown below.
                PdfRendererBuilder builder = new PdfRendererBuilder();

                builder.withUri(destPdfPath);
                builder.toStream(os);
                builder.useFont(FONT_MERRIWEATHER_REGULAR,"Merriweather",400, PdfRendererBuilder.FontStyle.NORMAL,true);
                builder.useFont(FONT_MERRIWEATHER_ITALIC,"Merriweather", 400, PdfRendererBuilder.FontStyle.ITALIC,true);
                builder.useFont(FONT_MERRIWEATHER_BOLD,"Merriweather", 700, PdfRendererBuilder.FontStyle.NORMAL, true);
                builder.useFont(FONT_SOURCE_CODE_PRO,"Source Code Pro");
                builder.withW3cDocument(html5ParseDocument(srcHtmlPath, 1000), srcHtmlPath);
                builder.useHttpStreamImplementation(new OkHttpStreamFactory());
                builder.run();
                log.debug("Tentative d'ajout de la page de la couverture");
                addCoverpage();
                log.info("Fichier PDF crée avec succès");
                return true;
        } catch (IOException e1) {
            log.error(e1.getMessage(), e1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private org.w3c.dom.Document html5ParseDocument(String urlStr, int timeoutMs) throws IOException {
        URL url = new URL(urlStr);

        org.jsoup.nodes.Document doc;

        if (url.getProtocol().equalsIgnoreCase("file")) {
            doc = Jsoup.parse(new File(url.getPath()), "UTF-8");
        } else {
            doc = Jsoup.parse(url, timeoutMs);
        }

        return DOMBuilder.jsoup2DOM(doc);
    }
}

@Slf4j
class OkHttpStreamFactory implements HttpStreamFactory {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public HttpStream getUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            final Response response = client.newCall(request).execute();

            return new HttpStream() {
                @Override
                public InputStream getStream() {
                    return response.body().byteStream();
                }

                @Override
                public Reader getReader() {
                    return response.body().charStream();
                }
            };
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}