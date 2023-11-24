package com.mycompany.libreriajpa;

import Entidades.Autor;
import Entidades.Editorial;
import Entidades.Libro;
import Servicios.AutorServicio;
import Servicios.EditorialServicio;
import Servicios.LibroServicio;

public class LibreriaJPA {

    public static void main(String[] args) throws Exception {
        AutorServicio as = new AutorServicio();
        EditorialServicio es = new EditorialServicio();
        LibroServicio ls = new LibroServicio();

        Libro libro = ls.buscarLibroPorID(9);
        ls.devolucionLibro(libro);
    }
}
