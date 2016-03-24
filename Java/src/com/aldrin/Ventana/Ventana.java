package com.aldrin.Ventana;

import com.aldrin.Utils.Utils;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Clase ventana, encargada de encapsular el Jframe y sus componentes internos
 *
 */
public class Ventana {
    private JFrame ventana;
    private Juego juego;
    /**
     * @param titulo El titulo de la ventana
     * @param ancho El ancho de la ventana
     * @param alto El alto de la venta
     */
    public Ventana(String titulo, int ancho, int alto, Juego juego){
        this.ventana = new JFrame(titulo);
        this.juego = juego;

        this.ventana.setSize(new Dimension(ancho, alto));
        this.ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.ventana.setResizable(false);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.add(this.juego);
        this.ventana.setVisible(true);

        Utils.logi(String.format("Ventana creada, %dx%d", ventana.getSize().width, ventana.getSize().height));
        Juego.TAMAÑO = juego.getSize();
        Utils.logi(String.format("Tamaño del juego %dx%d", Juego.TAMAÑO.width, Juego.TAMAÑO.height));
    }

    public JFrame getVentana() {
        return ventana;
    }

    public Juego getJuego() {
        return juego;
    }
}
