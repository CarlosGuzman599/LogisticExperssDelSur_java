/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logisticexperssdelsur;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author CARLITOS
 */
public class Algoritmos {
   
    public static ArrayList<String> Poblaciones = new ArrayList<String>();
    static ArrayList<String> RutaTiempo = new ArrayList<String>();
    static ArrayList<String> RutaDistancia = new ArrayList<String>();
    static ArrayList ValoresDisponibles = new ArrayList<>();
    static int SizeMatrix;
    static long[][] MatrixDistancia;
    static long[][] MatrixTiempo;
    static long TiempoTotal  = 0;
    static long DistanciaTotal = 0;
    
    
    public static void AgregarCiudad(){
        Poblaciones.clear();
        RutaDistancia.clear();
        RutaTiempo.clear();
        ValoresDisponibles.clear();
        TiempoTotal  = 0;
        DistanciaTotal = 0;
        
        Poblaciones.add("SUCURSLA CD.GUZMAN");
        UsoBaseDeDatos.LlenarArrayPoblaciones();
        GenerarRuta();
    }
    
    private static void GenerarRuta(){
        //COMENZAMOS GENERANDO LAS MATRICES DISTANCIA Y TIEMPO
        GeneraMatrixDistanciaTiempo();
        //BUSCAMOS LOCALIDAD MAS DISTAN PARA CORRER LOS ALGORITMOS
        for(int x = 1;x <= SizeMatrix-1;x++){
            ValoresDisponibles.add(MatrixDistancia[0][x]);
        }
        Collections.sort(ValoresDisponibles);
        long MayorValor = (long) ValoresDisponibles.get(ValoresDisponibles.size()-1);
        ValoresDisponibles.clear();
        for(int x = 0;x <= SizeMatrix-1;x++){
            if(MatrixDistancia[0][x] == MayorValor){
                int PunterSiguiente = x;
                System.out.println("---- "+Poblaciones.get(PunterSiguiente));
            }
        }
        RutaDistancia.add("SUCURSLA CD.GUZMAN");
        RutaTiempo.add("SUCURSLA CD.GUZMAN");
    }

    private static void GeneraMatrixDistanciaTiempo(){
        SizeMatrix = Poblaciones.size();
        MatrixDistancia = new long[SizeMatrix][SizeMatrix];
        MatrixTiempo = new long[SizeMatrix][SizeMatrix];
        
        for(int x=0;x<=SizeMatrix-1;x++){
            for(int w=0;w<=SizeMatrix-1;w++){
                if(!(x==w)){
                String[] TiempoDistancia = UsoBaseDeDatos.GetDataBaseTiempoDistancia(Poblaciones.get(x), Poblaciones.get(w));
                MatrixTiempo[x][w] = Long.parseLong(TiempoDistancia[1]);
                MatrixDistancia[x][w] = Long.parseLong(TiempoDistancia[0]);
                }else{
                    MatrixDistancia[x][w] = 0;
                    MatrixTiempo[x][w] = 0;
                }
            }   
        }
        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("+                                                               +");
        ValoresDisponibles.clear();
        TiempoRuta("SUCURSLA CD.GUZMAN");
        
        String[] TiempoDistancia = UsoBaseDeDatos.GetDataBaseTiempoDistancia(RutaTiempo.get(RutaTiempo.size()-1),RutaTiempo.get(0));
        int TiempoRetorno = Integer.valueOf(TiempoDistancia[1]);
        int DistanciaRetorno = Integer.valueOf(TiempoDistancia[0]);
        
        TiempoTotal = TiempoTotal + UsoBaseDeDatos.CajasEnCarga()*300;
        TiempoTotal = TiempoTotal + TiempoRetorno;
        System.out.println("        ******  Tiempo total: " + TiempoTotal/60 + " Minutos");
        ValoresDisponibles.clear();
        DistanciaRuta("SUCURSLA CD.GUZMAN");
        DistanciaTotal = DistanciaTotal + UsoBaseDeDatos.CajasEnCarga()*400;
        DistanciaTotal = DistanciaTotal + DistanciaRetorno;
        System.out.println("        ******  Distancia total: " + DistanciaTotal/1000 + " Kilometros");
        System.out.println("+                                                               +");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        
        OrdenVisitas();  
    }
    
    private static void TiempoRuta(String Origen){
        RutaTiempo.add(Origen);
        int PunterSiguiente = 0;
        int PosOrigen = Poblaciones.indexOf(Origen);
        
        for(int x = 0;x <= SizeMatrix-1;x++){
            if(!(PosOrigen==x) && !(RutaTiempo.contains(Poblaciones.get(x)))){
                ValoresDisponibles.add(MatrixTiempo[PosOrigen][x]);
            }
        }
        
        Collections.sort(ValoresDisponibles);
        long MenorValor = (long) ValoresDisponibles.get(0);
        ValoresDisponibles.clear();
        
        for(int x = 0;x <= SizeMatrix-1;x++){
            if(MatrixTiempo[PosOrigen][x] == MenorValor){
                PunterSiguiente = x;
            }
        }
        
        if(!(Poblaciones.size()==RutaTiempo.size()+1)){
            System.out.println("        "+Poblaciones.get(PunterSiguiente));
            TiempoTotal = TiempoTotal + MenorValor;
            TiempoRuta(Poblaciones.get(PunterSiguiente));
        }else{
            System.out.println("        "+Poblaciones.get(PunterSiguiente));
            TiempoTotal = TiempoTotal + MatrixTiempo[PosOrigen][PunterSiguiente];
            RutaTiempo.add(Poblaciones.get(PunterSiguiente));
        }
    }
    
    private static void DistanciaRuta(String Origen){
        RutaDistancia.add(Origen);
        int PunterSiguiente = 0;
        int PosOrigen = Poblaciones.indexOf(Origen);
        
        for(int x = 0;x <= SizeMatrix-1;x++){
            if(!(PosOrigen==x) && !(RutaDistancia.contains(Poblaciones.get(x)))){
                ValoresDisponibles.add(MatrixDistancia[PosOrigen][x]);
            }
        }
        
        Collections.sort(ValoresDisponibles);
        long MenorValor = (long) ValoresDisponibles.get(0);
        ValoresDisponibles.clear();
        
        for(int x = 0;x <= SizeMatrix-1;x++){
            if(MatrixDistancia[PosOrigen][x] == MenorValor){
                PunterSiguiente = x;
            }
        }
        
        if(!(Poblaciones.size()==RutaDistancia.size()+1)){
            //System.out.println(Poblaciones.get(PunterSiguiente));
            DistanciaTotal = DistanciaTotal + MenorValor;
            DistanciaRuta(Poblaciones.get(PunterSiguiente));  
        }else{
            //System.out.println("        "+Poblaciones.get(PunterSiguiente));
            DistanciaTotal = DistanciaTotal + MatrixDistancia[PosOrigen][PunterSiguiente];
            RutaDistancia.add(Poblaciones.get(PunterSiguiente));
        }
    }  
    
    private static void OrdenVisitas(){
        for(int x = 1;x <=RutaTiempo.size()-1;x++){
            System.out.println("--------------------------  "+RutaTiempo.get(x)+"  --------------------------");
            System.out.println("    VELIZ    FACTURA    CAJAS          CLIENTE                    DIRECCION");
            UsoBaseDeDatos.DomicioPoblacion(RutaTiempo.get(x));
        }
    }
}
