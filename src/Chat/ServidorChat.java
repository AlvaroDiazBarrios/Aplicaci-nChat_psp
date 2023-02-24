package Chat;

import java.io.*;
import java.net.*;

/**
 *
 * @author gandalfvaro
 */
public class ServidorChat {

    static final int MAXIMO = 5; //Variable que controla la cantidad máxima de usuarios a la vex

    public static void main(String args[]) throws IOException {
        
        int port = 60000; //puerto de escucha del servidor

        ServerSocket servidor = new ServerSocket(port); //Creamos el socket del servidor
        System.out.println("Servidor Lanzado"); //Informamos de que se ha lanzado
        Socket[] clientes = new Socket[MAXIMO]; //Creamos un array de clientes con el límite máximo de clientes que vamos a permitir
        
        ComunHilos compartido = new ComunHilos(MAXIMO, 0, 0, clientes); //Creamos el Objeto compartido que almacena toda la información
        
        //Mientras que en el objeto común haya menos conexiones que las máximas
        while (compartido.getCONEXIONES() < MAXIMO) { 
            Socket socket = servidor.accept();// esperamos al cliente
            compartido.addCliente(socket, compartido.getCONEXIONES());  //Añadimos un nuevo cliente a la tabla de clientes en la posición que le corresponde
            compartido.setACTUALES(compartido.getACTUALES() + 1); //Aumentamos en uno el numero de conexiones actuales
            compartido.setCONEXIONES(compartido.getCONEXIONES() + 1); //Aumentamos en uno el numero de conxesiones
            HiloServidorChat hilo = new HiloServidorChat(socket, compartido); //Creamos un hilo
            hilo.start(); //Lanzamos un hilo
        }
        servidor.close(); //Cerramos el servidor
    }
}
