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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import utilidades.Queries;

/**
 *
 * @author Carlos
 */
public class Viajes extends javax.swing.JFrame {
    ArrayList<String> viajes = new ArrayList<String>();
    int totalPrecioOrden;
    boolean hayDecimal = false;
    Statement stmt = null;
    Connection con = null;
    int filaSeleccionada;
    int id_colaboradorQueRegistra;
    int id_colaboradorRegistrado;
    int id_sucursal;
    double tarifa;
    double distancia;
    double distanciaTotal;
    double total;
    double subtotal;
    double isv;

    /**
     * Creates new form Viajes
     */
    public Viajes(String nombreUsuario) throws SQLException {
        initComponents();
        holders();
        informacionGeneral();
        lbl_nombreUsuario.setText(nombreUsuario);
        this.con = ConexionBD.obtenerConexion();
        llenarTransportistas();
        llenarSucursales();
        /*lbl_nombreProducto.setVisible(false);
        lbl_idFactura.setVisible(false);
        lbl_idCliente.setVisible(false);
        lbl_idProducto.setVisible(false);
        lbl_idDetalle.setVisible(false);     
        buscarClientes();
        clientesArray();*/
    }

    public Viajes() throws SQLException {
        initComponents();
        holders();
        informacionGeneral();
        llenarTransportistas();
        llenarSucursales();
        this.con = ConexionBD.obtenerConexion();
        //buscarClientes();
        //clientesArray();
    }

    public void informacionGeneral() {
        this.setTitle("Ordenes");
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
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
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
    }
    

    private void actualizarDatos() {
        /*try {
            String nombreProducto = lbl_nombreProducto.getText();
            String sql = "SELECT * FROM inventario where nombreproducto = '" + nombreProducto + "'";
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            double subtotal = 0;
            while (rs.next()) {
                Locale locale = new Locale("es", "HN");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                DefaultTableModel model = (DefaultTableModel) tbl_orden.getModel();
                String[] datos = new String[3];
                datos[0] = rs.getString("nombreproducto");
                datos[1] = "1";
                String precio = currencyFormatter.format(rs.getDouble("precio")).substring(1);
                datos[2] = precio;       
                model.addRow(datos);
                subtotal = rs.getDouble("precio");
                //lbl_idProducto.setText(rs.getString("idproducto"));
                guardarDetalle();
                int indiceUltimaFila = tbl_orden.getRowCount() - 1;
                model.removeRow(indiceUltimaFila);
                String[] datos2 = new String[4];
                datos2[0] = rs.getString("nombreproducto");
                datos2[1] = "1";              
                datos2[2] = precio;
                datos2[3] = lbl_idDetalle.getText();
                model.addRow(datos2);
                
            }
            double totalAnterior = Double.parseDouble(txt_total.getText());
            double totalNuevo = totalAnterior + subtotal;

            double isv = totalNuevo * 0.15;
            double subtotalConImpuesto = totalNuevo - isv;

            double total = subtotalConImpuesto + isv;

            String subtotalFinal = String.valueOf(subtotalConImpuesto);
            String isvFinal = String.valueOf(isv);
            String totalFinal = String.valueOf(total);
            txt_subtotal.setText(subtotalFinal);
            txt_isv.setText(isvFinal);
            txt_total.setText(totalFinal);
        } catch (Exception e) {
            System.err.println(e);
        }*/
    }
    
    public void restablecerComboBoxes(){
        cmb_colaboradores.setEnabled(false);
        cmb_sucursales.setEnabled(false);
    }

    
    public void accionesIniciar(){
        btn_iniciarViaje.setEnabled(false);
        btn_cancelarViaje.setEnabled(true);
        btn_finalizarViaje.setEnabled(true);
        tbl_viajes.setEnabled(true);
        lbl_transportistas.setEnabled(true);
        cmb_transportistas.setEnabled(true);
    }
    
    public void botonesPorDefecto(){
        btn_iniciarViaje.setEnabled(true);
        btn_cancelarViaje.setEnabled(false);
        btn_eliminarViaje.setEnabled(false);
        btn_finalizarViaje.setEnabled(false);
    }
    
