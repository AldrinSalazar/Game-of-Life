package com.aldrin.Entidades;


import com.aldrin.Utils.Utils;

import java.awt.*;

public abstract class Entidad {

    /**
     * El numero total de entidades que han existido, usado para crear el ID unico.
     */
    private static int numeroEntidades = 0;

    /**
     * HitBox, un rectangulo que indica la posicion logica de una entidad, usado
     * para el calculo de colisiones.
     */
    private Rectangle hitBox;

    /**
     * EL tamaño de la representacion grafica, si es que existe.
     */
    private Rectangle tamañoGrafico;

    /**
     * Controla la profundidad de dibujado, para controlar las entidades que se dibujan
     * unas arribas de otras.
     */
    private int capa;

    /**
     * ID de identificacion unico.
     */
    private int id;

    /**
     * Nombre, si fuera necesario.
     */
    private String nombre;

    /**
     * Posicion de la representacion grafica.
     */
    protected int x, y;

    /**
     * Velocidad de la representacion grafica.
     */
    protected int vX, vY;

    public Entidad(){
        this(0, 0);
    }

    /**
     * Para crearla en la posicion indicada.
     * @param x posicion en X
     * @param y posicion en Y
     */
    public Entidad(int x, int y){
        this.x = x;
        this.y = y;

        vX = 0;
        vY = 0;

        hitBox = new Rectangle(0,0);
        tamañoGrafico = new Rectangle(10,10);

        capa = 0;

        numeroEntidades++;
        id = numeroEntidades;

        nombre = "Entidad "+id;

        Utils.logi(String.format("Entidad %s, ID:%d creada.", nombre, id));
    }

    /**
     * Evalua si se colisiona con el hitBox de otra entidad
     * @param e Entidad a evaluar
     * @return Si existe o no colision
     */
    public boolean colision(Entidad e){
        Rectangle b = e.getHitBox();
        return this.hitBox.intersects(b);
    }

    /**
     * Muestra la hitBox, con fines de debug.
     * @param g Grafico donde se dibujara.
     */
    protected void dibujarHitBox(Graphics2D g){
        g.setColor(Color.MAGENTA);
        g.drawRect(x, y, hitBox.width, hitBox.height);
    }

    /**
     * Muestra los bordes del grafico, con fines de debug.
     * @param g Grafico donde se dibujara.
     */
    protected void dibujarBordes(Graphics2D g){
        g.setColor(Color.CYAN);
        g.drawRect(x, y, tamañoGrafico.width, tamañoGrafico.height);
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle hitBox) {
        this.hitBox = hitBox;
    }

    public Rectangle getTamañoGrafico() {
        return tamañoGrafico;
    }

    public void setTamañoGrafico(Rectangle tamañoGrafico) {
        this.tamañoGrafico = tamañoGrafico;
    }

    /**
     * Obtiene el ID unico de la entidad.
     * @return ID unico.
     */
    public int getId() {
        return id;
    }

    /**
     * Callback llamado en una entidad antes de eliminarse de la lista de entidades de
     * un controlador.
     */
    public void alEliminar(){
        Utils.logi(String.format("Entidad %s ID:%d, eliminada.",nombre, id));
    }

    /**
     * @see com.aldrin.Ventana.Juego
     */
    public abstract void tick();

    /**
     * @see com.aldrin.Ventana.Juego
     * @param g Grafico de la instancia visible donde se dibuja cada elemento.
     */
    public abstract void render(Graphics2D g);
}
