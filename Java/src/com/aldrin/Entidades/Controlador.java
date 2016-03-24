package com.aldrin.Entidades;

import com.aldrin.Utils.Utils;

import java.util.ArrayList;

/**
 * Su funcion es agrupar todas las entidades, llamar sus metodos de actualizacion y render, eliminarlas
 * obtenerlas y modificarlas.
 */

public class Controlador {
    private ArrayList<Entidad> entidades;

    public Controlador(){
        entidades = new ArrayList<>();
    }

    public void añadirEntidad(Entidad e){
        entidades.add(e);
        Utils.logd(String.format("&d entidades en controlador.",entidades.size()));
    }

    public void eliminarEntidad(Entidad e){
        e.alEliminar();
        entidades.remove(e);
        Utils.logd(String.format("&d entidades en controlador.",entidades.size()));
    }

    public Entidad obtenerPorID(int id){
        for(Entidad e:entidades){
            if(e.getId() == id)
                return e;
        }
        return null;
    }
}
