/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zestedesavoir.zestwriter.utils.readability;

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
}