module com.audit.auditaifx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.collections4;
    requires org.apache.commons.compress;
    requires org.apache.xmlbeans;
    requires org.apache.logging.log4j;
    requires com.github.librepdf.openpdf;
    requires com.google.gson;
    
    // New requirements from target
    requires org.apache.pdfbox;
    requires jakarta.mail;
    requires jdk.httpserver;
    requires java.prefs;

    // Ouvrir les packages à JavaFX pour la réflexion
    opens com.audit.auditaifx to javafx.fxml;
    opens com.audit.auditaifx.controller to javafx.fxml;
    opens com.audit.auditaifx.model to javafx.base, javafx.fxml;
    opens com.audit.auditaifx.service to javafx.fxml;

    // Ouvrir les nouveaux packages pour le module Gestion Entreprise
    opens com.gestion.controller to javafx.fxml;
    opens com.gestion.entity to javafx.base;
    opens com.gestion.service to javafx.fxml;
    opens com.gestion.util to javafx.fxml;
    
    // Exporter les packages
    exports com.audit.auditaifx;
    exports com.audit.auditaifx.controller;
    exports com.audit.auditaifx.model;
    exports com.audit.auditaifx.service;
    
    // Exporter les nouveaux packages
    exports com.gestion.controller;
    exports com.gestion.entity;
    exports com.gestion.service;
    exports com.gestion.util;
}
