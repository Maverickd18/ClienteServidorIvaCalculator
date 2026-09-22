package aplicacion.servicios;

import aplicacion.puertos.CalcularIvaInputPort;
import dominio.modelos.DatosIva;
import dominio.modelos.ResultadoIva;
import dominio.puertos.ClienteUdpPort;

public class CalcularIvaService implements CalcularIvaInputPort {

    private final ClienteUdpPort clienteUdpPort;

    public CalcularIvaService(ClienteUdpPort clienteUdpPort) {
        this.clienteUdpPort = clienteUdpPort;
    }

    @Override
    public ResultadoIva calcular(DatosIva datos, String host, int puerto) throws Exception {
        if (datos.getPrecioSinIva() < 0 || datos.getPorcentajeIva() < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }
        return clienteUdpPort.enviarPeticionCalculo(datos, host, puerto);
    }
}
