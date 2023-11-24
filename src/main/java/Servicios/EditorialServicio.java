package Servicios;

import Entidades.Editorial;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class EditorialServicio {

    public void agregarEditorial(String nombre, boolean alta) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();
        try {
            Editorial editorial = new Editorial(nombre, alta);

            if (buscarEditorialPorNombre(nombre) != null) {
                System.out.println("Ya existe una editorial con ese nombre");
            } else {
                em.getTransaction().begin();

                em.persist(editorial);
                em.getTransaction().commit();
            }

        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    public Editorial buscarEditorialPorID(int id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();
            Editorial editorial = em.find(Editorial.class, id);
            em.getTransaction().commit();

            return editorial;
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
            emf.close();

        }
    }

    public Editorial buscarEditorialPorNombre(String nombre) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            String jpql = "select e from Editorial e where e.nombre = :nombre";

            TypedQuery<Editorial> query = em.createQuery(jpql, Editorial.class);
            query.setParameter("nombre", nombre);

            Editorial editorial = (Editorial) query.getSingleResult();

            em.getTransaction().commit();

            return editorial;
        } catch (NonUniqueResultException e) {
            throw e;
        } finally {
            em.close();
            emf.close();

        }
    }

    public void CambiarEstadoEditorial(Editorial editorial) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            int id = editorial.getId();
            String jpql = "update Editorial e set e.alta = :alta where e.id = :id";

            TypedQuery<Editorial> query = em.createQuery(jpql, Editorial.class);
            query.setParameter("id", id);
            query.setParameter("alta", !editorial.isAlta());

            int filasActualizadas = query.executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
            emf.close();

        }
    }

    public void eliminarEditorial(Editorial editorial) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            int id = editorial.getId();

            em.getTransaction().begin();
            Editorial edit = em.find(Editorial.class, id);
            if (edit != null) {
                em.remove(edit);
            } else {
                System.out.println("La editorial seleccionada no se encuentra en la base de datos");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
        }
    }

}
