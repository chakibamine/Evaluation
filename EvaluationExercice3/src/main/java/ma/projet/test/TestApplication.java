package ma.projet.test;

import ma.projet.beans.Homme;
import ma.projet.beans.Femme;
import ma.projet.beans.Mariage;
import ma.projet.service.HommeService;
import ma.projet.service.FemmeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestApplication {

    public static void main(String[] args) {
        try {
            HommeService hommeService = new HommeService();
            FemmeService femmeService = new FemmeService();
            MariageService mariageService = new MariageService();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            System.out.println("=== CRÉATION DES DONNÉES DE TEST ===");

            // Créer 10 femmes
            Femme[] femmes = new Femme[10];
            femmes[0] = new Femme("RAMI", "Salima", "0661234567", "Casablanca", createDate(1970, 5, 15));
            femmes[1] = new Femme("ALI", "Amal", "0662345678", "Rabat", createDate(1975, 8, 20));
            femmes[2] = new Femme("ALAOUI", "Wafa", "0663456789", "Fès", createDate(1980, 12, 10));
            femmes[3] = new Femme("ALAMI", "Karima", "0664567890", "Marrakech", createDate(1965, 3, 25));
            femmes[4] = new Femme("BENNANI", "Fatima", "0665678901", "Tanger", createDate(1982, 7, 8));
            femmes[5] = new Femme("CHRAIBI", "Aicha", "0666789012", "Agadir", createDate(1978, 11, 30));
            femmes[6] = new Femme("DRISSI", "Nadia", "0667890123", "Meknès", createDate(1985, 4, 18));
            femmes[7] = new Femme("EL FASSI", "Khadija", "0668901234", "Oujda", createDate(1972, 9, 5));
            femmes[8] = new Femme("GHALI", "Zineb", "0669012345", "Tétouan", createDate(1988, 1, 12));
            femmes[9] = new Femme("HAJJI", "Samira", "0660123456", "Safi", createDate(1990, 6, 22));

            for (Femme femme : femmes) {
                femmeService.create(femme);
            }

            // Créer 5 hommes
            Homme[] hommes = new Homme[5];
            hommes[0] = new Homme("SAFI", "Said", "0671234567", "Casablanca", createDate(1960, 12, 3));
            hommes[1] = new Homme("BENNANI", "Ahmed", "0672345678", "Rabat", createDate(1965, 4, 15));
            hommes[2] = new Homme("ALAMI", "Youssef", "0673456789", "Fès", createDate(1970, 8, 28));
            hommes[3] = new Homme("CHRAIBI", "Omar", "0674567890", "Marrakech", createDate(1975, 1, 10));
            hommes[4] = new Homme("DRISSI", "Khalid", "0675678901", "Tanger", createDate(1980, 5, 20));

            for (Homme homme : hommes) {
                hommeService.create(homme);
            }

            // Créer des mariages
            // SAFI Said - mariages multiples selon l'exemple
            mariageService.create(new Mariage(createDate(1989, 9, 3), createDate(1990, 9, 3), 0, hommes[0], femmes[3])); // Karima - échec
            mariageService.create(new Mariage(createDate(1990, 9, 3), null, 4, hommes[0], femmes[0])); // Salima - en cours
            mariageService.create(new Mariage(createDate(1995, 9, 3), null, 2, hommes[0], femmes[1])); // Amal - en cours
            mariageService.create(new Mariage(createDate(2000, 11, 4), null, 3, hommes[0], femmes[2])); // Wafa - en cours

            // Autres mariages pour tester les requêtes
            mariageService.create(new Mariage(createDate(1985, 6, 15), null, 2, hommes[1], femmes[4])); // Ahmed - Fatima
            mariageService.create(new Mariage(createDate(1990, 3, 20), createDate(1995, 3, 20), 1, hommes[1], femmes[5])); // Ahmed - Aicha (échec)
            mariageService.create(new Mariage(createDate(1996, 7, 10), null, 3, hommes[1], femmes[6])); // Ahmed - Nadia

            // Femme mariée plusieurs fois
            mariageService.create(new Mariage(createDate(1992, 4, 8), createDate(1997, 4, 8), 1, hommes[2], femmes[7])); // Youssef - Khadija
            mariageService.create(new Mariage(createDate(1998, 10, 12), null, 2, hommes[3], femmes[7])); // Omar - Khadija (2ème mariage)

            mariageService.create(new Mariage(createDate(2005, 2, 14), null, 1, hommes[4], femmes[8])); // Khalid - Zineb

            System.out.println("Données créées avec succès !");
            System.out.println();

            // 1. Afficher la liste des femmes
            System.out.println("=== 1. LISTE DES FEMMES ===");
            List<Femme> listeFemmes = femmeService.findAll();
            for (Femme f : listeFemmes) {
                System.out.println(f.getId() + " - " + f.getPrenom() + " " + f.getNom() +
                        " (née le " + sdf.format(f.getDateNaissance()) + ")");
            }
            System.out.println();

            // 2. Afficher la femme la plus âgée
            System.out.println("=== 2. FEMME LA PLUS ÂGÉE ===");
            Femme femmeAgee = femmeService.findOldestWoman();
            if (femmeAgee != null) {
                System.out.println("Femme la plus âgée : " + femmeAgee.getPrenom() + " " + femmeAgee.getNom() +
                        " (née le " + sdf.format(femmeAgee.getDateNaissance()) + ")");
            }
            System.out.println();

            // 3. Afficher les épouses d'un homme donné entre deux dates
            System.out.println("=== 3. ÉPOUSES DE SAID SAFI ENTRE 1990-2000 ===");
            Date dateDebut = createDate(1990, 1, 1);
            Date dateFin = createDate(2000, 12, 31);
            List<Femme> epouses = hommeService.getEpousesBetweenDates(hommes[0].getId(), dateDebut, dateFin);
            for (Femme epouse : epouses) {
                System.out.println("Épouse : " + epouse.getPrenom() + " " + epouse.getNom());
            }
            System.out.println();

            // 4. Afficher le nombre d'enfants d'une femme entre deux dates
            System.out.println("=== 4. NOMBRE D'ENFANTS DE SALIMA RAMI ENTRE 1990-2000 ===");
            Long nbrEnfants = femmeService.countChildrenBetweenDates(femmes[0].getId(), dateDebut, dateFin);
            System.out.println("Nombre d'enfants : " + nbrEnfants);
            System.out.println();

            // 5. Afficher les femmes mariées deux fois ou plus
            System.out.println("=== 5. FEMMES MARIÉES DEUX FOIS OU PLUS ===");
            List<Femme> femmesMultiMariees = femmeService.findWomenMarriedTwiceOrMore();
            for (Femme f : femmesMultiMariees) {
                System.out.println("Femme : " + f.getPrenom() + " " + f.getNom());
            }
            System.out.println();

            // 6. Afficher les hommes mariés à quatre femmes entre deux dates
            System.out.println("=== 6. HOMMES MARIÉS À 4 FEMMES ENTRE 1990-2010 ===");
            Date dateDebut2 = createDate(1990, 1, 1);
            Date dateFin2 = createDate(2010, 12, 31);
            Long nbrHommes4Femmes = hommeService.countMenWith4WivesBetweenDates(dateDebut2, dateFin2);
            System.out.println("Nombre d'hommes mariés à 4 femmes : " + nbrHommes4Femmes);
            System.out.println();

            // 7. Afficher les mariages d'un homme avec tous les détails
            System.out.println("=== 7. DÉTAILS DES MARIAGES DE SAID SAFI ===");
            hommeService.displayMariageDetails(hommes[0].getId());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }
}