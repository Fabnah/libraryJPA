package Servicios;

import Entidades.Autor;
import Entidades.Editorial;
import Entidades.Libro;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class LibroServicio {

    AutorServicio as = new AutorServicio();
    EditorialServicio es = new EditorialServicio();

    public void agregarLibroNuevo(boolean alta, int anio, int ejemplares, String titulo, int autorID, int editorialID) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Libro libro = new Libro();
            Autor autor = new Autor();
            Editorial editorial = new Editorial();

            libro.setAlta(alta);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(0);
            libro.setEjemplaresRestantes(ejemplares);
            libro.setTitulo(titulo);

            try {
                autor = as.buscarAutorPorID(autorID);
            } catch (NoResultException e) {
                System.out.println("El autor no se encontro");
            }

            libro.setAutor(autor);

            try {
                editorial = es.buscarEditorialPorID(editorialID);
            } catch (NoResultException e) {
                System.out.println("La editorial no se encontro");
            }

            libro.setEditorial(editorial);

            em.persist(libro);
            em.getTransaction().commit();

        } catch (Exception e) {
        } finally {
            em.close();
            emf.close();
        }

    }

    public Libro buscarLibroPorID(int id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();
        Libro libro = new Libro();
        try {
            em.getTransaction().begin();
            libro = em.find(Libro.class, id);
            em.getTransaction().commit();

        } catch (Exception e) {
        } finally {
            em.close();
            emf.close();
        }
        return libro;
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            String jpql = "select e from Libro e where e.titulo = :titulo";
            TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
            query.setParameter("titulo", titulo);

            Libro libro = query.getSingleResult();

            em.getTransaction().commit();

            return libro;

        } catch (NonUniqueResultException e) {
            throw e;
        } finally {
            em.close();
            emf.close();

        }

    }

    public void eliminarLibroDeLaBase(Libro libro) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            int libroID = libro.getId();
            Libro libroDB = em.find(Libro.class, libroID);

            em.remove(libroDB);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.out.println("No se encontro el libro");
            }
        } finally {
            em.close();
            emf.close();
        }
    }

    public void modificarEstadoDelLibro(Libro libro) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            String jpql = "update Libro e set e.alta = :alta where e.id = :id";
            TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
            query.setParameter("alta", !libro.isAlta());
            query.setParameter("id", libro.getId());

            int filasActualizadas = query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
        } finally {
            em.close();
            emf.close();
        }
    }

    public void prestamoLibro(Libro libro) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Libro libroDB = em.find(Libro.class, libro.getId());
            if (libroDB.getEjemplares() > 0 && libroDB.isAlta()) {
                sumarPrestados(em, libroDB);
                restarRestantes(em, libroDB);

                String jpql = "update Libro e set e.ejemplares = :ejemplares where e.id = :id";
                TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
                query.setParameter("ejemplares", libro.getEjemplares() - 1);
                query.setParameter("id", libro.getId());
                int filasActualizadas = query.executeUpdate();
                em.getTransaction().commit();
            }

        } catch (Exception e) {
            if (libro.getEjemplares() == 0) {
                System.out.println("No hay libros para prestar");
            }
            if (!libro.isAlta()) {
                System.out.println("El título no se encuentra disponible");
            }
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    public void sumarPrestados(EntityManager em, Libro libro) {
        String jpql = "update Libro e set e.ejemplaresPrestados = :ejemplaresPrestados where e.id = :id";
        TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
        query.setParameter("ejemplaresPrestados", libro.getEjemplaresPrestados() + 1);
        query.setParameter("id", libro.getId());
        int filasActualizadas = query.executeUpdate();
    }

    public void restarRestantes(EntityManager em, Libro libro) {
        String jpql = "update Libro e set e.ejemplaresRestantes = :ejemplaresRestantes where e.id = :id";
        TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
        query.setParameter("ejemplaresRestantes", libro.getEjemplaresRestantes() - 1);
        query.setParameter("id", libro.getId());
        int filasActualizadas = query.executeUpdate();
    }

    public void devolucionLibro(Libro libro) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libreriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Libro libroDB = em.find(Libro.class, libro.getId());
            if (libroDB.getEjemplares() > 0 && libroDB.isAlta()) {
                restarPrestados(em, libroDB);
                sumarRestantes(em, libroDB);

                String jpql = "update Libro e set e.ejemplares = :ejemplares where e.id = :id";
                TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
                query.setParameter("ejemplares", libro.getEjemplares() + 1);
                query.setParameter("id", libro.getId());
                int filasActualizadas = query.executeUpdate();
                em.getTransaction().commit();
            }
        } catch (Exception e) {
        }finally {
            em.close();
            emf.close();
        }
    }

    public void restarPrestados(EntityManager em, Libro libro) {
        String jpql = "update Libro e set e.ejemplaresPrestados = :ejemplaresPrestados where e.id = :id";
        TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
        query.setParameter("ejemplaresPrestados", libro.getEjemplaresPrestados() - 1);
        query.setParameter("id", libro.getId());
        int filasActualizadas = query.executeUpdate();
    }

    public void sumarRestantes(EntityManager em, Libro libro) {
        String jpql = "update Libro e set e.ejemplaresRestantes = :ejemplaresRestantes where e.id = :id";
        TypedQuery<Libro> query = em.createQuery(jpql, Libro.class);
        query.setParameter("ejemplaresRestantes", libro.getEjemplaresRestantes() + 1);
        query.setParameter("id", libro.getId());
        int filasActualizadas = query.executeUpdate();
    }

}
