module com.example.mindjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.pdfbox;
    requires com.google.gson;
    requires jdk.httpserver;
    requires java.desktop;
    requires jakarta.mail;
    requires java.prefs;
    requires org.json;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.controlsfx.controls;
    requires javafx.web;
    requires javafx.swing;
    requires java.net.http;

    // Core app packages
    opens com.example.mindjavafx to javafx.fxml;
    opens com.example.mindjavafx.controller to javafx.fxml;
    opens com.example.mindjavafx.model to javafx.base;
    opens com.example.mindjavafx.apirest.dto to com.google.gson;

    exports com.example.mindjavafx;
    exports com.example.mindjavafx.controller;
    exports com.example.mindjavafx.model;
    exports com.example.mindjavafx.service;
    exports com.example.mindjavafx.util;
    exports com.example.mindjavafx.apirest;
    exports com.example.mindjavafx.apirest.dto;

    // Gestion Entreprise packages
    opens com.gestion.controller to javafx.fxml;
    opens com.gestion.entity to javafx.base;
    opens com.gestion.util to org.json;
    exports com.gestion.controller;
    exports com.gestion.entity;
    exports com.gestion.service;
    exports com.gestion.util;

    // Gestion Audit packages (controlleur, models, services)
    opens controlleur to javafx.fxml;
    opens models to javafx.base;
    exports controlleur;
    exports models;
    exports services;
    exports util;

    // tn.esprit packages
    opens tn.esprit to javafx.fxml;
    exports tn.esprit;

    // Apache POI for Rapports Excel Export
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.github.librepdf.openpdf;

    // Gestion Rapports packages
    opens com.audit.auditaifx.controller to javafx.fxml;
    opens com.audit.auditaifx.model to javafx.base;
    exports com.audit.auditaifx.controller;
    exports com.audit.auditaifx.model;
    exports com.audit.auditaifx.service;

    // Gestion Audit (Module Intégré)
    opens com.gestionaudit.controllers to javafx.fxml;
    opens com.gestionaudit.models to javafx.base;
    exports com.gestionaudit;
    exports com.gestionaudit.controllers;
    exports com.gestionaudit.models;
    exports com.gestionaudit.services;
    exports com.gestionaudit.utils;
}
