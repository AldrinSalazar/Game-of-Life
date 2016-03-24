package com.aldrin.Estados;

import com.aldrin.Entidades.Controlador;
import com.aldrin.Ventana.Juego;

import java.awt.Graphics2D;

/**
 * Repesenta un estado del juego, controla la logica de cada entidad asi como sus graficas.
 * Posee una instancia de controlador para ayudarse en estas funciones.
 */
public abstract class Estado {
    protected Controlador controlador;
    public String nombre;
    protected Juego juego;

    public Estado(){
        controlador = new Controlador();
        nombre = "Estado sin nombre.";
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public abstract void tick();
    public abstract void render(Graphics2D g);
}
