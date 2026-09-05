import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class IVAServer extends JFrame {

    private JTextArea txtLogs;
    private JTextField txtPuerto;
    private JLabel lblEstado;
    private JButton btnIniciar;
    private JButton btnDetener;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean isRunning = false;

    public IVAServer() {
        setTitle("Servidor de Cálculo de IVA");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centrar a la izquierda para no estorbar con el cliente
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width / 2) - 450, (screenSize.height / 2) - 175);

        // Creación del panel de pestañas, similar al diseño de la imagen
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
        btnDetener.setEnabled(false); // Deshabilitado por defecto
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

        // Añadir pestañas
        tabbedPane.addTab("Conexión", panelConexion);
        tabbedPane.addTab("Log de Conexiones", panelLogs);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Acciones de los Botones ---
        btnIniciar.addActionListener(e -> iniciarServidor());
        btnDetener.addActionListener(e -> detenerServidor());
    }

    private void log(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(mensaje + "\n");
            // Auto-scroll hacia abajo
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }

    private void iniciarServidor() {
        int port;
        try {
            port = Integer.parseInt(txtPuerto.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                isRunning = true;
                
                // Actualizar UI
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("En ejecución (Puerto " + port + ")");
                    lblEstado.setForeground(new Color(0, 150, 0)); // Color verde
                    btnIniciar.setEnabled(false);
                    btnDetener.setEnabled(true);
                    txtPuerto.setEnabled(false);
                });

                log("--- SERVIDOR INICIADO EN EL PUERTO " + port + " ---");

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        log(">>> Nuevo cliente: " + clientSocket.getInetAddress());

                        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                        double precioSinIva = in.readDouble();
                        double porcentajeIva = in.readDouble();

                        log("    Datos: Precio = $" + precioSinIva + " | IVA = " + porcentajeIva + "%");

                        double valorIva = precioSinIva * (porcentajeIva / 100);
                        double precioFinal = precioSinIva + valorIva;

                        out.writeDouble(valorIva);
                        out.writeDouble(precioFinal);
                        
                        log("<<< Resultados enviados al cliente.");
                        log("-");
                        
                        in.close();
                        out.close();
                        clientSocket.close();

                    } catch (SocketException e) {
                        // Esta excepción salta cuando le damos al botón "Detener" (cerramos el socket manualmente)
                        if (!isRunning) {
                            log("El servidor ha sido detenido.");
                        } else {
                            log("Error de Socket: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        log("Error en la conexión con el cliente: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                log("Error al iniciar el servidor: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "No se pudo iniciar el servidor.\n¿Quizás el puerto " + port + " ya está en uso?", "Error", JOptionPane.ERROR_MESSAGE);
                    detenerServidor(); // Restaurar botones
                });
            }
        });
        
        serverThread.start();
    }

    private void detenerServidor() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Cerramos el socket para forzar la salida del .accept()
            }
        } catch (IOException e) {
            log("Error al cerrar el servidor: " + e.getMessage());
        }

        // Actualizar UI
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("Detenido");
            lblEstado.setForeground(Color.RED);
            btnIniciar.setEnabled(true);
            btnDetener.setEnabled(false);
            txtPuerto.setEnabled(true);
            log("--- SERVIDOR DETENIDO ---");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IVAServer().setVisible(true);
        });
    }
}
