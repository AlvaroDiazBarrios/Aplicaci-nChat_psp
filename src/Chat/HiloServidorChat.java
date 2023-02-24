package Chat;

import java.io.*;
import java.net.*;

/**
 *
 * @author gandalfvaro
 */
public class HiloServidorChat extends Thread {

    DataInputStream in; //input stream
    Socket cliente; //Socket del cliente
    ComunHilos compartido; //Objeto compartido entre todos

    public HiloServidorChat(Socket s, ComunHilos comun) {
        this.cliente = s; //Vinculamos el cliente
        this.compartido = comun; //Asignamos el objeto compartido
        
        try {
            
            in = new DataInputStream(cliente.getInputStream()); //Abrimos el flujo de entrada para leer entre todos.
        
        } catch (IOException e) {
            System.out.println("error de entrada y salida");
            e.printStackTrace();
        }
        
    }

    @Override
    public void run() {
        
        System.out.println("conexiones actuales: " + compartido.getACTUALES()); //Informo de las conexiones actuales
        String texto = compartido.getMensajes(); //Recojo todos los mensajes del servidor
        MensajearATodos(texto); //mando los mensajes a todos los chats
        String cadena;
        
        while (true) {
            try {
                
                cadena = in.readUTF(); //Leemos del cliente
                
                //Si manda la señal de desconexión
                if (cadena.trim().equals("*")) {
                    compartido.setACTUALES(compartido.getACTUALES() - 1); //Eliminamos el cliente de actuales
                    System.out.println("conexiones actuales: " + compartido.getACTUALES()); //Mostramos cuantos usuarios actules hay
                    break; //Salgo del bucle
                }
                
                compartido.setMensajes(compartido.getMensajes() + cadena + "\n"); //Añado el mensaje al "historial" de mi chat
                MensajearATodos(compartido.getMensajes()); //Mando los mensajes
                
            } catch (Exception e) {
                //Si ocurre cualquier error desconectamos al cliente para evitar que el resto de clientes se caigan.
                e.printStackTrace();
                break;
            }
        }
        
        try {
            cliente.close(); // Tras la desconexión cerramos el socket del cliente
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void MensajearATodos(String texto) {
        
        //Recorremos todos los clientes que hay en la sala para enviarles los mensajes
        for (int i = 0; i < compartido.getCONEXIONES(); i++) {
            Socket c = compartido.getCliente(i); //Cojemos el cliente del array de clientes
            //Si no está cerrado
            if (!c.isClosed()) {
                try {
                    
                    DataOutputStream out = new DataOutputStream(c.getOutputStream()); //Abrimos el outputStream del cliente
                    out.writeUTF(texto); //Le enviamos el mensaje
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
