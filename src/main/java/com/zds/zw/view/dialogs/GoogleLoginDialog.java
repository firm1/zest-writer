package com.zds.zw.view.dialogs;

import com.zds.zw.MainApp;
import com.zds.zw.utils.Configuration;
import com.zds.zw.view.com.CustomDialog;
import javafx.concurrent.Worker.State;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.CookieHandler;
import java.net.CookieManager;

public class GoogleLoginDialog extends CustomDialog<Pair<String, String>> {
	public GoogleLoginDialog(LoginDialog parent) {
        super();
		this.setTitle(Configuration.getBundle().getString("ui.dialog.auth.google.title"));

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        webEngine.setJavaScriptEnabled(false);

        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if(newState == State.RUNNING) {
                if(webEngine.getLocation().contains("accounts.google.com/ServiceLoginAuth")) {
                    scrollPane.setVisible(false);
                }
            }
            if(newState == State.SUCCEEDED) {
                if("https://zestedesavoir.com/".equals(webEngine.getLocation())) {
                    Element elementary = webEngine.getDocument().getDocumentElement();
                    Element logbox = getLogBox(elementary);
                    String pseudo = getPseudo(logbox);
                    String id = getId(logbox);
                    MainApp.getZdsutils().authToGoogle(manager.getCookieStore().getCookies(), pseudo, id);
                    getThis().close();
                    parent.close();
                } else {
                    if(webEngine.getLocation().contains("accounts.google.com/ServiceLoginAuth")) {
                        scrollPane.setVisible(true);
                    }
                }
            }
        });
        webEngine.load("https://zestedesavoir.com/login/google-oauth2/");
	}

	public GoogleLoginDialog getThis() {
		return this;
	}
	private Element getLogBox(Element el) {
        NodeList childNodes = el.getChildNodes();
        if("DIV".equals(el.getNodeName())){
            String attr = el.getAttribute("class");
            if(attr != null && attr.contains("my-account-dropdown")) {
                return el;
            }
        }
        for(int i=0; i<childNodes.getLength(); i++){
            org.w3c.dom.Node item = childNodes.item(i);
            if(item instanceof Element){
                Element res = getLogBox((Element)item);
                if(res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    private String getPseudo(Element logbox){
        NodeList childNodes = logbox.getChildNodes();
        for(int i=0; i<childNodes.getLength(); i++){
            org.w3c.dom.Node item = childNodes.item(i);
            if(item instanceof Element){
                Element find = (Element)item;
                if("SPAN".equals(find.getNodeName())) {
                    return find.getTextContent();
                }
            }
        }
        return null;
    }

    private String getId(Element logbox){
        NodeList childNodes = logbox.getChildNodes();
        for(int i=0; i<childNodes.getLength(); i++){
            org.w3c.dom.Node item = childNodes.item(i);
            if(item instanceof Element){
                Element ulItem = (Element)item;
                if("UL".equals(ulItem.getNodeName())) {
                    NodeList childUlNodes = ulItem.getChildNodes();
                    for(int j=0; j<childUlNodes.getLength(); j++){
                        org.w3c.dom.Node jtem = childUlNodes.item(j);
                        if(jtem instanceof Element){
                            Element liItem = (Element)jtem;
                            if("LI".equals(liItem.getNodeName())) {
                                NodeList childIlNodes = liItem.getChildNodes();
                                for(int k=0; k<childIlNodes.getLength(); k++){
                                    org.w3c.dom.Node ktem = childIlNodes.item(k);
                                    if(ktem instanceof Element){
                                        Element aItem = (Element)ktem;
                                        if("A".equals(aItem.getNodeName())) {
                                            String ref = aItem.getAttribute("href");
                                            if(ref.startsWith("/contenus/tutoriels")) {
                                                String[] splt = ref.split("/");
                                                if(splt.length >= 4) {
                                                    return splt[3];
                                                }
                                                else {
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
