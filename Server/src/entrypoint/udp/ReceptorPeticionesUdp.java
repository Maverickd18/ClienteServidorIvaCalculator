package entrypoint.udp;

import aplicacion.puertos.ProcesarPeticionUdpInputPort;
import dominio.puertos.PuertoNotificacionEvento;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceptorPeticionesUdp implements Runnable {

    private final int puerto;
    private final ProcesarPeticionUdpInputPort procesarService;
    private final PuertoNotificacionEvento notificador;
    private DatagramSocket socket;
    private volatile boolean corriendo;

    public ReceptorPeticionesUdp(int puerto, ProcesarPeticionUdpInputPort procesarService, PuertoNotificacionEvento notificador) {
        this.puerto = puerto;
        this.procesarService = procesarService;
        this.notificador = notificador;
        this.corriendo = true;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(puerto);
            notificador.notificar("--- SERVIDOR INICIADO EN EL PUERTO " + puerto + " (UDP) ---");

            byte[] buffer = new byte[1024];

            while (corriendo) {
                try {
                    DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paqueteRecibido);

                    String datosEntrada = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                    notificador.notificar(">>> Petición recibida de: " + paqueteRecibido.getAddress().getHostAddress() + ":" + paqueteRecibido.getPort());

                    // Procesar a través de la capa de aplicación
                    String respuesta = procesarService.procesar(datosEntrada);

                    // Enviar respuesta
                    byte[] bufferRespuesta = respuesta.getBytes();
                    DatagramPacket paqueteRespuesta = new DatagramPacket(
                            bufferRespuesta,
                            bufferRespuesta.length,
                            paqueteRecibido.getAddress(),
                            paqueteRecibido.getPort()
                    );
                    
                    socket.send(paqueteRespuesta);
                    notificador.notificar("<<< Resultados enviados al cliente.");
                    notificador.notificar("-");

                } catch (SocketException e) {
                    if (!corriendo) {
                        notificador.notificar("El servidor ha sido detenido.");
                    } else {
                        notificador.notificar("Error de Socket: " + e.getMessage());
                    }
                } catch (IOException e) {
                    notificador.notificar("Error procesando petición: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            notificador.notificar("Error al iniciar el servidor (¿puerto en uso?): " + e.getMessage());
        } finally {
            detener();
        }
    }

    public void detener() {
        corriendo = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
