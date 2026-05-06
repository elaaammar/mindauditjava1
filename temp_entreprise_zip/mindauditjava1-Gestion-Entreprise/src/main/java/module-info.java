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
}
