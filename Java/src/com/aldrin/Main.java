package com.aldrin;

import com.aldrin.Utils.Utils;
import com.aldrin.Ventana.Juego;
import com.aldrin.Ventana.Ventana;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        if(args.length < 1){
            Utils.loge("Uso: GoL.jar {numero de celdas}");
            System.exit(-1);
        }

        /**
         * Numero de celdas debe ser pasado como argumento
         */
        int numeroCeldas = Integer.parseInt(args[0]);

        /**
         * Separacion de los bordes de la pantalla, el JComponent toma el tamaño desde la misma barra
         * superior donde se encuentra el titulo, y los bordes de la ventana.
         */
        int offsetX = 7;
        int offsetY = 30;

        /**
         * Calculo del ancho y alto necesario de la ventana para acomodar el numero de celulas requerido.
         */
        int ancho = (GameOfLife.tamañoCelda*numeroCeldas) + (GameOfLife.separacion * numeroCeldas+1)
                + GameOfLife.offsetX + offsetX;

        int alto = (GameOfLife.tamañoCelda*numeroCeldas) + (GameOfLife.separacion * numeroCeldas+1)
                + GameOfLife.offsetY + offsetY;

        /**
         * Instanciacion del estado con el numero de celulas requerido
         */
        GameOfLife gameOfLife = new GameOfLife(numeroCeldas);
        Juego juego = new Juego();
        juego.setEstadoActual(gameOfLife);
        juego.addMouseListener(gameOfLife);

        /**
         * Creacion de la ventana
         */
        Ventana ventana = new Ventana("Game of life :-), @SAldrin", ancho, alto, juego);

        /**
         * Las instrucciones se muestran luego de crear la ventana
         */
        JOptionPane.showMessageDialog(null, "Click: Colocar celula\n" +
                "Ctrl+Click: Celulas aleatorias\n" +
                "Alt+Click: Iniciar / Detener simulacion\n" +
                "Shift+Click: Limpiar tablero",
                "Como usar", JOptionPane.INFORMATION_MESSAGE);

        /**
         * Game of life, comes to life
         */
        juego.iniciar();
    }
}