    public void deleteAllRows(final DefaultTableModel model) {
    for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
        model.removeRow(i);
    }
}
    
    public void accionesCancelar(){
        txt_subtotal.setText("0");
        txt_isv.setText("0");
        txt_total.setText("0");
        txt_distanciaTotal.setText("");
        /*cmb_metodoPago.setSelectedItem("Seleccione el método");
        cmb_metodoPago.setEnabled(false);
        txt_cambio.setText("");
        cmb_cliente.setEnabled(true);
        cmb_cliente.setSelectedItem("1. Consumidor Final");
        habilitarNumeros(false);
        habilitarProductos(false);*/
        botonesPorDefecto();  
        DefaultTableModel model = (DefaultTableModel) tbl_viajes.getModel();
        deleteAllRows(model);
    }
    
    private int capturarIdSucursal(String nombreSucursal){
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_sucursal from sucursales where nombre_sucursal = '"+nombreSucursal+"'";
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                id = Integer.parseInt(rs.getString("id_sucursal"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
       return -1;
    }
    
    public int capturarIdColaborador(String numeroIdentidad){
        int id;
        try {
            Statement st = con.createStatement();
            String sql = "select id_colaborador from colaboradores where numero_identidad_colaborador = '"+numeroIdentidad+"'";
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                id = Integer.parseInt(rs.getString("id_colaborador"));
                return id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
       return -1;
    }
    
    public double llenarTarifa(String numeroIdentidadTransportista){
        try {
            Statement st = con.createStatement();
            String sql = "select ta.tarifa from transportistas as tr " +
                        "join tarifa as ta on tr.id_tarifa = ta.id_tarifa "
                        +"where tr.numero_identidad_transportista = '"+numeroIdentidadTransportista+"'";
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                tarifa = rs.getDouble("tarifa");
                return tarifa;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
       return 0.0;
    }
    
    public double llenarDistancia(int idColaborador, int idSucursal){
        try {
            Statement st = con.createStatement();
            String sql = "select distancia from asignaciones " +
                         "where id_colaborador = '"+idColaborador+"' and id_sucursal = '"+idSucursal+"'";
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                distancia = rs.getDouble("distancia");
                return distancia;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
       return 0.0;
    }
    
    public void guardarDetalle(){
        try { 
            PreparedStatement ps;
            ResultSet rs;    
            ps = con.prepareStatement("INSERT INTO factura_detalle (idfactura, idproducto, cantidadfactura, precio)"
                    + "VALUES(?,?,?,?)");
            //ps.setString(1, lbl_idFactura.getText());
            //ps.setString(2, lbl_idProducto.getText());
            int indiceUltimaFila = tbl_viajes.getRowCount() - 1;
            String cantidadProducto = tbl_viajes.getValueAt(indiceUltimaFila, 1).toString();
            String precio = tbl_viajes.getValueAt(indiceUltimaFila, 2).toString();
            ps.setString(3, cantidadProducto);
            ps.setString(4, precio);
            int res = ps.executeUpdate();
            if (res > 0) {  
                Statement st = con.createStatement();
                String sql = "Select top 1 * from factura_detalle order by iddetalle desc";
                ResultSet rs1 = st.executeQuery(sql);
                if (rs1.next()) {
                     //lbl_idDetalle.setText(rs1.getString("iddetalle"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void validacionPago(){
        /*if(cmb_metodoPago.getSelectedItem().equals("Seleccione el método")){
            JOptionPane.showMessageDialog(this, "Por favor seleccione el método de pago", "Seleccione el método", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(txt_distanciaTotal.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Por favor seleccione la cantidad de pago con los botones numéricos", "Pago en blanco", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(txt_cambio.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Por favor seleccione la cantidad de pago con los botones numéricos", "Cambio en blanco", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        double total = Double.parseDouble(txt_total.getText());
        double pago = Double.parseDouble(txt_distanciaTotal.getText());
        
        if(total > pago){
            Locale locale = new Locale("es", "HN");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            double resto = total - pago;
            String restoTotal = currencyFormatter.format(resto);
            JOptionPane.showMessageDialog(this, "Pago insuficiente, faltan: L "+restoTotal+" ", "Pago Insuficiente", JOptionPane.INFORMATION_MESSAGE);
            return;
        }*/
    }
    
    public void imprimirFactura(){
        /*try {
            JasperReport reporte = null;
            String path = "src\\reportes\\factura.jasper";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("numeroFactura",lbl_idFactura.getText());
            parameters.put("cai","35BD6A-0195F4-B34BAA-8B7D13-37791A-2D");
            reporte = (JasperReport) JRLoader.loadObjectFromFile(path);
            JasperPrint jprint;
            jprint=JasperFillManager.fillReport(reporte,parameters,con);
            JasperViewer view = new JasperViewer(jprint,false);
            final JRViewer viewer = new JRViewer(jprint);
            JRSaveContributor[] contrbs = viewer.getSaveContributors();

            for (JRSaveContributor saveContributor : contrbs)
            {
                if (!(saveContributor instanceof net.sf.jasperreports.view.save.JRDocxSaveContributor ||
                    saveContributor instanceof net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor
                    || saveContributor instanceof net.sf.jasperreports.view.save.JRPdfSaveContributor))
            viewer.removeSaveContributor(saveContributor);
        }
        view.setContentPane(viewer);
        view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        view.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }*/
    }
    
    public void actualizarTotal(){
       double isv = totalPrecioOrden * 0.15;
       
       double subtotal = totalPrecioOrden - isv;
       
       txt_total.setText(String.valueOf(totalPrecioOrden));
       txt_isv.setText(String.valueOf(isv));
       txt_subtotal.setText(String.valueOf(subtotal));
    }
    
    private void limpiarColaboradores(){
        cmb_colaboradores.removeAllItems();
        cmb_colaboradores.addItem("Seleccione al colaborador");
    }
    
    private void limpiarSucursales(){
        cmb_sucursales.removeAllItems();
        cmb_sucursales.addItem("Seleccione la sucursal");
    }
    
    
     private void limpiarTransportistas(){
        cmb_transportistas.removeAllItems();
        cmb_transportistas.addItem("Seleccione al transportista");
    }
    
    private void llenarColaboradoresPorSucursal(String nombreSucursal) {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarColaboradoresPorSucursal(nombreSucursal);
            for (int i = 0; i < lista.size(); i++) {
                cmb_colaboradores.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
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
        lbl_sucursal = new javax.swing.JLabel();
        cmb_sucursales = new javax.swing.JComboBox<>();
        lbl_colaborador = new javax.swing.JLabel();
        cmb_colaboradores = new javax.swing.JComboBox<>();
        lbl_distancia = new javax.swing.JLabel();
        txt_distancia = new javax.swing.JTextField();
        btn_agregarViaje = new javax.swing.JButton();
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
        btn_iniciarViaje = new javax.swing.JButton();
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

        lbl_sucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/ubicacion.png"))); // NOI18N
        lbl_sucursal.setEnabled(false);
        lbl_sucursal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_sucursalMousePressed(evt);
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

        lbl_colaborador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/name.png"))); // NOI18N
        lbl_colaborador.setEnabled(false);
        lbl_colaborador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_colaboradorMousePressed(evt);
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

        btn_agregarViaje.setBackground(new java.awt.Color(205, 63, 145));
        btn_agregarViaje.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_agregarViaje.setForeground(new java.awt.Color(255, 255, 255));
        btn_agregarViaje.setText("Agregar Viaje");
        btn_agregarViaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_agregarViaje.setEnabled(false);
        btn_agregarViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_agregarViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_agregarViajeMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_agregarViajeMousePressed(evt);
            }
        });
        btn_agregarViaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregarViajeActionPerformed(evt);
            }
        });

        lbl_transportistas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/bus.png"))); // NOI18N
        lbl_transportistas.setEnabled(false);
        lbl_transportistas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_transportistasMousePressed(evt);
            }
        });

        cmb_transportistas.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_transportistas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione al transportista" }));
        cmb_transportistas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_transportistas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_transportistas.setEnabled(false);
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
                .addGap(41, 41, 41)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpn_productosLayout.createSequentialGroup()
                        .addComponent(lbl_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpn_productosLayout.createSequentialGroup()
                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpn_productosLayout.createSequentialGroup()
                        .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_agregarViaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_distancia, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jpn_productosLayout.setVerticalGroup(
            jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_productosLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_colaboradores, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(btn_agregarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        tbl_viajes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre del Producto", "Cantidad", "Precio", "ID"
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
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_subtotal))
                .addGap(15, 15, 15)
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_isv, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_isv))
                .addGap(18, 18, 18)
                .addGroup(jpn_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_total))
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
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_tarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_tarifa))
                .addGap(29, 29, 29)
                .addGroup(jpn_tarifaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_distanciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_distanciaTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        btn_iniciarViaje.setBackground(new java.awt.Color(205, 63, 145));
        btn_iniciarViaje.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        btn_iniciarViaje.setForeground(new java.awt.Color(255, 255, 255));
        btn_iniciarViaje.setText("Iniciar");
        btn_iniciarViaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_iniciarViaje.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_iniciarViajeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_iniciarViajeMouseExited(evt);
            }
        });
        btn_iniciarViaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_iniciarViajeActionPerformed(evt);
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
                    .addComponent(btn_iniciarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(btn_iniciarViaje, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jpn_principalLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lbl_tituloViajes))
                                    .addComponent(jsp_tabla))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                                .addGap(21, 21, 21)
                                .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_tituloViajes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
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

    private void lbl_homeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_homeMousePressed
        Principal principal = new Principal(lbl_nombreUsuario.getText());
        this.dispose();
        principal.setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_homeMousePressed

    private void btn_iniciarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_iniciarViajeMouseEntered
        btn_iniciarViaje.setBackground(new Color(156, 2, 91));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_iniciarViajeMouseEntered

    private void btn_iniciarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_iniciarViajeMouseExited
        btn_iniciarViaje.setBackground(new Color(205, 63, 145));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_iniciarViajeMouseExited

    private void btn_iniciarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_iniciarViajeActionPerformed
        btn_iniciarViaje.setBackground(new Color(40, 74, 172));
        accionesIniciar();
        /*try {
            Calendar f;
            f = Calendar.getInstance();
            int d = f.get(Calendar.DATE), mes = 1 + (f.get(Calendar.MONTH)), año = f.get(Calendar.YEAR);
            String fecha = (año + "-" + mes + "-" + d);
            PreparedStatement ps;
            ResultSet rs;
            String cai = "35BD6A-0195F4-B34BAA-8B7D13-37791A-2D";
            int totalInicial = 0;
            int cambioInicial = 0;
            int pagoInicial = 0;
            ps = con.prepareStatement("INSERT INTO factura_encabezado (cai, idempleado, totalfactura, idcliente,"
                    + "fecha_factura,cambio_factura,pago_factura)"
                    + "VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, cai);
            String idEmpleado = capturarIdEmpleado();
            //String idCliente = lbl_idCliente.getText();
            ps.setString(2, idEmpleado);
            ps.setString(3, String.valueOf(totalInicial));
            //ps.setString(4, idCliente);
            ps.setString(5, fecha);
            ps.setString(6, String.valueOf(cambioInicial));
            ps.setString(7, String.valueOf(pagoInicial));
            int res = ps.executeUpdate();
            if (res > 0) {
                capturarIdFactura();
                accionesIniciar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }*/
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_iniciarViajeActionPerformed

    private void btn_finalizarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_finalizarViajeActionPerformed
        btn_finalizarViaje.setBackground(new Color(40, 74, 172));
        try{
           validacionPago(); 
           PreparedStatement ps;
            ResultSet rs;
            ps = con.prepareStatement("Update factura_encabezado\n" +
                                      "set totalfactura = ?,\n" +
                                      "cambio_factura = ?, \n" +
                                      "pago_factura =? "+
                                      "where idfactura = ?");
            ps.setString(1, txt_total.getText());
            //String cambio = txt_cambio.getText().substring(1);
            //ps.setString(2, cambio);
            ps.setString(3, txt_distanciaTotal.getText());
            //ps.setString(4, lbl_idFactura.getText());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Factura pagada");
                imprimirFactura();
                accionesCancelar();
            }
        }catch(Exception e){
            
        }
        
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_finalizarViajeActionPerformed

    private void btn_cancelarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarViajeActionPerformed
        btn_cancelarViaje.setBackground(new Color(40, 74, 172));
        try {
                PreparedStatement ps;
                ps = con.prepareStatement("Delete factura_detalle\n"
                        + "where idfactura =?");   
                //ps.setString(1, lbl_idFactura.getText());
                
                PreparedStatement ps2;
                ps2 = con.prepareStatement("Delete factura_encabezado\n"
                        + "where idfactura =?");   
                //ps2.setString(1, lbl_idFactura.getText());
                
                JOptionPane.showMessageDialog(this, "Factura cancelada");
                accionesCancelar();  

            } catch (Exception e) {

            }
       
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cancelarViajeActionPerformed

    private void btn_finalizarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_finalizarViajeMouseEntered
        btn_finalizarViaje.setBackground(new Color(156, 2, 91));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_finalizarViajeMouseEntered

    private void btn_finalizarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_finalizarViajeMouseExited
        btn_finalizarViaje.setBackground(new Color(205, 63, 145));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_finalizarViajeMouseExited

    private void btn_cancelarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarViajeMouseEntered
        btn_cancelarViaje.setBackground(new Color(156, 2, 91));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cancelarViajeMouseEntered

    private void btn_cancelarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarViajeMouseExited
        btn_cancelarViaje.setBackground(new Color(205, 63, 145));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cancelarViajeMouseExited

    private void btn_eliminarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_eliminarViajeMouseEntered
        btn_eliminarViaje.setBackground(new Color(156, 2, 91));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_eliminarViajeMouseEntered

    private void btn_eliminarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_eliminarViajeMouseExited
        btn_eliminarViaje.setBackground(new Color(205, 63, 145));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_eliminarViajeMouseExited

    private void btn_eliminarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarViajeActionPerformed
        btn_eliminarViaje.setBackground(new Color(40, 74, 172));
            try {
                PreparedStatement ps;
                ResultSet rs;
                ps = con.prepareStatement("Delete factura_detalle\n"
                        + "where iddetalle =?");   
                String idDetalle = tbl_viajes.getValueAt(filaSeleccionada, 3).toString();
                ps.setString(1, idDetalle);
                int res = ps.executeUpdate();
                if (res > 0) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado");
                    DefaultTableModel model = (DefaultTableModel) tbl_viajes.getModel();
                    model.removeRow(filaSeleccionada);
                    int totalFilas = tbl_viajes.getRowCount();
                    totalPrecioOrden = 0;
                    for (int i = 0; i < totalFilas; i++) {
                        totalPrecioOrden += Double.parseDouble(tbl_viajes.getValueAt(i, 2).toString()); 
                    }
                    
                    actualizarTotal();
                    btn_eliminarViaje.setEnabled(false);
                }

            } catch (Exception e) {

            }

        // TODO add your handling code here:
    }//GEN-LAST:event_btn_eliminarViajeActionPerformed

    private void tbl_viajesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_viajesMouseClicked
        btn_eliminarViaje.setEnabled(true);
        filaSeleccionada = tbl_viajes.getSelectedRow();
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_viajesMouseClicked

    private void lbl_sucursalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_sucursalMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_sucursalMousePressed

    private void cmb_sucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_sucursalesActionPerformed
        if(cmb_sucursales.getItemCount() ==0){
            return;
        }
        
        if(!cmb_sucursales.getSelectedItem().toString().equals("Seleccione la sucursal")){
            limpiarColaboradores();
            String informacionSucursal = cmb_sucursales.getSelectedItem().toString();
            String reemplazarInformacionSucursal = informacionSucursal.replace("(", "|");
            String informacionSucursalFormateada = reemplazarInformacionSucursal.replace(")", "|");
            String [] partesInformacionSucursal = informacionSucursalFormateada.split("\\|");
            String nombreSucursal = partesInformacionSucursal[0].trim();
            llenarColaboradoresPorSucursal(nombreSucursal);
            cmb_colaboradores.setEnabled(true);
            id_sucursal = capturarIdSucursal(nombreSucursal);
        }
    }//GEN-LAST:event_cmb_sucursalesActionPerformed

    private void lbl_colaboradorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_colaboradorMousePressed
        cmb_colaboradores.requestFocus();
    }//GEN-LAST:event_lbl_colaboradorMousePressed

    private void cmb_colaboradoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_colaboradoresActionPerformed
        if(cmb_colaboradores.getItemCount() ==0){
            return;
        }
        if(!cmb_colaboradores.getSelectedItem().toString().equals("Seleccione al colaborador")){
            String informacionColaboradores = cmb_colaboradores.getSelectedItem().toString();
            String [] partesInformacionSucursal = informacionColaboradores.split("\\|");
            String numeroIdentidad = partesInformacionSucursal[2].trim();
            id_colaboradorRegistrado = capturarIdColaborador(numeroIdentidad);
            distancia = llenarDistancia(id_colaboradorRegistrado,id_sucursal);
            txt_distancia.setText(Double.toString(distancia));
            
            String informacionTransportista = cmb_transportistas.getSelectedItem().toString();
            String reemplazarInformacionTransportistas = informacionTransportista.replace("(", "|");
            String informacionTransportistaFormateada = reemplazarInformacionTransportistas.replace(")", "|");
            String [] partesInformacionTransportista = informacionTransportistaFormateada.split("\\|");
            String numeroIdentidadTransportista = partesInformacionTransportista[1].trim();
            tarifa = llenarTarifa(numeroIdentidadTransportista);
            txt_tarifa.setText(Double.toString(tarifa));
            total = distancia * tarifa;
            String totalFormateado = String.format("%.2f", total);
            txt_total.setText(totalFormateado);
        }
        else{
            txt_distanciaTotal.setEnabled(false);
        }
    }//GEN-LAST:event_cmb_colaboradoresActionPerformed

    private void lbl_transportistasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_transportistasMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_transportistasMousePressed

    private void lbl_distanciaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_distanciaMousePressed
        txt_distancia.requestFocus();
    }//GEN-LAST:event_lbl_distanciaMousePressed

    private void txt_distanciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_distanciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaActionPerformed

    private void txt_distanciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_distanciaKeyTyped
        char a=evt.getKeyChar();

        if(evt.getKeyChar() == 46){
            if(txt_distancia.getText().equals("")){
                evt.consume();
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            if(txt_distancia.getText().contains(".")){
                evt.consume();
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        if(txt_distancia.getText().length() >=5){
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Número máximo de dígitos admitidos","Límite de entrada",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (evt.getKeyChar() == 8 || evt.getKeyChar() == 127 ||
            evt.getKeyChar() == 0 || evt.getKeyChar() == 3 || evt.getKeyChar() == 22
            || evt.getKeyChar() == 26 || evt.getKeyChar() == 24 || evt.getKeyChar() == 44 || evt.getKeyChar() == 46) {
            return;
        }

        if(Character.isLetter(a) || !Character.isLetterOrDigit(a)){
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Sólo numeros","Restricción de entrada",JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaKeyTyped

    private void btn_agregarViajeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarViajeMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarViajeMouseEntered

    private void btn_agregarViajeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarViajeMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarViajeMouseExited

    private void btn_agregarViajeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_agregarViajeMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarViajeMousePressed

    private void btn_agregarViajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregarViajeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_agregarViajeActionPerformed

    private void cmb_transportistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_transportistasActionPerformed
       
        if(!cmb_transportistas.getSelectedItem().toString().equals("Seleccione al transportista")){
            limpiarSucursales();
            llenarSucursales();
            cmb_sucursales.setEnabled(true);
        }else{
           restablecer();
        }
    }//GEN-LAST:event_cmb_transportistasActionPerformed

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
    private javax.swing.JButton btn_agregarViaje;
    private javax.swing.JButton btn_cancelarViaje;
    private javax.swing.JButton btn_eliminarViaje;
    private javax.swing.JButton btn_finalizarViaje;
    private javax.swing.JButton btn_iniciarViaje;
    private javax.swing.JComboBox<String> cmb_colaboradores;
    private javax.swing.JComboBox<String> cmb_sucursales;
    private javax.swing.JComboBox<String> cmb_transportistas;
    private javax.swing.JPanel jpn_acciones;
    private javax.swing.JPanel jpn_principal;
    private javax.swing.JPanel jpn_productos;
    private javax.swing.JPanel jpn_tarifa;
    private javax.swing.JPanel jpn_total;
    private javax.swing.JScrollPane jsp_tabla;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel lbl_colaborador;
    private javax.swing.JLabel lbl_distancia;
    private javax.swing.JLabel lbl_distanciaTotal;
    private javax.swing.JLabel lbl_home;
    private javax.swing.JLabel lbl_isv;
    private javax.swing.JLabel lbl_nombreUsuario;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_sucursal;
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
