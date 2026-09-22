import adaptadores.red.AdaptadorClienteUdp;
import aplicacion.servicios.CalcularIvaService;
import dominio.puertos.ClienteUdpPort;
import entrypoint.gui.ClienteFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensamblaje de la Arquitectura Hexagonal (Inyección de dependencias manual)
        
        // 1. Instanciar Adaptadores (Infraestructura)
        ClienteUdpPort adaptadorUdp = new AdaptadorClienteUdp();
        
        // 2. Instanciar Casos de Uso (Aplicación)
        CalcularIvaService calcularIvaService = new CalcularIvaService(adaptadorUdp);
        
        // 3. Instanciar Entrypoints (GUI)
        SwingUtilities.invokeLater(() -> {
            ClienteFrame frame = new ClienteFrame(calcularIvaService);
            frame.setVisible(true);
        });
    }
}
