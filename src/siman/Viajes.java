/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siman;

import bd.ConexionBD;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.placeholder.PlaceHolder;
import java.awt.Color;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import utilidades.Queries;

/**
 *
 * @author Carlos
 */
public class Viajes extends javax.swing.JFrame {

    boolean facturaActiva = false;
    Connection con = null;
    int filaSeleccionada;
    int idColaboradorActivo;
    int idColaboradorRegistrado;
    int idSucursal;
    int idTransportista;
    int idViaje;
    int idDetalle;
    int idAsignacion;
    double tarifa;
    double distancia;
    double distanciaTotal;
    double total = 0;
    String nombreColaborador;
    String nombreSucursal;
    String fecha;
    String numeroIdentidadColaborador;

    /**
     * Creates new form Viajes
     *
     * @param nombreUsuario
     * @param idColaboradorActivo
     * @throws java.sql.SQLException
     */
    public Viajes(String nombreUsuario, int idColaboradorActivo) throws SQLException {
        initComponents();
        holders();
        informacionGeneral();
        lbl_nombreUsuario.setText(nombreUsuario);
        this.con = ConexionBD.obtenerConexion();
        llenarTransportistas();
        llenarSucursales();
        this.idColaboradorActivo = idColaboradorActivo;
    }

    public Viajes() throws SQLException {
        initComponents();
        holders();
        informacionGeneral();
        llenarTransportistas();
        llenarSucursales();
        this.con = ConexionBD.obtenerConexion();
        idColaboradorActivo = 2;
    }

