import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class IVAClient extends JFrame {

    // Componentes pestaña conexión
    private JTextField txtIpServidor;
    private JTextField txtPuertoServidor;
    private JButton btnConectar;
    private JButton btnCancelarConexion;

    // Componentes pestaña cálculo
    private JTextField txtPrecio;
    private JTextField txtPorcentajeIva;
    private JTextArea txtResultado;
    private JButton btnCalcular;
    private JButton btnCancelarCalculo;

    public IVAClient() {
        setTitle("Cliente de Cálculo de IVA");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        // Creación de las pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // ==========================================
        // PESTAÑA 1: CONEXIÓN
        // ==========================================
        JPanel panelConexion = new JPanel(new BorderLayout(10, 10));
        panelConexion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelCentroConexion = new JPanel(new GridLayout(2, 2, 10, 20));
        panelCentroConexion.add(new JLabel("IP Servidor:"));
        txtIpServidor = new JTextField("localhost");
        panelCentroConexion.add(txtIpServidor);

        panelCentroConexion.add(new JLabel("Puerto Servidor:"));
        txtPuertoServidor = new JTextField("5000");
        panelCentroConexion.add(txtPuertoServidor);

        panelConexion.add(panelCentroConexion, BorderLayout.NORTH);

        JPanel panelBotonesConexion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancelarConexion = new JButton("Cancelar");
        btnConectar = new JButton("Conectar"); 
        
        panelBotonesConexion.add(btnCancelarConexion);
        panelBotonesConexion.add(btnConectar);
        panelConexion.add(panelBotonesConexion, BorderLayout.SOUTH);

        // ==========================================
        // PESTAÑA 2: CÁLCULO IVA
        // ==========================================
        JPanel panelCalculo = new JPanel(new BorderLayout(10, 10));
        panelCalculo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new GridLayout(2, 2, 10, 20));
        panelEntrada.add(new JLabel("Precio sin IVA:"));
        txtPrecio = new JTextField();
        panelEntrada.add(txtPrecio);

        panelEntrada.add(new JLabel("Porcentaje de IVA (%):"));
        txtPorcentajeIva = new JTextField();
        panelEntrada.add(txtPorcentajeIva);
        
        panelCalculo.add(panelEntrada, BorderLayout.NORTH);

        JPanel panelResultadosBotones = new JPanel(new BorderLayout(0, 10));
        
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtResultado);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultado"));
        
        panelResultadosBotones.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesCalculo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancelarCalculo = new JButton("Limpiar");
        btnCalcular = new JButton("Calcular");
        btnCalcular.setEnabled(false); // Se habilita al presionar "Conectar" en la otra pestaña

        panelBotonesCalculo.add(btnCancelarCalculo);
        panelBotonesCalculo.add(btnCalcular);

        panelResultadosBotones.add(panelBotonesCalculo, BorderLayout.SOUTH);
        panelCalculo.add(panelResultadosBotones, BorderLayout.CENTER);

        // Añadir pestañas al panel principal
        tabbedPane.addTab("Conexión", panelConexion);
        tabbedPane.addTab("Cálculo IVA", panelCalculo);

        add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // ACCIONES DE BOTONES
        // ==========================================
        btnConectar.addActionListener(e -> {
            if(txtIpServidor.getText().trim().isEmpty() || txtPuertoServidor.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "Debe ingresar IP y Puerto.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            btnCalcular.setEnabled(true);
            txtIpServidor.setEnabled(false);
            txtPuertoServidor.setEnabled(false);
            btnConectar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Datos de conexión configurados.\nVe a la pestaña 'Cálculo IVA' para enviar los datos al servidor.", "Conexión", JOptionPane.INFORMATION_MESSAGE);
            tabbedPane.setSelectedIndex(1); // Cambiar a la pestaña de cálculo automáticamente
        });

        btnCancelarConexion.addActionListener(e -> {
            btnCalcular.setEnabled(false);
            txtIpServidor.setEnabled(true);
            txtPuertoServidor.setEnabled(true);
            btnConectar.setEnabled(true);
            txtIpServidor.setText("localhost");
            txtPuertoServidor.setText("5000");
        });

        btnCancelarCalculo.addActionListener(e -> {
            txtPrecio.setText("");
            txtPorcentajeIva.setText("");
            txtResultado.setText("");
        });

        btnCalcular.addActionListener(e -> calcularIVA());
    }

    private void calcularIVA() {
        String precioStr = txtPrecio.getText().trim();
        String ivaStr = txtPorcentajeIva.getText().trim();

        if (precioStr.isEmpty() || ivaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese ambos valores.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precioSinIva, porcentajeIva;

        try {
            precioSinIva = Double.parseDouble(precioStr);
            porcentajeIva = Double.parseDouble(ivaStr);

            if (precioSinIva < 0 || porcentajeIva < 0) {
                JOptionPane.showMessageDialog(this, "Los valores deben ser positivos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String host = txtIpServidor.getText().trim();
        int port;
        try {
            port = Integer.parseInt(txtPuertoServidor.getText().trim());
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El puerto configurado no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Enviar datos
            out.writeDouble(precioSinIva);
            out.writeDouble(porcentajeIva);

            // Recibir resultados
            double valorIva = in.readDouble();
            double precioFinal = in.readDouble();

            // Mostrar
            txtResultado.setText("");
            txtResultado.append("Precio Original: $" + String.format("%.2f", precioSinIva) + "\n");
            txtResultado.append("Porcentaje IVA : " + porcentajeIva + "%\n");
            txtResultado.append("Valor del IVA  : $" + String.format("%.2f", valorIva) + "\n");
            txtResultado.append("----------------------------\n");
            txtResultado.append("Precio Total   : $" + String.format("%.2f", precioFinal) + "\n");

        } catch (ConnectException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor en " + host + ":" + port + ".\nAsegúrese de que el servidor esté ENCENDIDO.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de comunicación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IVAClient().setVisible(true));
    }
}
