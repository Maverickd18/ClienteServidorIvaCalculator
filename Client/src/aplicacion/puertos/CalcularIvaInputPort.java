package aplicacion.puertos;

import dominio.modelos.DatosIva;
import dominio.modelos.ResultadoIva;

public interface CalcularIvaInputPort {
    ResultadoIva calcular(DatosIva datos, String host, int puerto) throws Exception;
}
