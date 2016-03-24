package com.aldrin.Ventana;

import com.aldrin.Estados.Estado;
import com.aldrin.Utils.Utils;

import javax.swing.JComponent;
import java.awt.*;

/**
 * Es la representacion en la ventana de los graficos del juego, se encarga de llevar los tiempos de
 * actualizacion y dibujado, tanto de si mismo como del estado actual.
 */
public class Juego extends JComponent implements Runnable{

    /**
     * Tamaño efectivo del Componene en la ventana
     */
    public static Dimension TAMAÑO;

    /**
     * Controla la ejecucion total, el dibujado y la logica de formas separadas
     */
    private boolean corriendo;
    private boolean renderizando;
    private boolean actualizando;

    /**
     * FPS alcanzados
     */
    private int fps;

    /**
     * Numero de actualizaciones logicas por segundo
     */
    private double ticksPorSegundo = 10;

    /**
     * Numero de cuadros por segundo, no son los FPS efectivos, es un multiplo del tiempo en ms
     * dado en cada ciclo para el descanso al CPU, si no el bucle principal
     * usa el 80% del CPU.
     */
    private double cuadrosPorSegundo = 20;

    /**
     * Tiempo que el CPU descansa en cada ciclo, para evitar usarlo al 80%
     */
    private int descanso = 50;

    /**
     * El estado actual que se muestra
     */
    private Estado estadoActual;

    /**
     * Hilo donde se lleva el ciclo principal
     */
    private Thread principal;

    public Juego(){
        corriendo = false;
        renderizando = true;
        actualizando = true;
        fps = 0;
    }

    /**
     * Lleva la actualizacion de la logica, se ejecuta tantas veces por segundo como sea
     * establecido en ticksPorSegundo. El tick se extiende a todos los estados y entidades
     * en ellos.
     */
    private void tick(){
        estadoActual.tick();
    }

    /**
     * Lleva la actualizacion grafica, se ejecuta tantas veces por segundo como sea posible.
     * Se extiende a todas las entidades y el estado actual.
     */
    private void render(Graphics2D g){
        /**
         * Hay que establecer la calidad de dibujado antes de empezar a usarlo, solamente habilitamos
         * el antialiasing para dibujado, no hay textos en esta ocacion.
         */
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);

        /**
         * Se difunde el dibujado al estado actual y a cada entidad en el
         */
        estadoActual.render(g);
    }

    /**
     * Detiene totalmente la ejecucion del hilo donde esta el bucle principal del juego.
     * Efectivamente terminando con la ejecucion del juego. (Por ahora)
     */
    private void detener(){
        try{
            //Unete !
            principal.join();
            corriendo = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Inicia el hilo principal del juego, asi como el primer estado.
     */
    public void iniciar(){
        if(estadoActual == null){
            Utils.loge("No se puede iniciar sin estado actual definido.");
            System.exit(1);
        }

        principal = new Thread(this, "Principal");
        principal.start();
        corriendo = true;
        Utils.logi("Juego iniciado.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render((Graphics2D)g);
    }

    /**
     * Inicia el bucle principal del juego, se actualiza la logica las veces por segundo
     * establecidas en ticksPorSegundo, y se actualizan los graficos las veces que sea
     * posible.
     */
    @Override
    public void run() {
        //Ultima actualizacion
        long ultimo = System.nanoTime();
        long ultimof = System.nanoTime();
        // frecuencia = 1 nanoSegundo / ticksPorSegundo
        double ns = 1000000000 / ticksPorSegundo;
        double nsf = 1000000000 / cuadrosPorSegundo;
        //Tiempo para contar los fps
        long timer = System.currentTimeMillis();
        //Cuadros dibujados
        int frames = 0;
        //Diferencia entre ultima actualizacion
        double delta = 0;
        double deltaf = 0;
        //Tiempo usado para renderizar, y actualizar
        double renderTime = 0;
        double tickTime = 0;
        //Informacion cada tantos segundos
        int tiempoInformacion = 5;

        //Bucle principal
        while(corriendo)
        {
            long ahora = System.nanoTime();
            delta += (ahora - ultimo) / ns;
            ultimo = ahora;

            //Si se acumula 1 o mas segundos
            while(delta >=1)
            {
                //Actualizar logica
                if(actualizando){
                    tickTime = System.nanoTime();
                    tick();
                    tickTime = System.nanoTime() - tickTime;
                }
                //1 segundo menos
                delta--;
            }

            long ahoraf = System.nanoTime();
            deltaf += (ahoraf - ultimof) / nsf;
            ultimof = ahoraf;

            while(deltaf >= 1){
                if(renderizando){
                    //actualizar graficos
                    renderTime = System.nanoTime();
                    repaint();
                    renderTime = System.nanoTime() - renderTime;
                    frames++;
                }
                deltaf--;
            }

            try{
                Thread.sleep(descanso);
            }catch (Exception e){}

            //Cada segundo se muestran los FPS logrados
            if(System.currentTimeMillis() - timer > tiempoInformacion * 1000)
            {
                timer += tiempoInformacion * 1000;
                fps = frames;
                frames = 0;
                Utils.logi(String.format("%d FPS, %.2f ms Render, %.2f ms Tick", fps, renderTime/1000000, tickTime/1000000));
            }
        }
        //Si no corre, se detiene
        detener();
    }

    /**
     * El estado actual es el que se esta actualizando y dibujando en el momento
     * solo puede haber un estado actual por instancia de Juego.
     * @param estadoActual El estado a ser actual.
     */
    public void setEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
        estadoActual.setJuego(this);
    }
}
