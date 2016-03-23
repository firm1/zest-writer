/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zestedesavoir.zestwriter.utils.readability;

import com.zestedesavoir.zestwriter.utils.ZestWriterErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * @author Panos Ipeirotis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Utilities {

	public static Double round(double d, int decimalPlace) {
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)

		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

    public static Document getAmazonResponse(String url) throws FactoryConfigurationError {
        String page;

        boolean done = false;
        int trial = 0;
        do {
            page = Utilities.getPage(url);
            if (page == null && trial<3) {
                Utilities.sleep(2);
                trial++;
            } else {
                done = true;
            }
        } while (!done);

        Document d;
        if (page == null) {
            System.out.println("Error:"+url);
            //Utilities.sleep(5);
            d = null;
        } else {
            d = Utilities.getXMLFromFile(page);
        }
        return d;
    }

    public static String cleanLine(String line) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c < 128 && Character.isLetter(c)) {
                buffer.append(c);
            } else {
                buffer.append(' ');
            }
        }
        return buffer.toString().toLowerCase();
    }

    public static String cleanForXML(String line) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c < 128 && Character.isLetter(c)) {
                buffer.append(c);
            } else {
                buffer.append('_');
            }
        }
        return buffer.toString();
    }

    /*
    public static void writeFile(String in, File out) {
        try {
            if (!(new File(out.getParent()).exists())) {
                (new File(out.getParent())).mkdirs();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            bw.write(in);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public static String getFile(String FileName) {
        StringBuilder buffer = new StringBuilder();

        try {
            BufferedReader dataInput = new BufferedReader(new FileReader(new File(FileName)));
            String line;

            while ((line = dataInput.readLine()) != null) {
                // buffer.append(cleanLine(line.toLowerCase()));
                buffer.append(line);
                buffer.append('\n');
            }
            dataInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer.toString();
    }

    public static String getFile(File f) {
        StringBuilder buffer = new StringBuilder();

        try {
            BufferedReader dataInput = new BufferedReader(new FileReader(f));
            String line;

            while ((line = dataInput.readLine()) != null) {
                // buffer.append(cleanLine(line.toLowerCase()));
                buffer.append(line);
                buffer.append('\n');
            }
            dataInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer.toString();
    }

    public static TreeSet<String> getWords(String TextFile) {
        TreeSet<String> result = new TreeSet<>();
        StringTokenizer st = new StringTokenizer(TextFile);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    public static String getPage(String URLName) {
        StringBuilder buffer = new StringBuilder();

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection connection = (HttpURLConnection) new URL(URLName).openConnection();
                //connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
                connection.connect();

                BufferedReader dataInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = dataInput.readLine()) != null) {
                    buffer.append(line);
                    // buffer.append(cleanLine(line.toLowerCase()));
                    buffer.append('\n');
                }
                dataInput.close();
            } catch (MalformedURLException e) {
                //e.printStackTrace();
                return null;

            } catch (IOException e) {
                //e.printStackTrace();
                return null;
            }



        return buffer.toString();
    }

    public static void sleep(int secs) {
        try {
            Thread.sleep(secs*1000);
        } catch (InterruptedException e) {
           e.printStackTrace();
        }
    }

    /**
     * Helper function. It reads an XML file and returns the in-memory
     * representation
     *
     * It accepts only valid documents
     *
     * @param file
     * @return Returns the in-memory XML representation of the stored XML file
     * @throws FactoryConfigurationError
     */
    public static Document getXMLFromFile(String file) throws FactoryConfigurationError {

        Document MIQuery = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // factory.setValidating(true);
            // Amazon does not put a DTD
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            factory.setCoalescing(true);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ZestWriterErrorHandler());
            InputSource inputSource = new InputSource(new StringReader(file));
            MIQuery = builder.parse(inputSource);
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
        } catch (ParserConfigurationException | IOException | FactoryConfigurationError pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }
        return MIQuery;
    }

    /**
     * Helper function. It reads an XML file and returns the in-memory
     * representation
     *
     * It accepts only valid documents
     *
     * @param MIxmlQuery
     * @return Creates an empty XML document
     * @throws FactoryConfigurationError
     */
    public static Document getXML() throws FactoryConfigurationError {

        Document MIQuery = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // factory.setValidating(true);
            // Amazon does not put a DTD
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            factory.setCoalescing(true);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ZestWriterErrorHandler());
            MIQuery = builder.newDocument();
        } catch (ParserConfigurationException | FactoryConfigurationError pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }
        return MIQuery;
    }

    // This method writes a DOM document to a file
    public static void writeXmlFile(Document doc, String filename) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerException ignored) {
        }
    }

    // This method writes a DOM document to a file
    public static void writeXmlFile(Document doc, File file) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerException ignored) {
        }
    }

    // This method retuns an XML string from the DOM document
    public static String writeXmlString(Document doc) {

        StringWriter sw = new StringWriter();

        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            Result result = new StreamResult(sw);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerException ignored) {
        }
        return sw.toString();

    }

    public static String httpget( String url ) {
        try {
            StringBuffer sb = new StringBuffer();
            URL href = new URL(url);
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection hc = (HttpURLConnection) href.openConnection();
            String ua="Mozilla/4.0 (compatible; MSIE 6.0; WINDOWS; .NET CLR 1.1.4322)";
            hc.setRequestProperty("user-agent", ua);
            hc.setRequestMethod("GET");
            hc.connect();

            InputStream is = hc.getInputStream();
            int i;
            while ( (i = is.read() ) != -1 ) {
                char c = (char) i;
                sb.append(c);
            }
            is.close();
            hc.disconnect();
            return new String(sb);
        } catch (Exception e) {
            return null;
        }
    }


}