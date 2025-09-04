import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
public class Main {
    public static void main(String[] args) {
        ServerSocket socketEspecial = null;
        try {
            socketEspecial = new ServerSocket(8080);
            System.out.println("Servidor iniciado en puerto 8080...");
        } catch (IOException e) {
            System.out.println("Hubo problemas en la conexion de red");
            System.exit(1);
        }

        Socket cliente = null;
        try {
            cliente = socketEspecial.accept();
            System.out.println("Cliente conectado.");
        } catch (IOException e) {
            System.out.println("Hubo problemas en la conexion de red");
            System.exit(1);
        }

        try (
                PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader lectorSocket = new BufferedReader(new InputStreamReader(cliente.getInputStream()))
        ) {
            Random random = new Random();
            boolean seguirJugando = true;

            while (seguirJugando) {
                int numeroSecreto = random.nextInt(10) + 1;
                int intentos = 3;
                boolean adivinado = false;

                escritor.println("Adivina el número (entre 1 y 10). ¡Tienes 3 oportunidades!");

                for (int i = 1; i <= intentos; i++) {
                    escritor.println("Intento " + i + ": Escribe un número:");
                    String entrada = lectorSocket.readLine();

                    if (entrada == null) {
                        seguirJugando = false; // cliente se desconectó
                        break;
                    }

                    int numeroCliente;
                    try {
                        numeroCliente = Integer.parseInt(entrada);
                    } catch (NumberFormatException e) {
                        escritor.println("Eso no es un número válido.");
                        i--; // no contar el intento
                        continue;
                    }

                    if (numeroCliente == numeroSecreto) {
                        escritor.println("¡Felicidades! Adivinaste el número " + numeroSecreto + " en el intento " + i);
                        adivinado = true;
                        break;
                    } else {
                        escritor.println("Incorrecto.");
                    }
                }

                if (!adivinado) {
                    escritor.println("Eres menso, el número era: " + numeroSecreto);
                }

                // Preguntar si quiere jugar otra vez
                escritor.println("¿Quieres jugar de nuevo? (SI/NO)");
                String respuesta = lectorSocket.readLine();

                if (respuesta == null || !respuesta.equalsIgnoreCase("SI")) {
                    seguirJugando = false;
                    escritor.println("FIN");
                    System.out.println("El cliente decidió terminar.");
                }
            }

            System.out.println("Juego terminado. Servidor se cierra.");
        } catch (IOException e) {
            System.out.println("Error de comunicacion entre los sockets");
            System.exit(2);
        } finally {
            try {
                cliente.close();
                socketEspecial.close();
            } catch (IOException e) {
                System.out.println("Hubo problemas en la conexion en la red");
            }
        }
    }
}