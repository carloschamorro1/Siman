/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import bd.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class Queries {
    
    Connection con;
    ResultSet rs;
    
    public Queries() throws SQLException{
        this.con = ConexionBD.obtenerConexion();
    }
    
    public ArrayList<String> llenarColaboradores(String nombreSucursal) throws SQLException{
         ArrayList<String> lista = new ArrayList<String>();
         String q= "SELECT c.id_colaborador, c.nombre_colaborador, c.apellido_colaborador, "
                 + "c.numero_identidad_colaborador, s.nombre_sucursal from colaboradores as c "
                 + "join sucursales as s on c.id_sucursal = s.id_sucursal "
                 + "where s.nombre_sucursal = '"+nombreSucursal+"'";
         Statement st;
         st = con.createStatement();
         try{
            rs=st.executeQuery(q);
            while(rs.next()){
                lista.add(rs.getString("nombre_colaborador") + " " 
                        + rs.getString("apellido_colaborador")+ " | "
                        + rs.getString("nombre_sucursal") + " | "
                        + rs.getString("numero_identidad_colaborador"));
            }
         }
        catch(Exception e){ 
                System.out.println(e.getMessage());
              }
          return lista;  
    }
    
     public ArrayList<String> llenarSucursales() throws SQLException{
         ArrayList<String> lista = new ArrayList<String>();
         String q= "select s.nombre_sucursal, c.nombre_ciudad from sucursales as s " +
                    "join ciudades as c on " +
                    "s.id_ciudad = c.id_ciudad";
         Statement st;
         st = con.createStatement();
         try{
            rs=st.executeQuery(q);
            while(rs.next()){
                lista.add(rs.getString("nombre_sucursal") +" (" 
                        + rs.getString("nombre_ciudad") + ")");
            }
         } 
        catch(Exception e){ 
                System.out.println(e.getMessage());
              }
          return lista;  
    }
  
}
