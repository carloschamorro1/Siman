/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siman;

import bd.ConexionBD;
import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
public class Reportes extends javax.swing.JFrame {

    Statement stmt = null;
    Connection con = null;
    ArrayList<String> colaboradores = new ArrayList<String>();
    int idTransportista;
    String numeroIdentidadTransportista;
    String fechaInicial;
    String fechaFinal;
    String nombreTransportista;
    String fechaInicialFormateada;
    String fechaFinalFormateada;
    int idColaboradorActivo;

    /**
     * Creates new form Reportes
     *
     * @param nombreUsuario
     * @param idColaborador
     * @throws java.sql.SQLException
     */
    public Reportes(String nombreUsuario, int idColaborador) throws SQLException {
        initComponents();
        informacionGeneral();
        lbl_nombreUsuario.setText(nombreUsuario);
        this.con = ConexionBD.obtenerConexion();
        this.idColaboradorActivo = idColaborador;
        llenarTransportistas();
    }

    public Reportes() throws SQLException {
        initComponents();
        informacionGeneral();
        lbl_nombreUsuario.setText("carlos.chamorro");
        this.idColaboradorActivo = 1;
        this.con = ConexionBD.obtenerConexion();
        llenarTransportistas();
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

    private void restablecer() {
        cmb_transportistas.setSelectedItem("Seleccione al transportista");
        jdt_fechaInicial.setCalendar(null);
        jdt_fechaFinal.setCalendar(null);
    }

    private void informacionGeneral() {
        this.setTitle("Sucursales");
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("../Img/icono.png")).getImage());
    }

    private void validarDatosRellenados() {
        if (cmb_transportistas.getSelectedItem().toString().equals("Seleccione al transportista")) {
            JOptionPane.showMessageDialog(null, "Por favor seleccione un transportista", "Transportista necesario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (jdt_fechaInicial.getCalendar() == null) {
            JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha inicial", "Fecha inicial necesaria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (jdt_fechaFinal.getCalendar() == null) {
            JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha final", "Fecha final necesaria", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void validarFechas() {
        if (jdt_fechaInicial.getCalendar() == null || jdt_fechaFinal.getCalendar() == null) {
            return;
        }
        if (jdt_fechaInicial.getDate().equals(jdt_fechaFinal.getDate())) {
            return;
        }
        if (!jdt_fechaInicial.getDate().before(jdt_fechaFinal.getDate())) {
            JOptionPane.showMessageDialog(null, "La fecha final no puede ser antes que la inicial", "Inconsistencia de fechas", JOptionPane.WARNING_MESSAGE);
        }
    }

    public double calcularTotal(int idTransportista, String fechaInicial, String fechaFinal) {
        double total = 0;
        try {
            Statement st = con.createStatement();
            String sql = "select total_viaje from viajes_encabezado as ve "
                    + "where id_transportista = '" + idTransportista + "' and fecha_viaje between '" + fechaInicial + "' and '" + fechaFinal + "';";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                total += rs.getDouble("total_viaje");
            }
            return total;
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public double calcularDistanciaTotal(int idTransportista, String fechaInicial, String fechaFinal) {
        double total = 0;
        try {
            Statement st = con.createStatement();
            String sql = "select a.distancia from viajes_encabezado as ve "
                    + "inner join viajes_detalle as vd "
                    + "on ve.id_viaje_encabezado = vd.id_viaje_encabezado "
                    + "inner join asignaciones as a "
                    + "on a.id_asignacion = vd.id_asignacion "
                    + "inner join sucursales as s "
                    + "on s.id_sucursal = a.id_sucursal "
                    + "inner join colaboradores as c "
                    + "on c.id_colaborador = vd.id_colaborador "
                    + "inner join transportistas as t "
                    + "on ve.id_transportista = t.id_transportista "
                    + "where t.id_transportista = '"+idTransportista+"' and fecha_viaje between '"+fechaInicial+"' and '"+fechaFinal+"'"
                    + "order by ve.fecha_viaje asc";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                total += rs.getDouble("distancia");
            }
            return total;
        } catch (SQLException ex) {
            Logger.getLogger(Asignaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public void imprimirReporte() {
        try {
            Calendar f = jdt_fechaInicial.getCalendar();
            int d = f.get(Calendar.DATE), mes = 1 + (f.get(Calendar.MONTH)), año = f.get(Calendar.YEAR);
            fechaInicial = (d + "/" + mes + "/" + año);

            Calendar f2 = jdt_fechaFinal.getCalendar();
            int d2 = f2.get(Calendar.DATE), mes2 = 1 + (f2.get(Calendar.MONTH)), año2 = f2.get(Calendar.YEAR);
            fechaFinal = (d2 + "/" + mes2 + "/" + año2);

            Calendar fFormateado = jdt_fechaInicial.getCalendar();
            int diaFormateado = fFormateado.get(Calendar.DATE), mesFormateado = 1 + (fFormateado.get(Calendar.MONTH)), añoFormateado = fFormateado.get(Calendar.YEAR);
            fechaInicialFormateada = (añoFormateado + "-" + mesFormateado + "-" + diaFormateado);

            Calendar fFormateado2 = jdt_fechaFinal.getCalendar();
            int diaFormateado2 = fFormateado2.get(Calendar.DATE), mesFormateado2 = 1 + (fFormateado2.get(Calendar.MONTH)), añoFormateado2 = fFormateado2.get(Calendar.YEAR);
            fechaFinalFormateada = (añoFormateado2 + "-" + mesFormateado2 + "-" + diaFormateado2);

            double total = calcularTotal(idTransportista, fechaInicialFormateada, fechaFinalFormateada);
            double distanciaTotal = calcularDistanciaTotal(idTransportista, fechaInicialFormateada, fechaFinalFormateada);
            
            String totalFormateado = String.format("%.2f", total);
            String distanciaTotalFormateada = String.format("%.2f", distanciaTotal);
            JasperReport reporte = null;
            String path = "src\\reportes\\reporte.jasper";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fechaInicialFormulario", fechaInicial);
            parameters.put("fechaFinalFormulario", fechaFinal);
            parameters.put("transportista", nombreTransportista);
            parameters.put("colaborador", lbl_nombreUsuario.getText());
            parameters.put("total", totalFormateado);
            parameters.put("idTransportista", idTransportista);
            parameters.put("fechaInicialBD", fechaInicialFormateada);
            parameters.put("fechaFinalBD", fechaFinalFormateada);
            parameters.put("distanciaTotal",distanciaTotalFormateada);
            reporte = (JasperReport) JRLoader.loadObjectFromFile(path);
            JasperPrint jprint;
            jprint = JasperFillManager.fillReport(reporte, parameters, con);
            JasperViewer view = new JasperViewer(jprint, false);
            final JRViewer viewer = new JRViewer(jprint);
            JRSaveContributor[] contrbs = viewer.getSaveContributors();

            for (JRSaveContributor saveContributor : contrbs) {
                if (!(saveContributor instanceof net.sf.jasperreports.view.save.JRDocxSaveContributor
                        || saveContributor instanceof net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor
                        || saveContributor instanceof net.sf.jasperreports.view.save.JRPdfSaveContributor)) {
                    viewer.removeSaveContributor(saveContributor);
                }
            }
            view.setContentPane(viewer);
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
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
        btn_generar = new javax.swing.JButton();
        lbl_home = new javax.swing.JLabel();
        lbl_usuario = new javax.swing.JLabel();
        lbl_tituloReportes = new javax.swing.JLabel();
        lbl_limpiar = new javax.swing.JLabel();
        cmb_transportistas = new javax.swing.JComboBox<>();
        jdt_fechaInicial = new com.toedter.calendar.JDateChooser();
        jdt_fechaFinal = new com.toedter.calendar.JDateChooser();
        lbl_fechaInicial = new javax.swing.JLabel();
        lbl_fechaFinal = new javax.swing.JLabel();
        lbl_transportista = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        kGradientPanel1.setkEndColor(new java.awt.Color(40, 74, 172));
        kGradientPanel1.setkStartColor(new java.awt.Color(205, 63, 145));
        kGradientPanel1.setkTransparentControls(false);
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(1319, 821));

        jpn_principal.setBackground(new java.awt.Color(255, 255, 255));

        lbl_nombreUsuario.setFont(new java.awt.Font("Roboto Black", 0, 24)); // NOI18N
        lbl_nombreUsuario.setText("Nombre de Usuario");

        btn_generar.setBackground(new java.awt.Color(205, 63, 145));
        btn_generar.setFont(new java.awt.Font("Roboto Black", 0, 24)); // NOI18N
        btn_generar.setForeground(new java.awt.Color(255, 255, 255));
        btn_generar.setText("Generar");
        btn_generar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_generar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_generarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_generarMouseExited(evt);
            }
        });
        btn_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generarActionPerformed(evt);
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

        lbl_tituloReportes.setFont(new java.awt.Font("Roboto Black", 0, 48)); // NOI18N
        lbl_tituloReportes.setText("Reportes");

        lbl_limpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/clean.png"))); // NOI18N
        lbl_limpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_limpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_limpiarMouseClicked(evt);
            }
        });

        cmb_transportistas.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        cmb_transportistas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione al transportista" }));
        cmb_transportistas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cmb_transportistas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmb_transportistas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_transportistasActionPerformed(evt);
            }
        });

        jdt_fechaInicial.setDateFormatString("dd/MM/yyyy");
        jdt_fechaInicial.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jdt_fechaFinal.setDateFormatString("dd/MM/yyyy");
        jdt_fechaFinal.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        lbl_fechaInicial.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_fechaInicial.setText("Fecha inicial");

        lbl_fechaFinal.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_fechaFinal.setText("Fecha final");

        lbl_transportista.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        lbl_transportista.setText("Transportista");

        javax.swing.GroupLayout jpn_principalLayout = new javax.swing.GroupLayout(jpn_principal);
        jpn_principal.setLayout(jpn_principalLayout);
        jpn_principalLayout.setHorizontalGroup(
            jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_principalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbl_fechaFinal)
                                    .addComponent(lbl_fechaInicial))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jdt_fechaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdt_fechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_generar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpn_principalLayout.createSequentialGroup()
                                        .addComponent(lbl_transportista)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)))))
                        .addGap(18, 18, 18)
                        .addComponent(lbl_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(33, Short.MAX_VALUE))
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addComponent(lbl_usuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lbl_tituloReportes)
                                .addGap(280, 280, 280))
                            .addGroup(jpn_principalLayout.createSequentialGroup()
                                .addComponent(lbl_nombreUsuario)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_home)
                                .addGap(21, 21, 21))))))
        );
        jpn_principalLayout.setVerticalGroup(
            jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpn_principalLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpn_principalLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lbl_nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(lbl_tituloReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmb_transportistas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_transportista, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jdt_fechaInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_fechaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpn_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_fechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdt_fechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(btn_generar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(146, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(297, 297, 297)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(330, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jpn_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
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

    private void btn_generarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_generarMouseEntered
        btn_generar.setBackground(new Color(156, 2, 91));
    }//GEN-LAST:event_btn_generarMouseEntered

    private void btn_generarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_generarMouseExited
        btn_generar.setBackground(new Color(205, 63, 145));
    }//GEN-LAST:event_btn_generarMouseExited

    private void lbl_homeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_homeMousePressed
        try {
            this.dispose();
            Principal principal;
            principal = new Principal(lbl_nombreUsuario.getText(), idColaboradorActivo);
            principal.setVisible(true);

        } catch (SQLException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lbl_homeMousePressed

    private void lbl_limpiarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_limpiarMouseClicked
        restablecer();
    }//GEN-LAST:event_lbl_limpiarMouseClicked

    private void btn_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generarActionPerformed
        validarDatosRellenados();
        validarFechas();
        imprimirReporte();
        restablecer();
    }//GEN-LAST:event_btn_generarActionPerformed

    private void cmb_transportistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_transportistasActionPerformed
        if (!cmb_transportistas.getSelectedItem().toString().equals("Seleccione al transportista")) {
            String informacionTransportista = cmb_transportistas.getSelectedItem().toString();
            String reemplazarInformacionTransportistas = informacionTransportista.replace("(", "|");
            String informacionTransportistaFormateada = reemplazarInformacionTransportistas.replace(")", "|");
            String[] partesInformacionTransportistas = informacionTransportistaFormateada.split("\\|");
            nombreTransportista = partesInformacionTransportistas[0].trim();
            numeroIdentidadTransportista = partesInformacionTransportistas[1].trim();
            idTransportista = capturarIdTransportista(numeroIdentidadTransportista);
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Reportes().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_generar;
    private javax.swing.JComboBox<String> cmb_transportistas;
    private com.toedter.calendar.JDateChooser jdt_fechaFinal;
    private com.toedter.calendar.JDateChooser jdt_fechaInicial;
    private javax.swing.JPanel jpn_principal;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel lbl_fechaFinal;
    private javax.swing.JLabel lbl_fechaInicial;
    private javax.swing.JLabel lbl_home;
    private javax.swing.JLabel lbl_limpiar;
    private javax.swing.JLabel lbl_nombreUsuario;
    private javax.swing.JLabel lbl_tituloReportes;
    private javax.swing.JLabel lbl_transportista;
    private javax.swing.JLabel lbl_usuario;
    // End of variables declaration//GEN-END:variables
}
