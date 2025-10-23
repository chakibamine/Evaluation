package ma.projet.service;


import ma.projet.dao.IDao;
import ma.projet.classes.Produit;
import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommande;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProduitService implements IDao<Produit> {

    @Override
    public boolean create(Produit produit) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(produit);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean update(Produit produit) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(produit);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean delete(Produit produit) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(produit);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public Produit findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return (Produit) session.get(Produit.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Produit> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Produit");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public List<Produit> findByCategorie(Categorie categorie) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Produit p WHERE p.categorie = :categorie");
            query.setParameter("categorie", categorie);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public List<Produit> findProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT DISTINCT lc.produit FROM LigneCommande lc " +
                    "WHERE lc.commande.dateCommande BETWEEN :dateDebut AND :dateFin";
            Query query = session.createQuery(hql);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public List<LigneCommande> findProduitsParCommande(Commande commande) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM LigneCommande lc WHERE lc.commande = :commande");
            query.setParameter("commande", commande);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public void afficherProduitsCommande(int commandeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Commande commande = (Commande) session.get(Commande.class, commandeId);
            if (commande != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                System.out.println("Commande : " + commande.getId() + "     Date : " + sdf.format(commande.getDateCommande()));
                System.out.println("Liste des produits :");
                System.out.println("Référence   Prix    Quantité");

                List<LigneCommande> lignes = findProduitsParCommande(commande);
                for (LigneCommande ligne : lignes) {
                    System.out.println(ligne.getProduit().getReference() + "        " +
                            ligne.getPrixUnitaire() + " DH  " + ligne.getQuantite());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public List<Produit> findProduitsPrixSuperieur(double prix) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.getNamedQuery("Produit.findByPrixSuperieur");
            query.setParameter("prix", prix);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }
}