    public void informacionGeneral() {
        this.setTitle("Viajes");
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("../Img/icono.png")).getImage());
        PlaceHolder holder;
    }

    private void holders() {
        PlaceHolder holder;
        holder = new PlaceHolder(txt_distancia, Color.gray, Color.black, "Distancia", false, "Roboto", 25);
    }

    private void llenarSucursales() {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarSucursales();
            for (int i = 0; i < lista.size(); i++) {
                cmb_sucursales.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiar() {
        cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
        cmb_sucursales.setSelectedItem("Seleccione la sucursal");
        txt_distancia.setText("");
        txt_tarifa.setText("0");
        txt_total.setText("0");
    }

    private void restablecer() {
        limpiar();
        holders();
        restablecerComboBoxes();
        variablesPorDefecto();
    }

    public int capturarIdColaboradorQueViaja(String numeroIdentidad) {
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_colaborador from colaboradores where numero_identidad_colaborador = '" + numeroIdentidad + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                id = Integer.parseInt(rs.getString("id_colaborador"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public String capturarNombreColaborador(int id_colaborador) {
        try {
            Statement st = con.createStatement();
            String sql = "select nombre_colaborador, apellido_colaborador from colaboradores where id_colaborador = '" + id_colaborador + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                nombreColaborador = rs.getString("nombre_colaborador") + " " + rs.getString("apellido_colaborador");
                return nombreColaborador;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public int capturarIdAsignacion(int idColaborador, int idSucursal) {
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_asignacion from asignaciones where id_colaborador = '" + idColaborador + "' and id_sucursal ='" + idSucursal + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                id = Integer.parseInt(rs.getString("id_asignacion"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private void actualizarTabla() {
        try {
            String informacionTransportista = cmb_transportistas.getSelectedItem().toString();
            String reemplazarInformacionTransportistas = informacionTransportista.replace("(", "|");
            String informacionTransportistaFormateada = reemplazarInformacionTransportistas.replace(")", "|");
            String[] partesInformacionTransportistas = informacionTransportistaFormateada.split("\\|");
            String nombreTransportista = partesInformacionTransportistas[0].trim();
            DefaultTableModel model = (DefaultTableModel) tbl_viajes.getModel();
            String[] datos = new String[6];
            datos[0] = Integer.toString(idDetalle);
            datos[1] = nombreTransportista;
            datos[2] = nombreSucursal;
            datos[3] = nombreColaborador;
            datos[4] = numeroIdentidadColaborador;
            datos[5] = txt_distancia.getText();
            model.addRow(datos);
            btn_limpiar.setEnabled(false);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void restablecerComboBoxes() {
        cmb_colaboradores.setEnabled(false);
        cmb_sucursales.setEnabled(false);
    }

    public void accionesIniciar() {
        lbl_iniciar.setEnabled(false);
        btn_cancelarViaje.setEnabled(true);
        btn_finalizarViaje.setEnabled(true);
        tbl_viajes.setEnabled(true);
        lbl_sucursales.setEnabled(true);
        cmb_sucursales.setEnabled(true);
        facturaActiva = true;
    }

    public void botonesPorDefecto() {
        lbl_iniciar.setEnabled(false);
        btn_limpiar.setEnabled(false);
        btn_cancelarViaje.setEnabled(false);
        btn_eliminarViaje.setEnabled(false);
        btn_finalizarViaje.setEnabled(false);
        btn_agregarColaborador.setEnabled(false);
    }

    public void deleteAllRows(final DefaultTableModel model) {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public void variablesPorDefecto() {
        filaSeleccionada = 0;
        idColaboradorRegistrado = 0;
        idSucursal = 0;
        idTransportista = 0;
        idViaje = 0;
        idDetalle = 0;
        tarifa = 0.0;
        distancia = 0.0;
        distanciaTotal = 0.0;
        total = 0.0;
        nombreColaborador = "";
        nombreSucursal = "";
        fecha = "";
        numeroIdentidadColaborador = "";
    }

    public void accionesCancelar() {
        facturaActiva = false;
        variablesPorDefecto();
        txt_subtotal.setText("0");
        txt_isv.setText("0");
        txt_total.setText("0");
        txt_tarifa.setText("0");
        txt_distanciaTotal.setText("0");
        jdt_fechaViaje.setCalendar(null);
        jdt_fechaViaje.setEnabled(true);
        botonesPorDefecto();
        cmb_transportistas.setEnabled(true);
        cmb_colaboradores.setEnabled(false);
        cmb_sucursales.setEnabled(false);
        cmb_transportistas.setSelectedItem("Seleccione al transportista");
        cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
        cmb_sucursales.setSelectedItem("Seleccione la sucursal");
        DefaultTableModel model = (DefaultTableModel) tbl_viajes.getModel();
        deleteAllRows(model);
    }

    private int capturarIdSucursal(String nombreSucursal) {
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_sucursal from sucursales where nombre_sucursal = '" + nombreSucursal + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                id = Integer.parseInt(rs.getString("id_sucursal"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private int capturarIdTransportista(String numeroIdentidad) {
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_transportista from transportistas where numero_identidad_transportista = '" + numeroIdentidad + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                id = Integer.parseInt(rs.getString("id_transportista"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public void capturarIdViaje() {
        try {
            Statement st = con.createStatement();
            String sql = "Select top 1 id_viaje_encabezado from viajes_encabezado order by id_viaje_encabezado desc";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                idViaje = rs.getInt("id_viaje_encabezado");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Viajes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double llenarTarifa(String numeroIdentidadTransportista) {
        try {
            Statement st = con.createStatement();
            String sql = "select ta.tarifa from transportistas as tr "
                    + "join tarifa as ta on tr.id_tarifa = ta.id_tarifa "
                    + "where tr.numero_identidad_transportista = '" + numeroIdentidadTransportista + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                tarifa = rs.getDouble("tarifa");
                return tarifa;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0.0;
    }

    public double llenarDistancia(int idColaborador, int idSucursal) {
        try {
            Statement st = con.createStatement();
            String sql = "select distancia from asignaciones "
                    + "where id_colaborador = '" + idColaborador + "' and id_sucursal = '" + idSucursal + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                distancia = rs.getDouble("distancia");
                return distancia;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0.0;
    }

    public void guardarDetalle() {
        idAsignacion = capturarIdAsignacion(idColaboradorRegistrado, idSucursal);
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("INSERT INTO viajes_detalle (id_viaje_encabezado, id_colaborador,id_asignacion) "
                    + "VALUES(?,?,?)");
            ps.setInt(1, idViaje);
            ps.setInt(2, idColaboradorRegistrado);
            ps.setInt(3, idAsignacion);
            int res = ps.executeUpdate();
            if (res > 0) {
                Statement st = con.createStatement();
                String sql = "Select top 1 * from viajes_detalle order by id_viaje_detalle desc";
                ResultSet rs1 = st.executeQuery(sql);
                if (rs1.next()) {
                    idDetalle = (rs1.getInt("id_viaje_detalle"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public boolean colaboradorYaAsignado(String fecha, int colaborador) {
        try {
            Statement st = con.createStatement();
            String sql = "select * from viajes_encabezado as ve "
                    + "join viajes_detalle as vd "
                    + "on ve.id_viaje_encabezado = vd.id_viaje_encabezado "
                    + "where ve.fecha_viaje = '" + fecha + "' and vd.id_colaborador = " + colaborador + "";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                Calendar f = jdt_fechaViaje.getCalendar();
                int d = f.get(Calendar.DATE), mes = 1 + (f.get(Calendar.MONTH)), año = f.get(Calendar.YEAR);
                String fechaFormateada = (d + "/" + mes + "/" + año);
                JOptionPane.showMessageDialog(null, "El empleado ya posee un viaje programado para el día: " + fechaFormateada, "Viaje ya programado", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Viajes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void limpiarColaboradores() {
        cmb_colaboradores.removeAllItems();
        cmb_colaboradores.addItem("Seleccione al colaborador");
    }

    private void limpiarSucursales() {
        cmb_sucursales.removeAllItems();
        cmb_sucursales.addItem("Seleccione la sucursal");
    }

    private void llenarColaboradoresPorSucursal(String nombreSucursal) {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarColaboradoresPorSucursal(nombreSucursal);
            for (int i = 0; i < lista.size(); i++) {
                cmb_colaboradores.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenarTransportistas() {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarTransportistas();
            for (int i = 0; i < lista.size(); i++) {
                cmb_transportistas.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel1 = new keeptoo.KGradientPanel();
        jpn_principal = new javax.swing.JPanel();
        lbl_nombreUsuario = new javax.swing.JLabel();
        lbl_home = new javax.swing.JLabel();
        lbl_usuario = new javax.swing.JLabel();
        lbl_tituloViajes = new javax.swing.JLabel();
        jpn_productos = new javax.swing.JPanel();
        lbl_sucursales = new javax.swing.JLabel();
        cmb_sucursales = new javax.swing.JComboBox<>();
        lbl_colaboradores = new javax.swing.JLabel();
        cmb_colaboradores = new javax.swing.JComboBox<>();
        lbl_distancia = new javax.swing.JLabel();
        txt_distancia = new javax.swing.JTextField();
        btn_agregarColaborador = new javax.swing.JButton();
        lbl_iniciar = new javax.swing.JLabel();
        lbl_fechaViaje = new javax.swing.JLabel();
        jdt_fechaViaje = new com.toedter.calendar.JDateChooser();
        lbl_transportistas = new javax.swing.JLabel();
        cmb_transportistas = new javax.swing.JComboBox<>();
        jsp_tabla = new javax.swing.JScrollPane();
        tbl_viajes = new javax.swing.JTable();
        jpn_acciones = new javax.swing.JPanel();
        jpn_total = new javax.swing.JPanel();
        lbl_subtotal = new javax.swing.JLabel();
        lbl_isv = new javax.swing.JLabel();
        lbl_total = new javax.swing.JLabel();
        txt_subtotal = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        txt_isv = new javax.swing.JTextField();
        jpn_tarifa = new javax.swing.JPanel();
        lbl_tarifa = new javax.swing.JLabel();
        lbl_distanciaTotal = new javax.swing.JLabel();
        txt_distanciaTotal = new javax.swing.JTextField();
        txt_tarifa = new javax.swing.JTextField();
        btn_cancelarViaje = new javax.swing.JButton();
        btn_limpiar = new javax.swing.JButton();
        btn_finalizarViaje = new javax.swing.JButton();
        btn_eliminarViaje = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        kGradientPanel1.setkEndColor(new java.awt.Color(40, 74, 172));
        kGradientPanel1.setkStartColor(new java.awt.Color(205, 63, 145));
        kGradientPanel1.setkTransparentControls(false);
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(1319, 821));

        jpn_principal.setBackground(new java.awt.Color(255, 255, 255));

        lbl_nombreUsuario.setFont(new java.awt.Font("Roboto Black", 0, 24)); // NOI18N
        lbl_nombreUsuario.setText("Nombre de Usuario");

        lbl_home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/home.png"))); // NOI18N
        lbl_home.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_homeMousePressed(evt);
            }
        });

        lbl_usuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/profile.png"))); // NOI18N
        lbl_usuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lbl_tituloViajes.setFont(new java.awt.Font("Roboto Black", 0, 48)); // NOI18N
        lbl_tituloViajes.setText("Viajes");

        jpn_productos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lbl_sucursales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/ubicacion.png"))); // NOI18N
        lbl_sucursales.setEnabled(false);
        lbl_sucursales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_sucursalesMousePressed(evt);
            }
        });

        cmb_sucursales.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_sucursales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione la sucursal" }));
        cmb_sucursales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_sucursales.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_sucursales.setEnabled(false);
        cmb_sucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_sucursalesActionPerformed(evt);
            }
        });

        lbl_colaboradores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/name.png"))); // NOI18N
        lbl_colaboradores.setEnabled(false);
        lbl_colaboradores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_colaboradoresMousePressed(evt);
            }
        });

        cmb_colaboradores.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_colaboradores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione al colaborador" }));
        cmb_colaboradores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_colaboradores.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_colaboradores.setEnabled(false);
        cmb_colaboradores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_colaboradoresActionPerformed(evt);
            }
        });

        lbl_distancia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/distancia.png"))); // NOI18N
        lbl_distancia.setEnabled(false);
        lbl_distancia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_distanciaMousePressed(evt);
            }
        });

        txt_distancia.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_distancia.setEnabled(false);
        txt_distancia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_distanciaActionPerformed(evt);
            }
        });
        txt_distancia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_distanciaKeyTyped(evt);
            }
        });

        btn_agregarColaborador.setBackground(new java.awt.Color(205, 63, 145));
        btn_agregarColaborador.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_agregarColaborador.setForeground(new java.awt.Color(255, 255, 255));
        btn_agregarColaborador.setText("Agregar Colaborador");
        btn_agregarColaborador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_agregarColaborador.setEnabled(false);
        btn_agregarColaborador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_agregarColaboradorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_agregarColaboradorMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_agregarColaboradorMousePressed(evt);
            }
        });
        btn_agregarColaborador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregarColaboradorActionPerformed(evt);
            }
        });

        lbl_iniciar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/inicio.png"))); // NOI18N
        lbl_iniciar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_iniciar.setEnabled(false);
        lbl_iniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_iniciarMouseClicked(evt);
            }
        });

        lbl_fechaViaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/calendar.png"))); // NOI18N

        jdt_fechaViaje.setDateFormatString("dd/MM/yyyy");
        jdt_fechaViaje.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jdt_fechaViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jdt_fechaViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jdt_fechaViajeMouseExited(evt);
            }
        });

        lbl_transportistas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/bus.png"))); // NOI18N
        lbl_transportistas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_transportistasMousePressed(evt);
            }
        });

        cmb_transportistas.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_transportistas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione al transportista" }));
        cmb_transportistas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_transportistas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_transportistas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cmb_transportistasMouseEntered(evt);
            }
        });
        cmb_transportistas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_transportistasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpn_productosLayout = new javax.swing.GroupLayout(jpn_productos);
        jpn_productos.setLayout(jpn_productosLayout);
        jpn_productosLayout.setHorizontalGroup(
            jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_productosLayout.createSequentialGroup()
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_productosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_fechaViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdt_fechaViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpn_productosLayout.createSequentialGroup()
                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpn_productosLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(btn_agregarColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpn_productosLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jpn_productosLayout.createSequentialGroup()
                                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lbl_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lbl_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jpn_productosLayout.createSequentialGroup()
                                        .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jpn_productosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpn_productosLayout.setVerticalGroup(
            jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_productosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbl_fechaViaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdt_fechaViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_transportistas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_colaboradores, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_agregarColaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tbl_viajes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Transportista", "Sucursal", "Colaborador", "No. Identidad", "Distancia"
            }
        ));
        tbl_viajes.setEnabled(false);
        tbl_viajes.setRowHeight(18);
        tbl_viajes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_viajesMouseClicked(evt);
            }
        });
        jsp_tabla.setViewportView(tbl_viajes);
        if (tbl_viajes.getColumnModel().getColumnCount() > 0) {
            tbl_viajes.getColumnModel().getColumn(0).setResizable(false);
            tbl_viajes.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        jpn_acciones.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jpn_total.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lbl_subtotal.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_subtotal.setText("Subtotal");

        lbl_isv.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_isv.setText("ISV");

        lbl_total.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_total.setText("Total L");

        txt_subtotal.setEditable(false);
        txt_subtotal.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_subtotal.setText("0");

        txt_total.setEditable(false);
        txt_total.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_total.setText("0");

        txt_isv.setEditable(false);
        txt_isv.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_isv.setText("0");

        javax.swing.GroupLayout jpn_totalLayout = new javax.swing.GroupLayout(jpn_total);
        jpn_total.setLayout(jpn_totalLayout);
        jpn_totalLayout.setHorizontalGroup(
            jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_totalLayout.createSequentialGroup()
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpn_totalLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(lbl_isv))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_totalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_total, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_subtotal, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_subtotal)
                    .addComponent(txt_isv)
                    .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jpn_totalLayout.setVerticalGroup(
            jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_totalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_subtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(lbl_subtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_isv, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(lbl_isv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_total, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(lbl_total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpn_tarifa.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lbl_tarifa.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_tarifa.setText("Tarifa (L/Km)");

        lbl_distanciaTotal.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_distanciaTotal.setText("Distancia total");

        txt_distanciaTotal.setEditable(false);
        txt_distanciaTotal.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_distanciaTotal.setText("0");

        txt_tarifa.setEditable(false);
        txt_tarifa.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        txt_tarifa.setText("0");

        javax.swing.GroupLayout jpn_tarifaLayout = new javax.swing.GroupLayout(jpn_tarifa);
        jpn_tarifa.setLayout(jpn_tarifaLayout);
        jpn_tarifaLayout.setHorizontalGroup(
            jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_tarifaLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_distanciaTotal)
                    .addComponent(lbl_tarifa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_distanciaTotal)
                    .addComponent(txt_tarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jpn_tarifaLayout.setVerticalGroup(
            jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_tarifaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_tarifa, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(lbl_tarifa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_distanciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_distanciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        btn_cancelarViaje.setBackground(new java.awt.Color(205, 63, 145));
        btn_cancelarViaje.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_cancelarViaje.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelarViaje.setText("Cancelar");
        btn_cancelarViaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_cancelarViaje.setEnabled(false);
        btn_cancelarViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_cancelarViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_cancelarViajeMouseExited(evt);
            }
        });
        btn_cancelarViaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarViajeActionPerformed(evt);
            }
        });

        btn_limpiar.setBackground(new java.awt.Color(205, 63, 145));
        btn_limpiar.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_limpiar.setForeground(new java.awt.Color(255, 255, 255));
        btn_limpiar.setText("Limpiar");
        btn_limpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_limpiar.setEnabled(false);
        btn_limpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_limpiarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_limpiarMouseExited(evt);
            }
        });
        btn_limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiarActionPerformed(evt);
            }
        });

        btn_finalizarViaje.setBackground(new java.awt.Color(205, 63, 145));
        btn_finalizarViaje.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_finalizarViaje.setForeground(new java.awt.Color(255, 255, 255));
        btn_finalizarViaje.setText("Finalizar");
        btn_finalizarViaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_finalizarViaje.setEnabled(false);
        btn_finalizarViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_finalizarViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_finalizarViajeMouseExited(evt);
            }
        });
        btn_finalizarViaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_finalizarViajeActionPerformed(evt);
            }
        });

        btn_eliminarViaje.setBackground(new java.awt.Color(205, 63, 145));
        btn_eliminarViaje.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_eliminarViaje.setForeground(new java.awt.Color(255, 255, 255));
        btn_eliminarViaje.setText("Eliminar");
        btn_eliminarViaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_eliminarViaje.setEnabled(false);
        btn_eliminarViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_eliminarViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_eliminarViajeMouseExited(evt);
            }
        });
        btn_eliminarViaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarViajeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpn_accionesLayout = new javax.swing.GroupLayout(jpn_acciones);
        jpn_acciones.setLayout(jpn_accionesLayout);
        jpn_accionesLayout.setHorizontalGroup(
            jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_accionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpn_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jpn_tarifa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_finalizarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_eliminarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39))
        );
        jpn_accionesLayout.setVerticalGroup(
            jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_accionesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_finalizarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_eliminarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
            .addGroup(jpn_accionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpn_accionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpn_tarifa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpn_total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpn_principalLayout = new javax.swing.GroupLayout(jpn_principal);
        jpn_principal.setLayout(jpn_principalLayout);
        jpn_principalLayout.setHorizontalGroup(
            jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_usuario)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_nombreUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_home)
                        .addGap(6, 6, 6))
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jpn_acciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lbl_tituloViajes)
                                .addGap(576, 576, 576))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                                .addComponent(jsp_tabla)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpn_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(21, 21, 21))
        );
        jpn_principalLayout.setVerticalGroup(
            jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_principalLayout.createSequentialGroup()
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(lbl_tituloViajes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpn_productos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jsp_tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jpn_acciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1426, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(kGradientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 928, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_eliminarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarViajeActionPerformed
        btn_eliminarViaje.setBackground(new Color(40, 74, 172));
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("Delete viajes_detalle\n"
                    + "where id_viaje_detalle =?");
            String idViajeDetalle = tbl_viajes.getValueAt(filaSeleccionada, 0).toString();
            int id = Integer.parseInt(idViajeDetalle);
            ps.setInt(1, id);
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Viaje eliminado");
                DefaultTableModel model = (DefaultTableModel) tbl_viajes.getModel();
                model.removeRow(filaSeleccionada);
                int totalFilas = tbl_viajes.getRowCount();
                distanciaTotal = 0;
                for (int i = 0; i < totalFilas; i++) {
                    distanciaTotal += Double.parseDouble(tbl_viajes.getValueAt(i, 5).toString());
                }
                double nuevoTotal = tarifa * distanciaTotal;
                String totalFormateado = String.format("%.2f", nuevoTotal);
                String distanciaFormateada = String.format("%.2f", distanciaTotal);
                txt_distanciaTotal.setText(distanciaFormateada);
                txt_total.setText(totalFormateado);
                btn_eliminarViaje.setEnabled(false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_eliminarViajeActionPerformed

    private void btn_eliminarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_eliminarViajeMouseExited
        btn_eliminarViaje.setBackground(new Color(205, 63, 145));
    }//GEN-LAST:event_btn_eliminarViajeMouseExited

    private void btn_eliminarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_eliminarViajeMouseEntered
        btn_eliminarViaje.setBackground(new Color(156, 2, 91));
    }//GEN-LAST:event_btn_eliminarViajeMouseEntered

    private void btn_finalizarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_finalizarViajeActionPerformed
        btn_finalizarViaje.setBackground(new Color(40, 74, 172));
        try {
            double totalViaje = Double.parseDouble(txt_total.getText());
            Statement st = con.createStatement();
            String sql = "Update viajes_encabezado "
                    + "set total_viaje = '" + totalViaje + "'"
                    + "where id_viaje_encabezado =  '" + idViaje + "'";
            int res = st.executeUpdate(sql);
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Viaje finalizado");
                accionesCancelar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_finalizarViajeActionPerformed

    private void btn_finalizarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_finalizarViajeMouseExited
        btn_finalizarViaje.setBackground(new Color(205, 63, 145));
    }//GEN-LAST:event_btn_finalizarViajeMouseExited

    private void btn_finalizarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_finalizarViajeMouseEntered
        btn_finalizarViaje.setBackground(new Color(156, 2, 91));
    }//GEN-LAST:event_btn_finalizarViajeMouseEntered

    private void btn_limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiarActionPerformed
        btn_limpiar.setBackground(new Color(40, 74, 172));
        restablecer();
        cmb_transportistas.setEnabled(true);
        jdt_fechaViaje.setCalendar(null);
        jdt_fechaViaje.setEnabled(true);
        cmb_transportistas.setSelectedItem("Seleccione al transportista");
        btn_agregarColaborador.setEnabled(false);
        btn_cancelarViaje.setEnabled(false);
        btn_finalizarViaje.setEnabled(false);
        lbl_iniciar.setEnabled(false);
    }//GEN-LAST:event_btn_limpiarActionPerformed

    private void btn_limpiarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_limpiarMouseExited
        btn_limpiar.setBackground(new Color(205, 63, 145));
    }//GEN-LAST:event_btn_limpiarMouseExited

    private void btn_limpiarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_limpiarMouseEntered
        btn_limpiar.setBackground(new Color(156, 2, 91));
    }//GEN-LAST:event_btn_limpiarMouseEntered

    private void btn_cancelarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarViajeActionPerformed
        btn_cancelarViaje.setBackground(new Color(40, 74, 172));
        facturaActiva = false;
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("Delete viajes_detalle "
                    + "where id_viaje_encabezado =?");
            ps.setInt(1, idViaje);
            PreparedStatement ps2;
            ps2 = con.prepareStatement("Delete viajes_encabezado "
                    + "where id_viaje_encabezado =?");
            ps2.setInt(1, idViaje);
            int res = ps.executeUpdate();
            int res2 = ps2.executeUpdate();
            JOptionPane.showMessageDialog(this, "Viaje cancelado");
            accionesCancelar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_cancelarViajeActionPerformed

    private void btn_cancelarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarViajeMouseExited
        btn_cancelarViaje.setBackground(new Color(205, 63, 145));
    }//GEN-LAST:event_btn_cancelarViajeMouseExited

    private void btn_cancelarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarViajeMouseEntered
        btn_cancelarViaje.setBackground(new Color(156, 2, 91));
    }//GEN-LAST:event_btn_cancelarViajeMouseEntered

    private void tbl_viajesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_viajesMouseClicked
        btn_eliminarViaje.setEnabled(true);
        filaSeleccionada = tbl_viajes.getSelectedRow();
    }//GEN-LAST:event_tbl_viajesMouseClicked

    private void lbl_iniciarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_iniciarMouseClicked
        if (!lbl_iniciar.isEnabled()) {
            return;
        }
        try {
            jdt_fechaViaje.setEnabled(false);
            Calendar f = jdt_fechaViaje.getCalendar();
            int d = f.get(Calendar.DATE), mes = 1 + (f.get(Calendar.MONTH)), año = f.get(Calendar.YEAR);
            fecha = (año + "-" + mes + "-" + d);
            PreparedStatement ps;
            double totalInicial = 0;
            ps = con.prepareStatement("INSERT INTO viajes_encabezado (id_colaborador, id_transportista, fecha_viaje, total_viaje) "
                    + "VALUES(?,?,?,?)");
            ps.setInt(1, idColaboradorActivo);
            ps.setInt(2, idTransportista);
            ps.setString(3, fecha);
            ps.setDouble(4, totalInicial);
            int res = ps.executeUpdate();
            if (res > 0) {
                capturarIdViaje();
                accionesIniciar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_lbl_iniciarMouseClicked

    private void cmb_transportistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_transportistasActionPerformed
        if (!cmb_transportistas.getSelectedItem().toString().equals("Seleccione al transportista")) {
            if (jdt_fechaViaje.getCalendar() == null) {
                JOptionPane.showMessageDialog(rootPane, "Seleccione una fecha");
                return;
            }
            limpiarSucursales();
            llenarSucursales();
            String informacionTransportista = cmb_transportistas.getSelectedItem().toString();
            String reemplazarInformacionTransportistas = informacionTransportista.replace("(", "|");
            String informacionTransportistaFormateada = reemplazarInformacionTransportistas.replace(")", "|");
            String[] partesInformacionTransportistas = informacionTransportistaFormateada.split("\\|");
            String numeroIdentidadTransportista = partesInformacionTransportistas[1].trim();
            idTransportista = capturarIdTransportista(numeroIdentidadTransportista);
            tarifa = llenarTarifa(numeroIdentidadTransportista);
            txt_tarifa.setText(Double.toString(tarifa));
            cmb_transportistas.setEnabled(false);
            btn_limpiar.setEnabled(true);
            jdt_fechaViaje.setEnabled(true);
            lbl_iniciar.setEnabled(true);
        }
    }//GEN-LAST:event_cmb_transportistasActionPerformed

    private void lbl_transportistasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_transportistasMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_transportistasMousePressed

    private void btn_agregarColaboradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregarColaboradorActionPerformed
        try {
            double nuevaDistancia = distanciaTotal + Double.parseDouble(txt_distancia.getText());
            if (colaboradorYaAsignado(fecha, idColaboradorRegistrado)) {
                cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
                txt_distancia.setText("Distancia");
                return;
            }
            if (nuevaDistancia > 100) {
                JOptionPane.showMessageDialog(null, "Un viaje no puede acumular mas de 100 km.", "Límite de kilómetros", JOptionPane.ERROR_MESSAGE);
                cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
                txt_distancia.setText("Distancia");
                return;
            }

            guardarDetalle();
            actualizarTabla();

            double totalNuevo = distancia * tarifa;
            total += totalNuevo;
            String totalFormateado = String.format("%.2f", total);
            txt_total.setText(totalFormateado);
            distanciaTotal += Double.parseDouble(txt_distancia.getText());
            txt_distanciaTotal.setText(Double.toString(distanciaTotal));
            tbl_viajes.setEnabled(true);
            txt_distancia.setText("Distancia");
            cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
            cmb_colaboradores.setEnabled(false);
            cmb_sucursales.setSelectedItem("Seleccione la sucursal");
            btn_agregarColaborador.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarColaboradorActionPerformed

    private void btn_agregarColaboradorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarColaboradorMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarColaboradorMousePressed

    private void btn_agregarColaboradorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarColaboradorMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarColaboradorMouseExited

    private void btn_agregarColaboradorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarColaboradorMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarColaboradorMouseEntered

    private void txt_distanciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_distanciaKeyTyped
        char a = evt.getKeyChar();

        if (evt.getKeyChar() == 46) {
            if (txt_distancia.getText().equals("")) {
                evt.consume();
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (txt_distancia.getText().contains(".")) {
                evt.consume();
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        if (txt_distancia.getText().length() >= 5) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Número máximo de dígitos admitidos", "Límite de entrada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (evt.getKeyChar() == 8 || evt.getKeyChar() == 127
                || evt.getKeyChar() == 0 || evt.getKeyChar() == 3 || evt.getKeyChar() == 22
                || evt.getKeyChar() == 26 || evt.getKeyChar() == 24 || evt.getKeyChar() == 44 || evt.getKeyChar() == 46) {
            return;
        }

        if (Character.isLetter(a) || !Character.isLetterOrDigit(a)) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Sólo numeros", "Restricción de entrada", JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaKeyTyped

    private void txt_distanciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_distanciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaActionPerformed

    private void lbl_distanciaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_distanciaMousePressed
        txt_distancia.requestFocus();
    }//GEN-LAST:event_lbl_distanciaMousePressed

    private void cmb_colaboradoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_colaboradoresActionPerformed
        if (cmb_colaboradores.getItemCount() == 0) {
            return;
        }
        if (txt_distanciaTotal.getText().equals("0")) {
            txt_total.setText("0");
        }
        if (!cmb_colaboradores.getSelectedItem().toString().equals("Seleccione al colaborador")) {
            String informacionColaboradores = cmb_colaboradores.getSelectedItem().toString();
            String[] partesInformacionSucursal = informacionColaboradores.split("\\|");

            nombreColaborador = partesInformacionSucursal[0].trim();
            numeroIdentidadColaborador = partesInformacionSucursal[2].trim();
            idColaboradorRegistrado = capturarIdColaboradorQueViaja(numeroIdentidadColaborador);
            distancia = llenarDistancia(idColaboradorRegistrado, idSucursal);
            txt_distancia.setText(Double.toString(distancia));

            btn_agregarColaborador.setEnabled(true);
        } else {
            txt_distancia.setText("Distancia");
        }
    }//GEN-LAST:event_cmb_colaboradoresActionPerformed

    private void lbl_colaboradoresMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_colaboradoresMousePressed
        cmb_colaboradores.requestFocus();
    }//GEN-LAST:event_lbl_colaboradoresMousePressed

    private void cmb_sucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_sucursalesActionPerformed
        if (cmb_sucursales.getItemCount() == 0) {
            return;
        }
        if (txt_distanciaTotal.getText().equals("0")) {
            txt_total.setText("0");
        }

        if (!cmb_sucursales.getSelectedItem().toString().equals("Seleccione la sucursal")) {
            txt_distancia.setText("Distancia");
            limpiarColaboradores();
            String informacionSucursal = cmb_sucursales.getSelectedItem().toString();
            String reemplazarInformacionSucursal = informacionSucursal.replace("(", "|");
            String informacionSucursalFormateada = reemplazarInformacionSucursal.replace(")", "|");
            String[] partesInformacionSucursal = informacionSucursalFormateada.split("\\|");
            nombreSucursal = partesInformacionSucursal[0].trim();
            llenarColaboradoresPorSucursal(nombreSucursal);
            cmb_colaboradores.setEnabled(true);
            idSucursal = capturarIdSucursal(nombreSucursal);
        } else {
            cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
            cmb_colaboradores.setEnabled(false);
            txt_distancia.setText("Distancia");
        }
    }//GEN-LAST:event_cmb_sucursalesActionPerformed

    private void lbl_sucursalesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_sucursalesMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_sucursalesMousePressed

    private void lbl_homeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_homeMousePressed
        if (facturaActiva) {
            JOptionPane.showMessageDialog(null, "No puede regresar mientras un viaje activo, si desea salir, presione cancelar.", "Viaje activo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            this.dispose();
            Principal principal;
            principal = new Principal(lbl_nombreUsuario.getText(), idColaboradorActivo);
            principal.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Viajes.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_homeMousePressed

    private void jdt_fechaViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdt_fechaViajeMouseEntered
        if (jdt_fechaViaje.getCalendar() != null) {
            lbl_iniciar.setEnabled(true);
        }
    }//GEN-LAST:event_jdt_fechaViajeMouseEntered

    private void jdt_fechaViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdt_fechaViajeMouseExited
        if (jdt_fechaViaje.getCalendar() != null) {
            lbl_iniciar.setEnabled(true);
        }
    }//GEN-LAST:event_jdt_fechaViajeMouseExited

    private void cmb_transportistasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmb_transportistasMouseEntered
        if (jdt_fechaViaje.getCalendar() == null) {
            JOptionPane.showMessageDialog(rootPane, "Por favor, seleccione una fecha", "Fecha necesaria", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_cmb_transportistasMouseEntered

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            UIManager.put("Button.arc", 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Viajes().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Viajes.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_agregarColaborador;
    private javax.swing.JButton btn_cancelarViaje;
    private javax.swing.JButton btn_eliminarViaje;
    private javax.swing.JButton btn_finalizarViaje;
    private javax.swing.JButton btn_limpiar;
    private javax.swing.JComboBox<String> cmb_colaboradores;
    private javax.swing.JComboBox<String> cmb_sucursales;
    private javax.swing.JComboBox<String> cmb_transportistas;
    private com.toedter.calendar.JDateChooser jdt_fechaViaje;
    private javax.swing.JPanel jpn_acciones;
    private javax.swing.JPanel jpn_principal;
    private javax.swing.JPanel jpn_productos;
    private javax.swing.JPanel jpn_tarifa;
    private javax.swing.JPanel jpn_total;
    private javax.swing.JScrollPane jsp_tabla;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel lbl_colaboradores;
    private javax.swing.JLabel lbl_distancia;
    private javax.swing.JLabel lbl_distanciaTotal;
    private javax.swing.JLabel lbl_fechaViaje;
    private javax.swing.JLabel lbl_home;
    private javax.swing.JLabel lbl_iniciar;
    private javax.swing.JLabel lbl_isv;
    private javax.swing.JLabel lbl_nombreUsuario;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_sucursales;
    private javax.swing.JLabel lbl_tarifa;
    private javax.swing.JLabel lbl_tituloViajes;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JLabel lbl_transportistas;
    private javax.swing.JLabel lbl_usuario;
    private javax.swing.JTable tbl_viajes;
    private javax.swing.JTextField txt_distancia;
    private javax.swing.JTextField txt_distanciaTotal;
    private javax.swing.JTextField txt_isv;
    private javax.swing.JTextField txt_subtotal;
    private javax.swing.JTextField txt_tarifa;
    private javax.swing.JTextField txt_total;
    // End of variables declaration//GEN-END:variables
}
