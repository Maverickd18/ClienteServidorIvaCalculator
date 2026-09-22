package dominio.puertos;

import dominio.modelos.DatosIva;
import dominio.modelos.ResultadoIva;
import java.io.IOException;

public interface ClienteUdpPort {
    ResultadoIva enviarPeticionCalculo(DatosIva datos, String host, int puerto) throws IOException;
}
