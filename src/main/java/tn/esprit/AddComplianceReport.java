package tn.esprit;

import models.Rapport;
import services.RapportService;

public class AddComplianceReport {
    public static void main(String[] args) {
        RapportService rs = new RapportService();
        Rapport complianceRapport = new Rapport(
            "Regulatory Compliance Audit", 
            "Annual review of regulatory standards and internal policy adherence.", 
            "2024-04-13", 
            "Compliance"
        );
        try {
            rs.add(complianceRapport);
            System.out.println("Compliance report added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
