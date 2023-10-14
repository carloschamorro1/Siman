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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import utilidades.Queries;

/**
 *
 * @author Carlos
 */
public class Asignaciones extends javax.swing.JFrame {

    Statement stmt = null;
    Connection con = null;
    ArrayList<String> colaboradores = new ArrayList<String>();
    int idColaborador;
    int idSucursal;
    int cifrasDecimales;
    String nombreColaborador;
    String nombreSucursal;
    int idColaboradorActivo;

    /**
     * Creates new form Asignaciones
     *
     * @param nombreUsuario
     * @param idColaborador
     * @throws java.sql.SQLException
     */
    public Asignaciones(String nombreUsuario, int idColaborador) throws SQLException {
        initComponents();
        informacionGeneral();
        holders();
        llenarSucursales();
        desactivarCampos();
        lbl_nombreUsuario.setText(nombreUsuario);
        this.con = ConexionBD.obtenerConexion();
        this.idColaboradorActivo = idColaborador;
    }

    public Asignaciones() throws SQLException {
        initComponents();
        informacionGeneral();
        holders();
        llenarSucursales();
        desactivarCampos();
        this.con = ConexionBD.obtenerConexion();
    }

    public void desactivarCampos() {
        cmb_colaboradores.setEnabled(false);
        txt_distancia.setEnabled(false);
        btn_asignar.setEnabled(false);
    }

    public void activarCampos() {
        cmb_colaboradores.setEnabled(true);
        txt_distancia.setEnabled(true);
        btn_asignar.setEnabled(true);
    }

    public void activarCamposSinDistancia() {
        cmb_colaboradores.setEnabled(true);
        btn_asignar.setEnabled(true);

    }

