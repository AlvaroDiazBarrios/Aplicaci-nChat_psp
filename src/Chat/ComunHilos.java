/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import java.net.*;

/**
 *
 * @author gandalfvaro
 */
public class ComunHilos {

    private int conexiones; //total de conexiones en el array
    private int actuales; //total de conexiones actuales
    private int MAXIMO; //maximo de conexiones permitidas
    private Socket[] clientes = new Socket[this.MAXIMO]; //array de clientes conectados
    private String mensajes; //mensajes que se han enviado

    public ComunHilos(int maximo, int actuales, int conexiones, Socket[] clientes) {
        this.MAXIMO = maximo;
        this.actuales = actuales;
        this.conexiones = conexiones;
        this.clientes = clientes;
        this.mensajes = ""; //al inicio estan vacios
    }

    public int getMAXIMO() {
        return this.MAXIMO;
    }

    public void setMAXIMO(int maximo) {
        this.MAXIMO = maximo;
    }

    public int getCONEXIONES() {
        return this.conexiones;
    }
    
    public synchronized void setCONEXIONES(int conexiones) {
        this.conexiones = conexiones;
    }

    public String getMensajes() {
        return this.mensajes;
    }

    public synchronized void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }

    public int getACTUALES() {
        return this.actuales;
    }

    public synchronized void setACTUALES(int actuales) {
        this.actuales = actuales;
    }

    public synchronized void addCliente(Socket cliente, int i) {
        this.clientes[i] = cliente;
    }

    public Socket getCliente(int i) {
        return this.clientes[i];
    }
}
