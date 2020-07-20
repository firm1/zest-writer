open module zestwriter {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires wellbehavedfx;
    requires de.jensd.fx.glyphs.commons;
    requires org.apache.commons.io;
    requires zt.zip;
    requires java.desktop;
    requires jdk.jsobject;
    requires org.jsoup;
    requires aliasi.lingpipe;
    requires org.controlsfx.controls;
    requires org.apache.commons.text;
    requires org.graalvm.sdk;
    requires org.graalvm.js;
    requires slf4j.api;
    requires java.net.http;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    exports com.zds.zw;
}