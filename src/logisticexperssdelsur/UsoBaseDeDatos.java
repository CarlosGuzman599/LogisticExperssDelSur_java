/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logisticexperssdelsur;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author CARLITOS
 */
public class UsoBaseDeDatos {
    
    private static ClaseConexionMySQL ClaseConMySQL = new ClaseConexionMySQL();
    private static Connection Con = null;
    private static Calendar calendario = Calendar.getInstance();
    
    public static String AgregarExcel(String Ruta) throws SQLException{
        String Status = "";
        int ServiciosViejos = ConteoServicios();
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("load data infile "+Ruta+" into table servicio;");
            Status = (ConteoServicios()-ServiciosViejos) + " Servicios Agregados";
            AnalizaNuevos();
            AnalizaPoblacion();
            AnalizaRuta();
            AnalizaCliente();
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            Status = "ERROR " + e.getMessage();
        }
        
        return Status;
    }
  
    public static int ConteoServicios(){
        int TotalServicios = 0;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT COUNT(*) FROM servicio");
            Rt.beforeFirst();
            Rt.next();
            TotalServicios = Rt.getInt("count(*)");
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConteoServicios "+e);
        }
        return TotalServicios;
    }
    
    public static int ConteoServiciosAtender(){
        int TotalServicios = 0;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT COUNT(*) FROM servicio WHERE estado_serv = 'En espera'");
            Rt.beforeFirst();
            Rt.next();
            TotalServicios = Rt.getInt("count(*)");
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConteoServicios "+e);
        }
        return TotalServicios;
    }
    
    private static void AnalizaNuevos(){
        System.out.println("AnalizaNuevos()");
        Calendar calendario = Calendar.getInstance();
        int Dia, Mes, Ano;
        Dia = calendario.get(Calendar.DAY_OF_MONTH);
        Mes = calendario.get(Calendar.MONTH)+1;
        Ano = calendario.get(Calendar.YEAR);
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM servicio");
            while(Rt.next()){
                if(Rt.getString(12).equals("0")){
                    AgregarEntrada("UPDATE `servicio` SET `estado_serv` = 'En espera',`dia_serv` = '"+Dia+"', `mes_serv` = '"+Mes+"', `ano_serv` = '"+Ano+"' WHERE `factura_serv` = '"+Rt.getString(3)+"'");
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("AnalizaNuevos "+e);
        }
    }
    
    private static void AgregarEntrada(String Sentencia){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate(Sentencia);
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("AgregarEntrada "+e);
        }
    }
    
    ///+++++++++++++++++++++++++++++++++++++ POBLACIONES +++++++++++++++++++++++++++++++++++++
    ///+++++++++++++++++++++++++++++++++++++ POBLACIONES +++++++++++++++++++++++++++++++++++++
    ///+++++++++++++++++++++++++++++++++++++ POBLACIONES +++++++++++++++++++++++++++++++++++++
    
    private static void AnalizaPoblacion(){
        System.out.println("AnalizaPoblacion()");
        if(!(ConsultaPoblacion("SUCURSLA CD.GUZMAN"))){
            CreaSucursal();
        }
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `servicio`");

            while(Rt.next()){
                if(!ConsultaPoblacion(DepuraDatos.QuitaEspacioDos(Rt.getString(10)).toUpperCase())){
                    AgregarPoblacion(DepuraDatos.QuitaEspacioDos(Rt.getString(10)).toUpperCase(),Rt.getString(7));
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e){
            System.out.println("AnalizaCliente - ERROR ANALISIS DE POBLACION: "+e);
        }
    }
    
    private static void CreaSucursal(){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `poblacion` (`nombre_pobla`, `cp_pobla`, `latitud_pobla`, `longitud_pobla`, `nombre_google`) VALUES ('SUCURSLA CD.GUZMAN','49000','19.701494','-103.469158','Calle Gral. Ignacio Comonfort 124,Cd Guzman, Jal.')");
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear poblacion Nombre = "+poblacion+"\n"+e,"CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("CrearPoblacion - ERROR: "+e);
        }
    }
    
    private static boolean ConsultaPoblacion(String Nombre_Poblacion){
        boolean State = false;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");

            while(Rt.next()){
                if(Nombre_Poblacion.equals(Rt.getString(1))){
                    State = true;
                    break;
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al consultar poblacion \n"+e,"CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("ConsultaPoblacion - ERROR: "+e);
        }
        return State;
    }
    
    private static void AgregarPoblacion(String poblacion, String Cp){
        String Codenadas[] = GoogleMaps.GetPoblacionPlaces(poblacion,Cp);
        String latitud = Codenadas[0];
        String longitud = Codenadas[1];
        String NombreGoogle = Codenadas[2];

        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `poblacion` (`nombre_pobla`, `cp_pobla`, `latitud_pobla`, `longitud_pobla`, `nombre_google`) VALUES ('"+poblacion+"','"+Cp+"','"+latitud+"','"+longitud+"','"+NombreGoogle+"')");
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear poblacion Nombre = "+poblacion+"\n"+e,"CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("CrearPoblacion - ERROR: "+e);
        }
    }
    
    public static int ConteoPoblacionesErroneas(){
        int TotalPoblaciones = 0;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT COUNT(*) FROM poblacion WHERE nombre_google = 'Undefined'");
            Rt.beforeFirst();
            Rt.next();
            TotalPoblaciones = Rt.getInt("count(*)");
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConteoServicios "+e);
        }
        return TotalPoblaciones;
    }
    
    public static void LlenarTablaTodos(JTable jTPoblaciones){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");
            Tabla.setColumnIdentifiers(new Object[]{"Nombre Poblacion", "Codigo Postal","Latitud","Longitud","Nombre Asociado"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString("nombre_pobla"),Rt.getString("cp_pobla"),Rt.getString("latitud_pobla"),Rt.getString("longitud_pobla"),Rt.getString("nombre_google")});
            }
            Rt.close();
            St.close();
            Con.close();
            jTPoblaciones.setModel(Tabla);
            jTPoblaciones.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
    }
    
    public static void LlenarTablaError(JTable jTPoblaciones){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`  WHERE nombre_google = 'Undefined'");
            Tabla.setColumnIdentifiers(new Object[]{"Nombre Poblacion", "Codigo Postal","Latitud","Longitud","Nombre Asociado"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString("nombre_pobla"),Rt.getString("cp_pobla"),Rt.getString("latitud_pobla"),Rt.getString("longitud_pobla"),Rt.getString("nombre_google")});
            }
            Rt.close();
            St.close();
            Con.close();
            jTPoblaciones.setModel(Tabla);
            jTPoblaciones.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
    }
    
    public static class ModeloTablaNoEditable extends DefaultTableModel{
        public boolean isCellEditable (int row, int column){
            if (column == 5)
                return true;
                return false;
        }
    }
    
    public String GuardaCambiosPoblacion(String Pobacion, String CP, String Latitud, String Longitud, String Referencia){
        String Stado = "";
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("UPDATE `poblacion` SET `latitud_pobla` = '"+Latitud+"', `longitud_pobla` = '"+Longitud+"', `nombre_google` = '"+Referencia+"' WHERE `poblacion`.`nombre_pobla` = '"+Pobacion+"';");
            AnalizaRutaDos(Pobacion);
            Stado = "OK";
            St.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(this, "ERROR \n"+e, "INFORMACION",JOptionPane.ERROR_MESSAGE);
            Stado = ""+e;
        }
        return Stado;
    }
    
    //+++++++++++++++++++++++++++++++++++++ CODIGO POSTAL +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ CODIGO POSTAL +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ CODIGO POSTAL +++++++++++++++++++++++++++++++++++++
    
    public String CiudadCodigoPostal(String CodigoPostal){
        String Ciudad = "Sin Concidencia";
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `codigo_postal` WHERE codigo_cp = '"+CodigoPostal+"'");
            while(Rt.next()){
                Ciudad = Rt.getString(5);
                break;
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("CiudadCodigoPostal - Error: "+e);
        }
        return Ciudad;
    }
    
    public static void LlenarComboPoblaciones(JComboBox jCBPoblaciones, String CodigoPostal){
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `codigo_postal` WHERE codigo_cp = '"+CodigoPostal+"'");
            while(Rt.next()){
                jCBPoblaciones.addItem(Rt.getString(3));
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarComboPoblaciones - Error: "+e);
        }
    }
    
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
 
    private static void AnalizaRuta(){
        System.out.println("AnalizaRuta()");
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");
            while (Rt.next()) {
                if(!(Rt.getString(3).equals("Undefined") && Rt.getString(3).equals("Undefined"))){
                    AnalizaRutaDos(Rt.getString(1));//Origen
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al analizar ruta, informacion no obtenia","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("AnalizaRuta - ERROR SIN RESPUESTA: "+e);
        }
    }
    
    private static void AnalizaRutaDos(String Origen){
        System.out.println("AnalizaRutaDos()");
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");
            while (Rt.next()) {
                if(!(Rt.getString(3).equals("Undefined") && Rt.getString(4).equals("Undefined")) && !(Origen.equals(Rt.getString(1)))){
                    if(!(ConsultaRuta(Origen, Rt.getString(1)))){
                        CreaInfoRuta(Origen, Rt.getString(1), "NUEVO");//ORIGEN,DESTINO,NUEVO
                    }else if(ConsultaRuta(Origen, Rt.getString(1))){
                        CreaInfoRuta(Origen, Rt.getString(1), "MODIFICA");//ORIGEN,DESTINO,MODIFICA
                    }
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al analizar ruta, informacion no obtenia","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("AnalizaRutaDos - ERROR SIN RESPUESTA: "+e);
        }
    }
    
    private static Boolean ConsultaRuta(String Origen, String Destino){
       boolean Status = false;
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `rutas_conocidas` WHERE `origen_ruta` LIKE '"+Origen+"' AND `destino_ruta` LIKE '"+Destino+"'");
            if(Rt.first()){
                Status = true;
            }
            Rt.close();
            St.close();
            Con.close();
        } catch (ClassNotFoundException | SQLException e) {
            //JOptionPane.showMessageDialog(null, "Ups! Error al consultar ruta, informacion no obtenia","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("ConsultaRuta - ERROR SIN RESPUESTA: "+e);
        }
       return Status;
    }
    
    private static void CreaInfoRuta(String OriPoblacion, String DesPoblacion, String Tipo){
        String OriLongitud = " ";
        String OriLatitud = " ";

        String DesLongitud = " ";
        String DesLatitud = " ";

        try{

            String InfoOrigen[] = GetDataBaseCodenadas(OriPoblacion);
            OriLatitud = InfoOrigen[0];
            OriLongitud = InfoOrigen[1];

            String InfoDestino[] = GetDataBaseCodenadas(DesPoblacion);
            DesLatitud = InfoDestino[0];
            DesLongitud = InfoDestino[1];

            String DistamciaTiepo[] = GoogleMaps.GetDistanciaTiempo(OriPoblacion,OriLatitud,OriLongitud,DesPoblacion,DesLatitud,DesLongitud);

            if(DistamciaTiepo[0].equals("Undefined") && DistamciaTiepo[1].equals("Undefined")){
                //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+OriPoblacion+" - "+DesPobalcion,"GOOGLE MAPS",JOptionPane.ERROR_MESSAGE);
                System.out.println("CreaInfoRuta - ERROR SIN RESPUESTA: "+OriPoblacion+" - "+DesPoblacion);
            }else{
                String Tiempo =  DepuraDatos.DepuraTiempoDistancia(DistamciaTiepo[1]);
                String Distancia = DepuraDatos.DepuraTiempoDistancia(DistamciaTiepo[0]);
                
                if(Tipo.equals("NUEVO")){
                    CreaRuta(OriPoblacion,DesPoblacion,Tiempo,Distancia);  
                }else if(Tipo.equals("MODIFICA")){
                    ModificaRuta(OriPoblacion,DesPoblacion,Tiempo,Distancia);
                }
                
                //CreaRuta(OriPoblacion,DesPoblacion,Tiempo,Distancia);
            }
            //CAMINO DE REGRESO
            String DistamciaTiepoRetorno[] = GoogleMaps.GetDistanciaTiempo(DesPoblacion,DesLatitud,DesLongitud,OriPoblacion,OriLatitud,OriLongitud);
            if(DistamciaTiepoRetorno[0].equals("Undefined") && DistamciaTiepoRetorno[1].equals("Undefined")){
                //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+OriPoblacion+" - "+DesPobalcion,"GOOGLE MAPS",JOptionPane.ERROR_MESSAGE);
                System.out.println("CreaInfoRuta - ERROR SIN RESPUESTA: "+DesPoblacion+" - "+OriPoblacion);
            }else{
                String TiempoRetorno =  DepuraDatos.DepuraTiempoDistancia(DistamciaTiepoRetorno[1]);
                String DistanciaRetorno = DepuraDatos.DepuraTiempoDistancia(DistamciaTiepoRetorno[0]);
                
                if(Tipo.equals("NUEVO")){
                    CreaRuta(DesPoblacion,OriPoblacion,TiempoRetorno,DistanciaRetorno);
                }else if(Tipo.equals("MODIFICA")){
                    ModificaRuta(DesPoblacion,OriPoblacion,TiempoRetorno,DistanciaRetorno);
                }
                
                //CreaRuta(DesPoblacion,OriPoblacion,TiempoRetorno,DistanciaRetorno);
            }
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+OriPoblacion+" - "+DesPobalcion+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("CreaInfoRuta - ERROR SIN RESPUESTA: "+OriPoblacion+" - "+DesPoblacion+" : "+e);
        }
    }
        
    private static String[] GetDataBaseCodenadas(String Poblacion){
        String Latitud = "";
        String Longitud = "";
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");
            while (Rt.next()) {
                if(Rt.getString(1).equals(Poblacion)){
                    Latitud = Rt.getString(3);
                    Longitud = Rt.getString(4);
                    break;
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al consultar Cordenadas, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("GetDataBaseCodenadas - ERROR SIN RESPUESTA: "+Poblacion +e);
        }
        return new String[]{Latitud,Longitud};
    }
    
    private static void CreaRuta(String Origen,String Destino,String Tiempo, String Distancia){
        int RegistrosRutas = 0;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("select count(*) from rutas_conocidas");
            Rt.beforeFirst();
            Rt.next();
            RegistrosRutas = Rt.getInt("count(*)")+1;
            St.executeUpdate("INSERT INTO `rutas_conocidas` (`id_ruta`,`origen_ruta`,`destino_ruta`,`metros_ruta`,`segundos_ruta`) VALUES ('"+RegistrosRutas+"','"+Origen+"','"+Destino+"','"+Tiempo+"','"+Distancia+"')");
            Rt.close();
            St.close();
            Con.close();
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("CreaRuta - ERROR SIN RESPUESTA: "+Origen+" - "+Destino+" : "+e);
        }
    }
    
    private static void ModificaRuta(String Origen,String Destino,String Tiempo, String Distancia){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("UPDATE `rutas_conocidas` SET `metros_ruta` = '"+Distancia+"', `segundos_ruta` = '"+Tiempo+"' WHERE `rutas_conocidas`.`origen_ruta` = '"+Origen+"' AND `rutas_conocidas`.`destino_ruta` = '"+Destino+"';");
            St.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(this, "ERROR \n"+e, "INFORMACION",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    //+++++++++++++++++++++++++++++++++++++ ATENDER SERVICIOS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ ATENDER SERVICIOS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ ATENDER SERVICIOS +++++++++++++++++++++++++++++++++++++
    
    public void LimpioTablaDisponible(){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM disponibles_aten;");
            St.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(this, "ERROR \n"+e, "INFORMACION",JOptionPane.ERROR_MESSAGE);
            System.out.println("LimpioTablaDisponible - Error: "+e);
        }
    }
    
    public void LimpioTablaSelectos(){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM seleccion_aten;;");
            St.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(this, "ERROR \n"+e, "INFORMACION",JOptionPane.ERROR_MESSAGE);
            System.out.println("LimpioTablaSelectos - Error: "+e);
        }
    }
    
    public void LlenoTablaDisponibles(){
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion`");
            while(Rt.next()){
                if(CajasPoblacion(Rt.getString("nombre_pobla"))>0){
                    AgregaTablaDisponible(Rt.getString("nombre_pobla"),CajasPoblacion(Rt.getString("nombre_pobla")));
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaDisponibles - Error: "+e);
        }
    }
    
    private static int CajasPoblacion(String Poblacion){
        int Cajas = 0;
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `servicio` WHERE `poblacion_serv` LIKE '"+Poblacion+"' AND `estado_serv`= 'En espera';");
            while(Rt.next()){
                Cajas = Cajas + Rt.getInt(4);
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(this, "ERROR \n"+e, "INFORMACION",JOptionPane.ERROR_MESSAGE);
        }
        return Cajas;
    }
    
    public void AgregaTablaDisponible(String Poblacion, int Cajas){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `disponibles_aten` (`id_disp`, `poblacion_disp`, `cajas_disp`) VALUES (NULL, '"+Poblacion+"', '"+Cajas+"');");
            St.close();
            Con.close();
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("AgregaTablaDisponible - ERROR SIN RESPUESTA: "+e);
        }
    }
    
    public void RemueveTablaDisponible(String Poblacion, int Cajas){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM `disponibles_aten` WHERE `disponibles_aten`.`poblacion_disp` = '"+Poblacion+"' AND `disponibles_aten`.`cajas_disp` = '"+Cajas+"';");
            St.close();
            Con.close();
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("RemueveTablaDisponible - ERROR SIN RESPUESTA: "+e);
        }
    }
    
    public void AgregaTablaSeleccion(String Poblacion, int Cajas){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `seleccion_aten` (`id_selec`, `poblacion_selec`, `cajas_selec`) VALUES (NULL, '"+Poblacion+"', '"+Cajas+"');");
            St.close();
            Con.close();
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("AgregaTablaSeleccion - ERROR SIN RESPUESTA: "+e);
        }
    }
    
    public void RemueveTablaSeleccion(String Poblacion, int Cajas){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM `seleccion_aten` WHERE `seleccion_aten`.`poblacion_selec` = '"+Poblacion+"' AND `seleccion_aten`.`cajas_selec` = '"+Cajas+"';");
            St.close();
            Con.close();
        }catch (Exception e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("RemueveTablaSeleccion - ERROR SIN RESPUESTA: "+e);
        }
    }

    public static void LlenarTablaServiosDisponibles(JTable jTServiciosPoblaciones){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `disponibles_aten`");
            Tabla.setColumnIdentifiers(new Object[]{"Nombre Poblacion", "Cajas"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString(2),String.valueOf(Rt.getInt(3))});
            }
            Rt.close();
            St.close();
            Con.close();
            jTServiciosPoblaciones.setModel(Tabla);
            jTServiciosPoblaciones.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
    }
    
    public static void LlenarTablaServiosSeleccion(JTable jTServiciosSeleccion){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `seleccion_aten`");
            Tabla.setColumnIdentifiers(new Object[]{"Seleccionadas", "Cajas"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString(2),String.valueOf(Rt.getInt(3))});
            }
            Rt.close();
            St.close();
            Con.close();
            jTServiciosSeleccion.setModel(Tabla);
            jTServiciosSeleccion.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
    }
    
    public static int CajasEnCarga(){
        int Cajas = 0;
                try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `seleccion_aten`");
            while(Rt.next()){
                Cajas = Cajas + Rt.getInt(3);
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
        return Cajas;
    }
    
    public static void LlenarArrayPoblaciones(){
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `seleccion_aten`");
            while(Rt.next()){
                Algoritmos.Poblaciones.add(Rt.getString(2));
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarArrayPoblaciones - Error: "+e);
        }
    }
    
    public static String[] GetDataBaseTiempoDistancia(String Origen,String Destino){
        String Tiempo = "1";
        String Distancia = "1";
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `rutas_conocidas`");
            while (Rt.next()) {
                if(Rt.getString(2).equals(Origen) && Rt.getString(3).equals(Destino)){
                    Tiempo = Rt.getString(5);
                    Distancia = Rt.getString(4);
                    break;
                }
            }
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al consultar ruta, informacion no obtenia \n"+Origen+" - "+Destino+"\n","CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("GetDataBaseTiempoDistancia - ERROR SIN RESPUESTA: "+Origen+" - "+Destino+" : "+e);
        }
        return new String[]{Distancia,Tiempo};
    }
    
    public static void DomicioPoblacion(String Poblacion){
        int Orden = 1;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM servicio WHERE poblacion_serv LIKE '"+Poblacion+"' AND estado_serv = 'En espera'");
            while(Rt.next()){
                System.out.println(Orden+". "+Rt.getString("veliz_serv")+"  "+Rt.getString("factura_serv")+"    "+Rt.getString("cajas_serv")+"    "+Rt.getString("nombre_serv")+"    "+Rt.getString("domicilio_serv"));
                Orden = Orden+1;
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("DomicioPoblacion Error "+e);
        }
    }
    
    public void LlenarComboBoxVehiculosAtenderServios(JComboBox jCBAtenderServicioVehiculos){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `vehiculo`");
            while(Rt.next()){
                jCBAtenderServicioVehiculos.addItem(Rt.getString(2));
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            
        }
    }
    
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ RUTAS +++++++++++++++++++++++++++++++++++++
    
    public void RutaNueva(String RutaNueva){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `ruta` (`nombre_ruta`) VALUES ('"+RutaNueva+"');");
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al crear poblacion Nombre = "+poblacion+"\n"+e,"CONEXION DB",JOptionPane.ERROR_MESSAGE);
            System.out.println("RutaNueva - ERROR: "+e);
        }
    }
    
    public boolean RutaExiste(String Ruta){
        boolean Status = false;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `ruta`");
            while (Rt.next()) {
                if(Ruta.equals(Rt.getString(1))){
                    Status = true;
                    break;
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("RutaExiste - ERROR ANALISIS DE CLIENTES: "+e);
        } 
        return Status;
    }
    
    public void RutasLlenarTabla(JTable tbRuta){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `ruta`");
            Tabla.setColumnIdentifiers(new Object[]{"R U T A S"});
            while(Rt.next()){
                if(!"Indefinida".equals(Rt.getString(1))){
                    Tabla.addRow(new String[]{Rt.getString(1)});
                }
            }
            Rt.close();
            St.close();
            Con.close();
            tbRuta.setModel(Tabla);
            tbRuta.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("RutasLlenarTabla - Error: "+e);
        }
    }
    
    public void RutaEliminar(String Ruta){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM `ruta` WHERE `ruta`.`nombre_ruta` = \'"+Ruta+"\'");
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("RutaEliminar - ERROR CREAR CLIENTE: "+e);
        }
    }
    
    public void RutaEliminaAjustaPoblaciones(String Ruta){
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `poblacion` WHERE `ruta_pobla` LIKE '"+Ruta+"'");
            while(Rt.next()){
                RutaEnPoblaciones(Rt.getString(1));
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaTodos - Error: "+e);
        }
    }
    
    private void RutaEnPoblaciones(String Publacion){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("UPDATE `poblacion` SET `ruta_pobla` = 'Indefinida' WHERE `poblacion`.`nombre_pobla` = '"+Publacion+"';");
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("RutaEliminar - ERROR CREAR CLIENTE: "+e);
        }
    }

    //+++++++++++++++++++++++++++++++++++++ CLIENTES +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ CLIENTES +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ CLIENTES +++++++++++++++++++++++++++++++++++++
    
    private static void AnalizaCliente(){
        System.out.println("AnalizaCliente()");
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `servicio`");
            while (Rt.next()) {
                if(!(ConsultaCliente(Rt.getString(1)))){
                    AgregarCliente(Rt.getString(1), Rt.getString(2).toUpperCase(), DepuraDatos.DepuraDomicilio(Rt.getString(5)), Rt.getString(6).toUpperCase(), Rt.getString(7), DepuraDatos.QuitaEspacioDos(Rt.getString(10)).toUpperCase());
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("AnalizaCliente - ERROR ANALISIS DE CLIENTES: "+e);
        }   
    }
    
    private static Boolean ConsultaCliente(String Veliz){
        boolean Status = false;
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `cliente` WHERE `veliz_client` LIKE '"+Veliz+"'");
            if(Rt.first()){
                Status = true;
            }
            Rt.close();
            St.close();
            Con.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("ConsultaCliente - ERROR CONSULTAR CLIENTE: "+e);
        }
        return Status;
    }
    
    private static void AgregarCliente(String Veliz,String Nombre, String Domicilio, String Colonia, String Cp, String Poblacion){
        String LatiLongi[] = GoogleMaps.GetCordenadasDomicilio(Domicilio,Colonia,Cp,Poblacion);
        String Latidud = LatiLongi[0];
        String Longitud = LatiLongi[1];

        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `cliente` (`veliz_client`, `nombre_client`, `domicilio_client`, `colonia_client`, `poblacion_client`, `cp_client`, `latitud_client`, `longitud_client`) VALUES ('"+Veliz+"','"+Nombre+"','"+Domicilio+"','"+Colonia+"','"+Poblacion+"','"+Cp+"','"+Latidud+"','"+Longitud+"');");
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConsultaCliente - ERROR CREAR CLIENTE: "+e);
        }
    }
    
    public static int ConteoClientesErroneos(){
        int TotalClientes = 0;
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT COUNT(*) FROM cliente WHERE latitud_client = 'Undefined'");
            Rt.beforeFirst();
            Rt.next();
            TotalClientes = Rt.getInt("count(*)");
            Rt.close();
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConteoServicios "+e);
        }
        return TotalClientes;
    }
    
    public static void LlenarTablaClientes(JTable jTClientes){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM cliente");
            Tabla.setColumnIdentifiers(new Object[]{"Veliz", "Cliente","Domicilio","Poblacion"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString(1),Rt.getString(2),Rt.getString(3),Rt.getString(5)});
            }
            Rt.close();
            St.close();
            Con.close();
            jTClientes.setModel(Tabla);
            jTClientes.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaClientes - Error: "+e);
        }
    }
    
    //+++++++++++++++++++++++++++++++++++++ VEHICULOS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ VEHICULOS +++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++ VEHICULOS +++++++++++++++++++++++++++++++++++++
    
    public void LlenarTablaVehiculos(JTable tbVehiculos){
        ModeloTablaNoEditable Tabla = new ModeloTablaNoEditable();
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `vehiculo`");
            Tabla.setColumnIdentifiers(new Object[]{"Vehiculos Disponibles"});
            while(Rt.next()){
                Tabla.addRow(new String[]{Rt.getString(2)});
            }
            Rt.close();
            St.close();
            Con.close();
            tbVehiculos.setModel(Tabla);
            tbVehiculos.setEnabled(true);
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("LlenarTablaVehiculos - Error: "+e);
        }
    }
    
    public String VehiculosGuardarNuevo(String Modelo,String Marca,String Rendimiento){
        String Status = "Ok";
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("INSERT INTO `vehiculo` (`id_vehiculo`, `modelo_vehiculo`, `marca_vehiculo`, `rendimiento_vehiculo`) VALUES (NULL, '"+Modelo+"', '"+Marca+"', '"+Rendimiento+"');");
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("ConsultaCliente - ERROR CREAR CLIENTE: "+e);
            Status = ""+e;
        }
        return Status;
    }
    
    public boolean VehiculoRepetido(String Vehiculo){
        boolean Status = false;
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `vehiculo`");
            while(Rt.next()){
                if(Vehiculo.equals(Rt.getString(2))){
                    Status = true;
                    break;
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("VehiculoRepetido - Error: "+e);
        }
        return Status;
    }
    
    public String[] VehiculoDatos(String Vehiculo){
        String ID = "";
        String Modelo = "";
        String Marca = "";
        String Rendimiento = "";
        
        try {
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            ResultSet Rt = St.executeQuery("SELECT * FROM `vehiculo`");
            while(Rt.next()){
                if(Vehiculo.equals(Rt.getString(2))){
                    ID = Rt.getString(1);
                    Modelo = Rt.getString(2);
                    Marca = Rt.getString(3);
                    Rendimiento = Rt.getString(4);
                    break;
                }
            }
            Rt.close();
            St.close();
            Con.close();
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("VehiculoRepetido - Error: "+e);
        }
        return new String[]{ID,Modelo,Marca,Rendimiento};
    }
    
    public void VehiculoEliminar(String ID){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("DELETE FROM `vehiculo` WHERE `vehiculo`.`id_vehiculo` = "+ID);
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("VehiculoEliminar - ERROR CREAR CLIENTE: "+e);
        }
    }
    
    public void VehiculoModificar(String ID,String Modelo,String Marca,String Rendimiento){
        try{
            Con = ClaseConMySQL.ClaseConexionMySQL();
            Statement St = Con.createStatement();
            St.executeUpdate("UPDATE `vehiculo` SET `modelo_vehiculo` = '"+Modelo+"', `marca_vehiculo` = '"+Marca+"', `rendimiento_vehiculo` = '"+Rendimiento+"' WHERE `vehiculo`.`id_vehiculo` = " + ID);
            St.close();
            Con.close();
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("VehiculoModificar - ERROR CREAR CLIENTE: "+e);
        }
    }
}
