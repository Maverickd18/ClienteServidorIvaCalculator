package entrypoint.gui;

import adaptadores.red.AdaptadorNotificacionGUI;
import aplicacion.servicios.ProcesarPeticionUdpService;
import dominio.modelos.CalculoIva;
import dominio.puertos.PuertoNotificacionEvento;
import entrypoint.udp.ReceptorPeticionesUdp;

import javax.swing.*;
import java.awt.*;

public class ServidorFrame extends JFrame {

    private JTextArea txtLogs;
    private JTextField txtPuerto;
    private JLabel lblEstado;
    private JButton btnIniciar;
    private JButton btnDetener;

    private Thread serverThread;
    private ReceptorPeticionesUdp receptorUdp;

    public ServidorFrame() {
        setTitle("Servidor UDP - Cálculo de IVA (Hexagonal)");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width / 2) - 450, (screenSize.height / 2) - 175);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Pestaña de Conexión ---
        JPanel panelConexion = new JPanel(new BorderLayout(10, 10));
        panelConexion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 10, 20));
        
        panelCentro.add(new JLabel("Puerto de Servicio:"));
        txtPuerto = new JTextField("5000");
        panelCentro.add(txtPuerto);

        panelCentro.add(new JLabel("Estado:"));
        lblEstado = new JLabel("Detenido");
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.ITALIC));
        panelCentro.add(lblEstado);

        panelConexion.add(panelCentro, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDetener = new JButton("Detener");
        btnDetener.setEnabled(false);
        btnIniciar = new JButton("Iniciar");

        panelBotones.add(btnDetener);
        panelBotones.add(btnIniciar);

        panelConexion.add(panelBotones, BorderLayout.SOUTH);

        // --- Pestaña de Logs ---
        JPanel panelLogs = new JPanel(new BorderLayout());
        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLogs);
        panelLogs.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Conexión", panelConexion);
        tabbedPane.addTab("Log de Conexiones", panelLogs);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Acciones ---
        btnIniciar.addActionListener(e -> iniciarServidor());
        btnDetener.addActionListener(e -> detenerServidor());
    }

    private void iniciarServidor() {
        int port;
        try {
            port = Integer.parseInt(txtPuerto.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensamblaje Arquitectura Hexagonal para esta ejecución
        PuertoNotificacionEvento notificador = new AdaptadorNotificacionGUI(txtLogs);
        CalculoIva calculoIva = new CalculoIva();
        ProcesarPeticionUdpService procesarService = new ProcesarPeticionUdpService(calculoIva, notificador);
        
        receptorUdp = new ReceptorPeticionesUdp(port, procesarService, notificador);
        serverThread = new Thread(receptorUdp);
        serverThread.start();

        // Actualizar UI
        lblEstado.setText("En ejecución (Puerto " + port + ")");
        lblEstado.setForeground(new Color(0, 150, 0));
        btnIniciar.setEnabled(false);
        btnDetener.setEnabled(true);
        txtPuerto.setEnabled(false);
    }

    private void detenerServidor() {
        if (receptorUdp != null) {
            receptorUdp.detener();
        }

        // Actualizar UI
        lblEstado.setText("Detenido");
        lblEstado.setForeground(Color.RED);
        btnIniciar.setEnabled(true);
        btnDetener.setEnabled(false);
        txtPuerto.setEnabled(true);
        txtLogs.append("--- SERVIDOR DETENIDO ---\n");
    }
}
