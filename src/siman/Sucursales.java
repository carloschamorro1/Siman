/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siman;

import bd.ConexionBD;
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
import utilidades.Queries;

/**
 *
 * @author Carlos
 */
public class Sucursales extends javax.swing.JFrame {

    Statement stmt = null;
    Connection con = null;
    ArrayList<String> colaboradores = new ArrayList<String>();
    int id_colaborador;

    /**
     * Creates new form Sucursales
     */
    public Sucursales(String nombreUsuario) throws SQLException {
        initComponents();
        informacionGeneral();
        holders();
        llenarSucursales();
        lbl_nombreUsuario.setText(nombreUsuario);
        this.con = ConexionBD.obtenerConexion();
        //((JSpinner.DefaultEditor) spi_cantidadProducto.getEditor()).getTextField().setEditable(false);
    }

    public Sucursales() throws SQLException {
        initComponents();
        informacionGeneral();
        holders();
        llenarSucursales();
        lbl_nombreUsuario.setText("nombreUsuario");
        this.con = ConexionBD.obtenerConexion();
    }

    private void informacionGeneral() {
        this.setTitle("Inventario");
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("../Img/icono.png")).getImage());
    }

    private void holders() {
        PlaceHolder holder;
        holder = new PlaceHolder(txt_distancia, Color.gray, Color.black, "Ingrese la distancia", false, "Roboto", 25);
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
    
    private void limpiarColaboradores(){
        cmb_colaboradores.removeAllItems();
        cmb_colaboradores.addItem("Seleccione al colaborador");
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
    
    private void llenarColaboradores(String nombreSucursal) {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarColaboradores(nombreSucursal);
            for (int i = 0; i < lista.size(); i++) {
                cmb_colaboradores.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    
    private void rellenar() {
        String input = "";
        input = JOptionPane.showInputDialog(this, "¿Qué producto desea buscar?", "Consulta de producto", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            JOptionPane.showMessageDialog(this, "La acción fue cancelada", "¡AVISO!", JOptionPane.INFORMATION_MESSAGE);
        } else if (input.equals("")) {
            JOptionPane.showMessageDialog(this, "Favor de ingresar los datos del producto\n que desea buscar", "¡AVISO!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String sql = "select * from inventario\n"
                    + "where nombreproducto ='" + input + "'";
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    //txt_nombreProducto.setText(rs.getString("nombreproducto"));
                    int cantidad = Integer.parseInt(rs.getString("cantidadstock"));
                    //spi_cantidadProducto.setValue(cantidad);
                    String tipoMovimiento = rs.getString("tipomovimiento");
                    if (tipoMovimiento.equals("i")) {
                        cmb_colaboradores.setSelectedItem("Ingreso");
                    }
                    if (tipoMovimiento.equals("r")) {
                        cmb_colaboradores.setSelectedItem("Retiro");
                    }
                    txt_distancia.setText(rs.getString("precio"));
                    colorear();
                    habilitarAccionesBuscar();
                } else {
                    JOptionPane.showMessageDialog(null, "¡No se encuentra el producto! Por favor verifique sí, lo escribio correctamente");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void colorear() {
        cmb_sucursales.setForeground(Color.black);
        cmb_colaboradores.setForeground(Color.black);
        txt_distancia.setForeground(Color.black);
    }

    private void habilitarAccionesBuscar() {
        btn_asignar.setEnabled(false);
    }

    private boolean estaVacio() {
        /*if (txt_nombreProducto.getText().equals("Ingrese el nombre del producto")) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del producto", "Ingrese el nombre", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        if (spi_cantidadProducto.getValue().equals("0")) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese la cantidad del producto", "Ingrese la cantidad", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        if (cmb_colaborador.getSelectedItem().equals("Seleccione el movimiento")) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione el tipo de movimiento", "Seleccione el tipo de movimiento", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        if (txt_distancia.getText().equals("Ingrese el precio del producto")) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el precio del producto", "Ingrese el precio", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }*/
        return false;
    }

    private void limpiar() {
        cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
        cmb_sucursales.setSelectedItem("Seleccione la sucursal");
        txt_distancia.setText("");
    }

    private boolean existeProducto() {
        /*try {
            Statement st = con.createStatement();
            String sql = "Select nombreproducto from inventario where nombreproducto = '" + txt_nombreProducto.getText() + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "El Producto: " + txt_nombreProducto.getText() + " ya existe", "Este producto ¡Ya existe!", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return false;
    }

    private void restablecer() {
        limpiar();
        holders();
        btn_asignar.setEnabled(true);
    }

    private String capturarIdEmpleado() {
        String idEmpleado = "";
        try {
            Statement st = con.createStatement();
            String sql = "Select idempleado from empleado where usuario = '" + lbl_nombreUsuario.getText() + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                idEmpleado = rs.getString("idempleado");
                return idEmpleado;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idEmpleado;
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
        jPanel1 = new javax.swing.JPanel();
        txt_distancia = new javax.swing.JTextField();
        lbl_nombreUsuario = new javax.swing.JLabel();
        btn_asignar = new javax.swing.JButton();
        lbl_home = new javax.swing.JLabel();
        lbl_usuario = new javax.swing.JLabel();
        lbl_tituloSucursales = new javax.swing.JLabel();
        lbl_sucursal = new javax.swing.JLabel();
        lbl_distancia = new javax.swing.JLabel();
        lbl_colaborador = new javax.swing.JLabel();
        cmb_colaboradores = new javax.swing.JComboBox<>();
        cmb_sucursales = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        kGradientPanel1.setkEndColor(new java.awt.Color(40, 74, 172));
        kGradientPanel1.setkStartColor(new java.awt.Color(205, 63, 145));
        kGradientPanel1.setkTransparentControls(false);
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(1319, 821));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txt_distancia.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
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

        lbl_nombreUsuario.setFont(new java.awt.Font("Roboto Black", 0, 24)); // NOI18N
        lbl_nombreUsuario.setText("Nombre de Usuario");

        btn_asignar.setBackground(new java.awt.Color(205, 63, 145));
        btn_asignar.setFont(new java.awt.Font("Roboto Black", 0, 24)); // NOI18N
        btn_asignar.setForeground(new java.awt.Color(255, 255, 255));
        btn_asignar.setText("Agregar");
        btn_asignar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_asignar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_asignarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_asignarMouseExited(evt);
            }
        });
        btn_asignar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_asignarActionPerformed(evt);
            }
        });

        lbl_home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/home.png"))); // NOI18N
        lbl_home.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_homeMousePressed(evt);
            }
        });

        lbl_usuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/profile.png"))); // NOI18N
        lbl_usuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lbl_tituloSucursales.setFont(new java.awt.Font("Roboto Black", 0, 48)); // NOI18N
        lbl_tituloSucursales.setText("Sucursales");

        lbl_sucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/ubicacion.png"))); // NOI18N
        lbl_sucursal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_sucursalMousePressed(evt);
            }
        });

        lbl_distancia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/distancia.png"))); // NOI18N
        lbl_distancia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_distanciaMousePressed(evt);
            }
        });

        lbl_colaborador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/name.png"))); // NOI18N
        lbl_colaborador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_colaboradorMousePressed(evt);
            }
        });

        cmb_colaboradores.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_colaboradores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione al colaborador" }));
        cmb_colaboradores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_colaboradores.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_colaboradores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_colaboradoresActionPerformed(evt);
            }
        });

        cmb_sucursales.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_sucursales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione la sucursal" }));
        cmb_sucursales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_sucursales.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_sucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_sucursalesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_usuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_nombreUsuario)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(lbl_tituloSucursales)
                        .addGap(0, 609, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_home)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_asignar, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lbl_tituloSucursales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(74, 74, 74))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lbl_nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addComponent(btn_asignar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(205, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1395, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_distanciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_distanciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaActionPerformed

    private void btn_asignarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_asignarMouseEntered
        btn_asignar.setBackground(new Color(156, 2, 91));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_asignarMouseEntered

    private void btn_asignarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_asignarMouseExited
        btn_asignar.setBackground(new Color(205, 63, 145));
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_asignarMouseExited

    private void lbl_homeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_homeMousePressed
        Principal principal = new Principal(lbl_nombreUsuario.getText());
        this.dispose();
        principal.setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_homeMousePressed

    private void lbl_sucursalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_sucursalMousePressed
        cmb_sucursales.requestFocus();
    }//GEN-LAST:event_lbl_sucursalMousePressed

    private void lbl_distanciaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_distanciaMousePressed
        txt_distancia.requestFocus();
    }//GEN-LAST:event_lbl_distanciaMousePressed

    private void lbl_colaboradorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_colaboradorMousePressed
        cmb_colaboradores.requestFocus();
    }//GEN-LAST:event_lbl_colaboradorMousePressed

    private void btn_asignarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_asignarActionPerformed
        btn_asignar.setBackground(new Color(40, 74, 172));
        try {
            if (estaVacio()) {
                return;
            }

            if (existeProducto()) {
                return;
            }

            Calendar f;
            f = Calendar.getInstance();
            int d = f.get(Calendar.DATE), mes = 1 + (f.get(Calendar.MONTH)), año = f.get(Calendar.YEAR);
            String fecha = (año + "-" + mes + "-" + d);

            PreparedStatement ps;
            ResultSet rs;

            ps = con.prepareStatement("INSERT INTO inventario (nombreproducto, idempleado, cantidadstock, fechaintroduccion,"
                    + "tipomovimiento, precio)"
                    + "VALUES(?,?,?,?,?,?)");
            //ps.setString(1, txt_nombreProducto.getText());
            String idEmpleado = capturarIdEmpleado();
            ps.setString(2, idEmpleado);
            //ps.setString(3, spi_cantidadProducto.getValue().toString());
            ps.setString(4, fecha);
            ps.setString(5, cmb_colaboradores.getSelectedItem().toString().substring(0, 1).toLowerCase());
            ps.setString(6, txt_distancia.getText());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Producto agregado");
                restablecer();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_btn_asignarActionPerformed

    private void txt_distanciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_distanciaKeyTyped
        char a=evt.getKeyChar();
        if (evt.getKeyChar() == 8 || evt.getKeyChar() == 127 ||
            evt.getKeyChar() == 0 || evt.getKeyChar() == 3 || evt.getKeyChar() == 22
            || evt.getKeyChar() == 26 || evt.getKeyChar() == 24 || evt.getKeyChar() == 46 || evt.getKeyChar() == 44) {
            return;
        }

        if(txt_distancia.getText().length() >=10){
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Número máximo de dígitos admitidos");
        }
        if(Character.isLetter(a) || !Character.isLetterOrDigit(a)){
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Sólo numeros");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_distanciaKeyTyped

    private void cmb_sucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_sucursalesActionPerformed
        limpiarColaboradores();
        if(!cmb_sucursales.getSelectedItem().toString().equals("Seleccione la sucursal")){    
                String informacionSucursal = cmb_sucursales.getSelectedItem().toString();
                String reemplazarInformacionSucursal = informacionSucursal.replace("(", "|");
                String informacionSucursalFormateada = reemplazarInformacionSucursal.replace(")", "|");
                String [] partesInformacionSucursal = informacionSucursalFormateada.split("\\|");
                String nombreSucursal = partesInformacionSucursal[0].trim();
                llenarColaboradores(nombreSucursal);  
            }
    }//GEN-LAST:event_cmb_sucursalesActionPerformed

    private void cmb_colaboradoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_colaboradoresActionPerformed
        if(cmb_colaboradores.getItemCount() ==0){
            return;
        }
        if(!cmb_colaboradores.getSelectedItem().toString().equals("Seleccione al colaborador")){    
            String informacionColaboradores = cmb_colaboradores.getSelectedItem().toString();
            String [] partesInformacionSucursal = informacionColaboradores.split("\\|");
            String numeroIdentidad = partesInformacionSucursal[2].trim();
            id_colaborador = capturarIdColaborador(numeroIdentidad);
            }
    }//GEN-LAST:event_cmb_colaboradoresActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sucursales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sucursales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sucursales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sucursales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Sucursales().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Sucursales.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_asignar;
    private javax.swing.JComboBox<String> cmb_colaboradores;
    private javax.swing.JComboBox<String> cmb_sucursales;
    private javax.swing.JPanel jPanel1;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel lbl_colaborador;
    private javax.swing.JLabel lbl_distancia;
    private javax.swing.JLabel lbl_home;
    private javax.swing.JLabel lbl_nombreUsuario;
    private javax.swing.JLabel lbl_sucursal;
    private javax.swing.JLabel lbl_tituloSucursales;
    private javax.swing.JLabel lbl_usuario;
    private javax.swing.JTextField txt_distancia;
    // End of variables declaration//GEN-END:variables
}
