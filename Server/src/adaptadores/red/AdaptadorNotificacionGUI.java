package adaptadores.red;

import dominio.puertos.PuertoNotificacionEvento;

import javax.swing.SwingUtilities;
import javax.swing.JTextArea;

public class AdaptadorNotificacionGUI implements PuertoNotificacionEvento {

    private final JTextArea txtLogs;

    public AdaptadorNotificacionGUI(JTextArea txtLogs) {
        this.txtLogs = txtLogs;
    }

    @Override
    public void notificar(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(mensaje + "\n");
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }
}
