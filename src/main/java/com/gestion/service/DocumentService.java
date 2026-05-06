package com.gestion.service;

import com.gestion.entity.Document;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentService {
    public List<Document> findByEntrepriseId(int entrepriseId) throws SQLException {
        return new ArrayList<>(); // Stub
    }
    public int getComplianceScore(int entrepriseId) throws SQLException {
        return 0; // Stub
    }
}
