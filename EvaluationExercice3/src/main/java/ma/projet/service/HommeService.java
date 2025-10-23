package ma.projet.service;

import ma.projet.beans.Homme;
import ma.projet.beans.Femme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HommeService implements IDao<Homme> {

    @Override
    public boolean create(Homme homme) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(homme);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean update(Homme homme) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.update(homme);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean delete(Homme homme) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.delete(homme);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Homme findById(Long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Homme.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Homme> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Homme> query = session.createQuery("FROM Homme", Homme.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Femme> getEpousesBetweenDates(Long hommeId, Date dateDebut, Date dateFin) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT m.femme FROM Mariage m WHERE m.homme.id = :hommeId " +
                    "AND m.dateDebut BETWEEN :dateDebut AND :dateFin";
            Query<Femme> query = session.createQuery(hql, Femme.class);
            query.setParameter("hommeId", hommeId);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Long countMenWith4WivesBetweenDates(Date dateDebut, Date dateFin) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(Mariage.class);
            criteria.add(Restrictions.between("dateDebut", dateDebut, dateFin));
            criteria.add(Restrictions.isNull("dateFin"));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("homme.id"))
                    .add(Projections.count("homme.id")));

            List<Object[]> results = criteria.list();
            long count = 0;
            for (Object[] result : results) {
                Long marriageCount = (Long) result[1];
                if (marriageCount == 4) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void displayMariageDetails(Long hommeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Homme homme = session.get(Homme.class, hommeId);
            if (homme == null) {
                System.out.println("Homme non trouvé avec l'ID: " + hommeId);
                return;
            }

            System.out.println("Nom : " + homme.getNom().toUpperCase() + " " + homme.getPrenom().toUpperCase());

            String hql = "FROM Mariage m WHERE m.homme.id = :hommeId ORDER BY m.dateDebut";
            Query<Mariage> query = session.createQuery(hql, Mariage.class);
            query.setParameter("hommeId", hommeId);
            List<Mariage> mariages = query.list();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            System.out.println("Mariages En Cours :");
            int activeCount = 1;
            for (Mariage mariage : mariages) {
                if (mariage.isActive()) {
                    System.out.printf("%d. Femme : %s %s   Date Début : %s    Nbr Enfants : %d%n",
                            activeCount++,
                            mariage.getFemme().getPrenom().toUpperCase(),
                            mariage.getFemme().getNom().toUpperCase(),
                            sdf.format(mariage.getDateDebut()),
                            mariage.getNbrEnfants());
                }
            }

            System.out.println();
            System.out.println("Mariages échoués :");
            int failedCount = 1;
            for (Mariage mariage : mariages) {
                if (!mariage.isActive()) {
                    System.out.printf("%d. Femme : %s %s  Date Début : %s    %n",
                            failedCount++,
                            mariage.getFemme().getPrenom().toUpperCase(),
                            mariage.getFemme().getNom().toUpperCase(),
                            sdf.format(mariage.getDateDebut()));
                    System.out.printf("Date Fin : %s    Nbr Enfants : %d%n",
                            sdf.format(mariage.getDateFin()),
                            mariage.getNbrEnfants());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}