package aplicacion.servicios;

import aplicacion.puertos.ProcesarPeticionUdpInputPort;
import dominio.modelos.CalculoIva;
import dominio.puertos.PuertoNotificacionEvento;

public class ProcesarPeticionUdpService implements ProcesarPeticionUdpInputPort {

    private final CalculoIva calculoIva;
    private final PuertoNotificacionEvento notificador;

    public ProcesarPeticionUdpService(CalculoIva calculoIva, PuertoNotificacionEvento notificador) {
        this.calculoIva = calculoIva;
        this.notificador = notificador;
    }

    @Override
    public String procesar(String datosEntrada) {
        try {
            String[] partes = datosEntrada.trim().split(",");
            if (partes.length != 2) {
                notificador.notificar("Error: Formato de datos incorrecto. Esperado 'precio,iva'");
                return "0.0,0.0";
            }

            double precioSinIva = Double.parseDouble(partes[0]);
            double porcentajeIva = Double.parseDouble(partes[1]);

            notificador.notificar("    Datos: Precio = $" + precioSinIva + " | IVA = " + porcentajeIva + "%");

            double valorIva = calculoIva.calcularValorIva(precioSinIva, porcentajeIva);
            double precioFinal = calculoIva.calcularPrecioFinal(precioSinIva, valorIva);

            return valorIva + "," + precioFinal;

        } catch (NumberFormatException e) {
            notificador.notificar("Error: Datos no numéricos recibidos.");
            return "0.0,0.0";
        } catch (Exception e) {
            notificador.notificar("Error interno: " + e.getMessage());
            return "0.0,0.0";
        }
    }
}
