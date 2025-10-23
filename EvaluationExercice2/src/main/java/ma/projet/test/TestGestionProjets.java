package ma.projet.test;

import ma.projet.classes.*;
import ma.projet.service.*;
import ma.projet.util.HibernateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TestGestionProjets {

    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            ProjetService projetService = new ProjetService();
            TacheService tacheService = new TacheService();
            EmployeService employeService = new EmployeService();
            EmployeTacheService employeTacheService = new EmployeTacheService();

            System.out.println("=== Test de création des données ===");

            Employe employe1 = new Employe("Dupont", "Jean", "0123456789");
            Employe employe2 = new Employe("Martin", "Marie", "0987654321");
            employeService.create(employe1);
            employeService.create(employe2);
            System.out.println("Employés créés avec succès");

            Projet projet1 = new Projet("Gestion de stock", sdf.parse("14/01/2013"), sdf.parse("14/06/2013"));
            projetService.create(projet1);
            System.out.println("Projet créé avec succès");

            Tache tache1 = new Tache("Analyse", sdf.parse("01/02/2013"), sdf.parse("28/02/2013"), 1500.0, projet1);
            Tache tache2 = new Tache("Conception", sdf.parse("01/03/2013"), sdf.parse("31/03/2013"), 2000.0, projet1);
            Tache tache3 = new Tache("Développement", sdf.parse("01/04/2013"), sdf.parse("30/04/2013"), 800.0, projet1);

            tacheService.create(tache1);
            tacheService.create(tache2);
            tacheService.create(tache3);
            System.out.println("Tâches créées avec succès");

            EmployeTache et1 = new EmployeTache(employe1, tache1, sdf.parse("10/02/2013"), sdf.parse("20/02/2013"));
            EmployeTache et2 = new EmployeTache(employe1, tache2, sdf.parse("10/03/2013"), sdf.parse("15/03/2013"));
            EmployeTache et3 = new EmployeTache(employe2, tache3, sdf.parse("10/04/2013"), sdf.parse("25/04/2013"));

            employeTacheService.create(et1);
            employeTacheService.create(et2);
            employeTacheService.create(et3);
            System.out.println("Associations employé-tâche créées avec succès");

            System.out.println("\n=== Test d'affichage des tâches réalisées pour un projet ===");
            projetService.afficherTachesRealisees(projet1.getId());

            System.out.println("\n=== Test des tâches avec prix supérieur à 1000 DH ===");
            List<Tache> tachesCheres = tacheService.getTachesAvecPrixSuperieurA1000();
            if (tachesCheres != null) {
                System.out.println("Tâches avec prix > 1000 DH:");
                for (Tache t : tachesCheres) {
                    System.out.println("- " + t.getNom() + " : " + t.getPrix() + " DH");
                }
            }

            System.out.println("\n=== Test des tâches réalisées entre deux dates ===");
            Date dateDebut = sdf.parse("01/02/2013");
            Date dateFin = sdf.parse("31/03/2013");
            List<Tache> tachesEntreDates = tacheService.getTachesRealiseesEntreDates(dateDebut, dateFin);
            if (tachesEntreDates != null) {
                System.out.println("Tâches réalisées entre " + sdf.format(dateDebut) + " et " + sdf.format(dateFin) + ":");
                for (Tache t : tachesEntreDates) {
                    System.out.println("- " + t.getNom());
                }
            }

            System.out.println("\n=== Test des tâches réalisées par un employé ===");
            List<Tache> tachesEmploye1 = employeService.getTachesRealiseesByEmploye(employe1.getId());
            if (tachesEmploye1 != null) {
                System.out.println("Tâches réalisées par " + employe1.getPrenom() + " " + employe1.getNom() + ":");
                for (Tache t : tachesEmploye1) {
                    System.out.println("- " + t.getNom());
                }
            }

            System.out.println("\n=== Test des projets gérés par un employé ===");
            List<Projet> projetsEmploye1 = employeService.getProjetsGeresByEmploye(employe1.getId());
            if (projetsEmploye1 != null) {
                System.out.println("Projets gérés par " + employe1.getPrenom() + " " + employe1.getNom() + ":");
                for (Projet p : projetsEmploye1) {
                    System.out.println("- " + p.getNom());
                }
            }

        } catch (ParseException e) {
            System.err.println("Erreur de format de date: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}