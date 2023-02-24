package Chat;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author gandalfvaro
 */
public class ClienteChat extends JFrame implements ActionListener, Runnable {

    Socket cliente; //Socket del cliente
    DataInputStream in; //Flujo entrada del cliente
    DataOutputStream out; //Flujo salida del cliente

    String nombre; //Nombre de usuario

    static JTextField mensaje = new JTextField(); //Campo donde va a escribir el mensaje
    private JScrollPane ventanaMensajesChat; //Campo scrolleable donde se van a mostrar los mensajes
    static JTextArea ventanaMensajes; //Ventana donde se muestran los mensajes
    JButton botonEnviar = new JButton("Enviar"); //Boton Enviar
    JButton botonSalir = new JButton("Salir"); //Boton Salir
    boolean ejecutando = true; //Bandera que nos mantiene dentro del chat

    public ClienteChat(Socket cliente, String nombre) {
        super(" Conesctado al chat como: " + nombre); //Ponemos nombre a la ventana
        setLayout(null); //Eliminamos el leyaout.

        //textField mensajes
        this.mensaje.setBounds(10, 10, 400, 30); //definimos las propiedades del textField del mensaje
        add(this.mensaje); //añadimos el campo a la ventana

        //ScrollPane de la ventana mensajes
        this.ventanaMensajes = new JTextArea(); //creamos una nueva txtArea
        this.ventanaMensajesChat = new JScrollPane(this.ventanaMensajes); //la añadimos a la ventana donde aparecerán los mensajes
        this.ventanaMensajesChat.setBounds(10, 50, 400, 300); //definimos las propiedades de la ventana donde apareceran los mensajes
        add(this.ventanaMensajesChat); //la añadimos a la ventana
        this.ventanaMensajes.setEditable(false); //Hacemos que no se pueda editar la ventana donde se muestran los mensajes del chat

        //Boton enviar
        this.botonEnviar.setBounds(420, 10, 100, 30); //Definimos las propiedades del botón
        add(this.botonEnviar); //Lo añadimos a la ventana
        this.botonEnviar.addActionListener(this); //Añadimos el ActionListener del botón

        //Boton salir
        this.botonSalir.setBounds(420, 50, 100, 30); //Definimos las propiedades del botón
        add(this.botonSalir); //Lo añadimos a la ventana
        this.botonSalir.addActionListener(this); //Añadimos el ActionListener del botón

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //Hacemos que el al cerrar la ventana no haga nada

        this.cliente = cliente; //Asignamos el cliente
        this.nombre = nombre; //Asignamos su nick

        try {

            this.in = new DataInputStream(this.cliente.getInputStream()); //Abrimos el flujo de entrada de datos del cliente
            this.out = new DataOutputStream(this.cliente.getOutputStream()); //Abrimos el flujo de salida de datos del cliente
            String texto = " > " + nombre + ": Entra al chat"; //Mensaje de que el usuario entra al chat
            this.out.writeUTF(texto); //Enviamos el mensaje

        } catch (IOException e) { //si ocurre un error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "error de entrada-salida\n" + e.getMessage(), "Error: 2", JOptionPane.ERROR_MESSAGE);
            System.exit(2); //Cerramos la aplicación con codigo de error 2
        }
    }

    //Acciones para cuando pulsamos los botoles
    public void actionPerformed(ActionEvent e) {

        //Si pulsamos enviar
        if (e.getSource() == this.botonEnviar) {

            //Si el mensaje esta vacío no enviamos nada
            if (this.mensaje.getText().trim().length() == 0) {
                return;
            }

            //Formateamos el mensaje que se va a enviar
            String texto = " > " + this.nombre + ": " + this.mensaje.getText();

            try {

                this.mensaje.setText(""); //Reseteamos la casilla de mensajes
                this.out.writeUTF(texto); //Enviamos el mensaje formateado

            } catch (IOException error) {
                error.printStackTrace();
            }
        }

        //Si pulsamos salir
        if (e.getSource() == this.botonSalir) {

            String texto = " > " + this.nombre + ": Abandona el chat."; //Formateamos el mensaje de abandonar el chat

            try {

                this.out.writeUTF(texto); //Enviamos el mensaje de abandonando el chat
                this.out.writeUTF("*"); //Enviamos la señal para desconectarnos del servidor
                this.ejecutando = false; //Cambiamos el estado de la ejecución

            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        String texto;

        //Mientras se está ejecutando el cliente
        while (this.ejecutando) {
            try {

                texto = this.in.readUTF(); //leemos lo que nos llega del servidor
                this.ventanaMensajes.setText(texto); //lo mostramos por ventana
            } catch (IOException e) {
                //Si el servidor se desconeca inseperadamente 
                JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor\n" + e.getMessage(), "Error: 1", JOptionPane.ERROR_MESSAGE);
                System.exit(1); //Cerramos la aplicación con código de error 1
            }
        }

        try {
            this.cliente.close(); //Cerramos el cliente
            System.exit(0); //Cerramos la aplicación sin errores
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        int port = 60000; //puerto al que se va a conectar el cliente
        String host = "localhost"; //dirección a la que se va a conectar el cliente
        Socket socketCliente; //Socket del cliente
        String nick;

        do {
            nick = JOptionPane.showInputDialog("Introduce tu nick:"); //Mensaje "Inicio de Sesión" haciendo uso de los inputDialog por 
            
            //Controlamos que el nick no esté vacío
            if (nick.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "No ha introducido su nick", "¡Aviso!", JOptionPane.ERROR_MESSAGE);
            }
            
        } while (nick.trim().length() == 0);

        try {
            socketCliente = new Socket(host, port); //Creamos el socket del cliente 
            ClienteChat cliente = new ClienteChat(socketCliente, nick); //Creamos la ventana del cliente
            
            cliente.setBounds(700, 400, 540, 400); //Definimos las propiedades de la ventana del cliente
            cliente.setVisible(true); //Hacemos visible la ventana.
            
            new Thread(cliente).start(); //Lanzamos el proceso del cliente
            
        } catch (IOException e) {
            //Si el servidor no está conectado Informamos 
            JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor\n" + e.getMessage(), "Error: 1", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // y salimos con el codigo de error 1
        }
    }

}
