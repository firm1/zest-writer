package com.zestedesavoir.zestwriter.utils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class ZestWriterErrorHandler implements ErrorHandler {

	// ignore fatal errors (an exception is guaranteed)
	@Override
	public void fatalError(SAXParseException err) {
		writeErrorToConsole(err);
	}

	// treat validation errors as fatal
	@Override
	public void error(SAXParseException err) throws SAXParseException {
		writeErrorToConsole(err);
		throw err;
	}

	// dump warnings too
	@Override
	public void warning(SAXParseException err) {
		writeErrorToConsole(err);
	}

	private void writeErrorToConsole(SAXParseException  err) {
		System.out.println("** Warning" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
		System.out.println("   " + err.getMessage());
	}

}