package com.aldrin;


import com.aldrin.Estados.Estado;
import com.aldrin.Utils.Utils;
import com.aldrin.Ventana.Juego;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

/**
 * Conways Game of Life, con reglas standard.
 *
 * Aldrin Salazar
 */
public class GameOfLife extends Estado implements MouseListener {

    /**
     * Tamaño de la celda
     */
    public static int tamañoCelda = 15;

    /**
     * Offsets de separacion entre bordes de la
     * ventana, en ambas coordenadas
     */
    public static int offsetX = 2;
    public static int offsetY = 2;

    /**
     * Separacion entre celdas
     */
    public static int separacion = 1;

    /**
     * Numero de celdas en el tablero
     */
    private int tamañoTablero;

    /**
     * Generacion actual simulada
     */
    private int ciclos;

    /**
     * Tablero, cada celula esta o viva o muerta
     */
    private boolean[][] tablero;

    /**
     * Control de ejecucion de la simulacion
     */
    private boolean corriendo;

    /**
     * El color tanto de las celulas como de su cuerpo "muerto"
     */
    private Color color;

    /**
     * Da valor al tablero en 0, establece el tamaño y los ciclos.
     * @param tamaño Numero de celdas en el tablero, est es un cuadrado
     *               NxN.
     */
    public GameOfLife(int tamaño){
        this.ciclos = 0;
        this.tamañoTablero = tamaño;
        this.corriendo = false;
        this.tablero = new boolean[tamañoTablero][tamañoTablero];
        this.nombre = "GameOfLife";
        this.color = Color.BLUE;

        Utils.logi(String.format("Estado %s creado.", nombre));
    }

    /**
     * Dada una posicion en el tablero, se calcula el numero de celulas adyacentes.
     * @param x Posicion en X
     * @param y Posicion en Y
     * @return Numero de vecinos de la celula en P(x,y)
     */
    private int alrededor(int x, int y){
        int resultado = 0;

        resultado += valorEn(x-1, y-1)?1:0; //Diagonal Sup. Izq.
        resultado += valorEn(x, y-1)?1:0;   //Superior
        resultado += valorEn(x+1, y-1)?1:0; //Diagonal Suo. Der.

        resultado += valorEn(x-1, y)?1:0;   //Izquierda
        resultado += valorEn(x+1, y)?1:0;   //Derecha

        resultado += valorEn(x, y+1)?1:0;    //Inferior
        resultado += valorEn(x+1, y+1)?1:0;  //Diagonal Inf. Der.
        resultado += valorEn(x-1, y+1)?1:0;  //Diagonal Inf. Izq.

        return resultado;
    }

    /**
     * Accede al valor en una posicion del tablero, de esta forma se omiten
     * las posiciones fueras del espacio de la matriz.
     * @param x Posicion en X
     * @param y Posicion en Y
     * @return El valor en esa posicion (vivo o muerto).
     */
    private boolean valorEn(int x, int y){
        try{
            return tablero[x][y];
        }catch (ArrayIndexOutOfBoundsException e){

        }
        return false;
    }

    /**
     * Dado el numero de vecinos de una celula, y si esta se encuentra viva o muerta
     * se interpretan las reglas y se decide si vive o muere en la siguiente generacion.
     * @param vecinos Numero de vecinos de la celula
     * @param vivo Si la celula evaluada esta viva o muerta
     * @return Vive o muere en la siguiente generacion
     */
    private boolean reglas(int vecinos, boolean vivo){
        /**
         * !vivo !vivo !!!!
         */
        if(!vivo){
            return vecinos==3;
        }else{
            return !(vecinos>3 || vecinos<2);
        }
    }

    /**
     * Un ciclo de simulacion completo, se encarga de iterar sobre cada celula y aplicar
     * las reglas del juego, llevar el numero de generaciones y cambiar el tablero actual
     * por el siguiente simulado.
     */
    private void GoL(){
        boolean nuevo[][] = new boolean[tamañoTablero][tamañoTablero];

        for(int i = 0; i < tamañoTablero; i++){
            for(int j = 0; j < tamañoTablero; j++){
                int alrededor = alrededor(i, j);
                nuevo[i][j] = reglas(alrededor, tablero[i][j]);
            }
        }

        tablero = nuevo;
        ciclos++;
    }

    /**
     * Genera un estado de tablero aleatorio.
     */
    private void aleatorio(){
        Random r = new Random();

        for(int i = 0; i<tamañoTablero; i++){
            for(int j = 0; j<tamañoTablero; j++){
                tablero[i][j] = r.nextBoolean();
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        /**
         * Shift+Click Limpia el tablero
         */
        if(e.isShiftDown() && !corriendo){
            tablero = new boolean[tamañoTablero][tamañoTablero];
            return;
        }

        /**
         * Alt+Click Inicia la simulacion
         */
        if(e.isAltDown()){
            corriendo = !corriendo;
            ciclos = 0;
            return;
        }

        /**
         * Ctrl+Click Crea un tablero aleatorio
         */
        if(e.isControlDown() && !corriendo){
            aleatorio();
            return;
        }

        /**
         * Click derecho o izquierdo cambia el estado de una celula
         * (Solo con la simulacion pausada)
         */
        if(!corriendo){
            /**
             * Si se tiene un cuadrado de tamaño NxN, dividido K numero de celdas, y se quiere saber para cada
             * punto P(x,y) la respectiva celda S(i,j), sbaiendo que 0 <= x,y < N y 0 <= i,j < K, matemagicamente
             * obtenemos la celda con la formula:
             *
             *      i = xK/N | j = yK/N
             *
             * De esta forma no es necesario iterar sobre cada celda para saber en cual esta el mouse
             */
            float x = e.getX()*tamañoTablero / Juego.TAMAÑO.width;
            float y = e.getY()*tamañoTablero / Juego.TAMAÑO.height;

            /**
             * vivo? !vivo
             */
            tablero[(int)x][(int)y] = !tablero[(int)x][(int)y];

            Utils.logd(String.format("x:%d y:%d", (int)x, (int)y));
        }
    }

    /**
     * Punto de entrada del reloj principal que lleva el ciclo del juego, controla el numero
     * de generaciones simuladas por segundo.
     * @see Juego
     */
    @Override
    public void tick() {
        JFrame j = (JFrame) SwingUtilities.getRoot(juego);

        if(corriendo){
            j.setTitle(String.format("Game of Life - Corriendo | Generacion #%d",ciclos));
            GoL();
        }else {
            j.setTitle(String.format("Game of Life - Pausa | Generacion #%d",ciclos));
        }
    }

    /**
     * Punto de entrada del reloj principal al momento de dibujar la representacion de la simulacion.
     * @param g Donde se dibujara
     */
    @Override
    public void render(Graphics2D g) {
        /**
         * Color de fondo
         */
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, Juego.TAMAÑO.width, Juego.TAMAÑO.height);

        g.setColor(this.color);
        for(int i = 0; i<tamañoTablero; i++){
            for(int j = 0; j<tamañoTablero; j++){
                /**
                 * Si esta viva es un cuadro relleno, de lo contrario es un cuadrado vacio
                 */
                if(tablero[i][j])
                    g.fillRect((i*(separacion+tamañoCelda))+offsetX, (j*(separacion+tamañoCelda))+offsetY, tamañoCelda-1, tamañoCelda-1);
                else
                    g.drawRect((i*(separacion+tamañoCelda))+offsetX, (j*(separacion+tamañoCelda))+offsetY, tamañoCelda-1, tamañoCelda-1);
            }
        }

    }

    //** El sotano, donde las interfaces son complacidas con metodos vacios >:) **
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
