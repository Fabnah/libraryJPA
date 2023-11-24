package Servicios;

import Entidades.Autor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AutorServicio {

    public void crearAutor(String nombre, boolean alta) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            Autor autor = new Autor();
            autor.setNombre(nombre);
            autor.setAlta(alta);

            em.getTransaction().begin();
            em.persist(autor);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }

    }

    public void eliminarAutor(Autor autor) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            int id = autor.getId();
            Autor autorEM = em.find(Autor.class, id);
            if (autorEM != null) {
                em.remove(autorEM);
            }
            em.getTransaction().commit();

        } catch (NullPointerException e) {
            throw new Exception("El autor indicado no se encuentra en la base de datos");
        } finally {
            em.close();
            emf.close();
        }
    }

    public Autor buscarAutorPorID(int id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();
        try {

            em.getTransaction().begin();
            Autor autor = em.find(Autor.class, id);
            em.getTransaction().commit();
            return autor;

        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

}