    private void informacionGeneral() {
        this.setTitle("Sucursales");
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
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiarColaboradores() {
        cmb_colaboradores.removeAllItems();
        cmb_colaboradores.addItem("Seleccione al colaborador");
    }

    public int capturarIdColaborador(String numeroIdentidad) {
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

    private String capturarNombreSucursal(int id_sucursal) {
        try {
            Statement st = con.createStatement();
            String sql = "select nombre_sucursal from sucursales where id_sucursal = '" + id_sucursal + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                nombreSucursal = rs.getString("nombre_sucursal");
                return nombreSucursal;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
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

    private void llenarColaboradores() {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            lista = new Queries().llenarColaboradores();
            for (int i = 0; i < lista.size(); i++) {
                cmb_colaboradores.addItem(lista.get(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean estaVacio() {
        if (txt_distancia.getText().equals("Ingrese la distancia")) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese la distancia", "Ingrese la distancia", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    private boolean validarDistancia() {
        if (Double.parseDouble(txt_distancia.getText()) == 0) {
            JOptionPane.showMessageDialog(this, "La distancia no puede ser cero (0).", "Cero no permitido", JOptionPane.ERROR_MESSAGE);
            return true;
        } else if (Double.parseDouble(txt_distancia.getText()) > 50) {
            JOptionPane.showMessageDialog(this, "La distancia no puede ser mayor a cincuenta (50).", "Máximo de la distancia", JOptionPane.ERROR_MESSAGE);
            txt_distancia.setText("");
            txt_distancia.requestFocus();
            return true;
        }
        return false;
    }

    private void limpiar() {
        cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
        cmb_sucursales.setSelectedItem("Seleccione la sucursal");
        txt_distancia.setText("");
    }

    private void restablecer() {
        limpiar();
        holders();
        desactivarCampos();
    }

    public boolean sucursalYaAsignadaAColaborador() {
        try {
            Statement st = con.createStatement();
            String sql = "select * from asignaciones where id_colaborador = '" + idColaborador + "' and id_sucursal = '" + idSucursal + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "El colaborador ya tiene asignada esta sucursal", "Sucursal ya asignada", JOptionPane.INFORMATION_MESSAGE);
                txt_distancia.setText("");
                txt_distancia.setEnabled(false);
                cmb_colaboradores.setSelectedItem("Seleccione al colaborador");
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
        lbl_limpiar = new javax.swing.JLabel();
        lbl_distancia = new javax.swing.JLabel();
        lbl_colaborador = new javax.swing.JLabel();
        cmb_colaboradores = new javax.swing.JComboBox<>();
        cmb_sucursales = new javax.swing.JComboBox<>();
        lbl_sucursal1 = new javax.swing.JLabel();

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
        btn_asignar.setText("Asignar");
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

        lbl_limpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/clean.png"))); // NOI18N
        lbl_limpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_limpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_limpiarMouseClicked(evt);
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

        lbl_sucursal1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/ubicacion.png"))); // NOI18N
        lbl_sucursal1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_sucursal1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lbl_usuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_nombreUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_home)
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(194, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_sucursal1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lbl_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_asignar, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(161, 161, 161))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl_tituloSucursales)
                        .addGap(317, 317, 317))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lbl_nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lbl_tituloSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_sucursal1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_colaborador, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_colaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_distancia, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbl_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(btn_asignar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap(274, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(238, 238, 238))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
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
        try {
            this.dispose();
            Principal principal;
            principal = new Principal(lbl_nombreUsuario.getText(), idColaboradorActivo);
            principal.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_homeMousePressed

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

            if (validarDistancia()) {
                return;
            }

            if (sucursalYaAsignadaAColaborador()) {
                return;
            }

            PreparedStatement ps;
            ResultSet rs;
            ps = con.prepareStatement("INSERT INTO asignaciones (id_colaborador, id_sucursal, distancia) "
                    + "VALUES(?,?,?)");
            ps.setInt(1, idColaborador);
            ps.setInt(2, idSucursal);
            ps.setDouble(3, Double.parseDouble(txt_distancia.getText().trim()));
            int res = ps.executeUpdate();
            if (res > 0) {
                nombreColaborador = capturarNombreColaborador(idColaborador);
                nombreSucursal = capturarNombreSucursal(idSucursal);
                JOptionPane.showMessageDialog(this, "Se ha asigando la distancia: " + txt_distancia.getText().trim()
                        + "km a el/la empleado/a: " + nombreColaborador + " en la sucursal: " + nombreSucursal + ".");
                restablecer();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_asignarActionPerformed

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
    }//GEN-LAST:event_txt_distanciaKeyTyped

    private void cmb_sucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_sucursalesActionPerformed
        limpiarColaboradores();

        if (!cmb_sucursales.getSelectedItem().toString().equals("Seleccione la sucursal")) {
            activarCamposSinDistancia();
            String informacionSucursal = cmb_sucursales.getSelectedItem().toString();
            String reemplazarInformacionSucursal = informacionSucursal.replace("(", "|");
            String informacionSucursalFormateada = reemplazarInformacionSucursal.replace(")", "|");
            String[] partesInformacionSucursal = informacionSucursalFormateada.split("\\|");
            String nombreSucursal = partesInformacionSucursal[0].trim();
            llenarColaboradores();
            idSucursal = capturarIdSucursal(nombreSucursal);
        } else {
            restablecer();
        }
    }//GEN-LAST:event_cmb_sucursalesActionPerformed

    private void cmb_colaboradoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_colaboradoresActionPerformed
        if (cmb_colaboradores.getItemCount() == 0) {
            return;
        }
        if (!cmb_colaboradores.getSelectedItem().toString().equals("Seleccione al colaborador")) {
            txt_distancia.setEnabled(true);
            String informacionColaboradores = cmb_colaboradores.getSelectedItem().toString();
            String[] partesInformacionSucursal = informacionColaboradores.split("\\|");
            String numeroIdentidad = partesInformacionSucursal[1].trim();
            idColaborador = capturarIdColaborador(numeroIdentidad);
        } else {
            txt_distancia.setEnabled(false);
        }
    }//GEN-LAST:event_cmb_colaboradoresActionPerformed

    private void lbl_sucursal1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_sucursal1MousePressed
        cmb_sucursales.requestFocus();
    }//GEN-LAST:event_lbl_sucursal1MousePressed

    private void lbl_limpiarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_limpiarMouseClicked
        restablecer();
    }//GEN-LAST:event_lbl_limpiarMouseClicked

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
            java.util.logging.Logger.getLogger(Asignaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Asignaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Asignaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Asignaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Asignaciones().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
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
    private javax.swing.JLabel lbl_limpiar;
    private javax.swing.JLabel lbl_nombreUsuario;
    private javax.swing.JLabel lbl_sucursal1;
    private javax.swing.JLabel lbl_tituloSucursales;
    private javax.swing.JLabel lbl_usuario;
    private javax.swing.JTextField txt_distancia;
    // End of variables declaration//GEN-END:variables
}
