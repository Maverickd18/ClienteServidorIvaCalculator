package adaptadores.red;

import dominio.modelos.DatosIva;
import dominio.modelos.ResultadoIva;
import dominio.puertos.ClienteUdpPort;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AdaptadorClienteUdp implements ClienteUdpPort {

    @Override
    public ResultadoIva enviarPeticionCalculo(DatosIva datos, String host, int puerto) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000); // Timeout de 5 segundos
            InetAddress address = InetAddress.getByName(host);

            // Preparar datos a enviar: "precioSinIva,porcentajeIva"
            String mensajeEnviar = datos.getPrecioSinIva() + "," + datos.getPorcentajeIva();
            byte[] bufEnvio = mensajeEnviar.getBytes();
            DatagramPacket packetEnvio = new DatagramPacket(bufEnvio, bufEnvio.length, address, puerto);
            socket.send(packetEnvio);

            // Recibir respuesta: "valorIva,precioFinal"
            byte[] bufRecepcion = new byte[1024];
            DatagramPacket packetRecepcion = new DatagramPacket(bufRecepcion, bufRecepcion.length);
            socket.receive(packetRecepcion);

            String respuesta = new String(packetRecepcion.getData(), 0, packetRecepcion.getLength());
            String[] partes = respuesta.split(",");
            
            if(partes.length != 2) {
                throw new IOException("Respuesta del servidor mal formateada: " + respuesta);
            }

            double valorIva = Double.parseDouble(partes[0]);
            double precioFinal = Double.parseDouble(partes[1]);

            return new ResultadoIva(datos.getPrecioSinIva(), datos.getPorcentajeIva(), valorIva, precioFinal);
        }
    }
}
