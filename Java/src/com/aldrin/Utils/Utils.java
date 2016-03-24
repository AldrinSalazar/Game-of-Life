package com.aldrin.Utils;

/**
 * Agrupa funciones de ayuda de forma static para accederlas en todo el codigo.
 */
public class Utils {
    public static boolean debug = true;
    public static boolean info = true;
    public static boolean error = true;

    /**
     * Muestra en la salida standard el mensaje, como log de informacion.
     * @param s Mensaje a logear
     */
    public static void logi(String s){
        if(info)
            System.out.println("[INFO] - "+s);
    }

    /**
     * Muestra en la salida standard el mensaje, como log de error.
     * @param s Mensaje a logear
     */
    public static void loge(String s){
        if(error)
            System.out.println("[ERROR] - "+s);
    }

    /**
     * Muestra en la salida standard el mensaje, como log de debug.
     * @param s Mensaje a logear
     */
    public static void logd(String s){
        if(debug)
            System.out.println("[DEBUG] - "+s);
    }
}
