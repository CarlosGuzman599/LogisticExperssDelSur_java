/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logisticexperssdelsur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlitos
 */

public class ClaseConexionMySQL {
    public static String Servidor = "jdbc:mysql://localhost/logisticexpress";
    public static String User = "root";
    public static String Pass = "";
    public static String Driver = "com.mysql.jdbc.Driver";
    public static Connection Conexion = null;
    
    public Connection ClaseConexionMySQL() throws ClassNotFoundException, SQLException{
        try{
            Class.forName(Driver);
            Conexion = (Connection) DriverManager.getConnection(Servidor,User,Pass);
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Ups! Checa la conexion a la base de datos \n"+e,"CONEXION DB",JOptionPane.ERROR_MESSAGE);
        }
        return Conexion;
    }
    
}
