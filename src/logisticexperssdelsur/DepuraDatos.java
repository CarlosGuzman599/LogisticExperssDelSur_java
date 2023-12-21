/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logisticexperssdelsur;

/**
 *
 * @author CARLITOS
 */
public class DepuraDatos {
    public static String CambiaDiagonal(String Ruta){
        String Tem = "";
        int Conver;
        char Dato;
        Tem = Tem + '"';
        for(int x=0;x<Ruta.length();x++){            
            Dato = Ruta.charAt(x);
            Conver = Dato;
            if(Conver==92){
                Tem = Tem+'/';
            }else{
                Tem = Tem + Ruta.charAt(x);
            }
        }
        Tem = Tem + '"';
        Ruta = Tem;
        return Ruta; 
    }
    
    public static String QuitaEspacioDos(String NombreLoc){
        String Tem = "";
        for(int i = 0;i<=NombreLoc.length()-1;i++){
            Tem = Tem+NombreLoc.charAt(i);
        }
        return Tem;
    }
    
    public static String DepuraTiempoDistancia(String ToDepure){
        String Depurado = "";
        
        for(int w = 4;w<=ToDepure.length();w++){
            if(!(ToDepure.charAt(w) == ' ')){
                Depurado = Depurado + ToDepure.charAt(w);
            }else{
                break;
            }
        }
        return Depurado;
    }
    
    public static String DepuraDomicilio(String Domicilio){
        Domicilio = Domicilio.toUpperCase().replace(" EXT: ", " ").replace(" EXT. ", " ").replace(" NO. ", " ").replace(".", "");
        Domicilio = Domicilio.replace(" NO ", " ").replace(":", "").replace(" NO ", " ").replace(" EXT ", " ").replace(" SN ", "").replace("-", "").replace(" SN", "");
        Domicilio = Domicilio.replace("#", "");
        try{
            if(Domicilio.contains(" INT ")){
                String TemDomicilio = "";
                for(int i = 0;i<=Domicilio.length()-1; i++){
                    if(Domicilio.charAt(i)=='I' && Domicilio.charAt(i+1)=='N' && Domicilio.charAt(i+2)=='T' && Domicilio.charAt(i+3)==' '){
                        for(int w = 0;w <= i-1;w++){
                            TemDomicilio = TemDomicilio + Domicilio.charAt(w);
                        }
                        break;
                    }
                }
                Domicilio = TemDomicilio;
            }
            
            if(Domicilio.contains(" INTERIOR ")){
                String TemDomicilio = "";
                for(int i = 0;i<=Domicilio.length()-1; i++){
                    if(Domicilio.charAt(i)=='I' && Domicilio.charAt(i+1)=='N' && Domicilio.charAt(i+2)=='T' && Domicilio.charAt(i+3)=='E' && Domicilio.charAt(i+4)=='R' && Domicilio.charAt(i+5)=='I' && Domicilio.charAt(i+6)=='O' && Domicilio.charAt(i+7)=='R'){
                        for(int w = 0;w <= i-1;w++){
                            TemDomicilio = TemDomicilio + Domicilio.charAt(w);
                        }
                        break;
                    }
                }
                Domicilio = TemDomicilio;
            }
            
            if(Domicilio.charAt(Domicilio.length()-1) == ' '){
                String TemDomicilio = "";
                for(int i = 0;i<=Domicilio.length()-2; i++){
                    TemDomicilio = TemDomicilio + Domicilio.charAt(i);
                }
                Domicilio = TemDomicilio;
            }
            
            String TemDomicilio = "";
            for(int i = 0;i<=Domicilio.length()/2; i++){
                TemDomicilio = TemDomicilio + Domicilio.charAt(i);
            }
            
            int ban = 0;
            for(int i = (Domicilio.length()/2)+1;i<=Domicilio.length()-1;i++){
                if((Domicilio.charAt(i)=='1' | Domicilio.charAt(i)=='2' | Domicilio.charAt(i)=='3' | Domicilio.charAt(i)=='4' | Domicilio.charAt(i)=='5' | Domicilio.charAt(i)=='6' | Domicilio.charAt(i)=='7' | Domicilio.charAt(i)=='8' | Domicilio.charAt(i)=='9' | Domicilio.charAt(i)=='0') && ban == 0){
                    ban = 1;
                }
                if(!(Domicilio.charAt(i)=='1' | Domicilio.charAt(i)=='2' | Domicilio.charAt(i)=='3' | Domicilio.charAt(i)=='4' | Domicilio.charAt(i)=='5' | Domicilio.charAt(i)=='6' | Domicilio.charAt(i)=='7' | Domicilio.charAt(i)=='8' | Domicilio.charAt(i)=='9' | Domicilio.charAt(i)=='0') && ban == 1){
                    break;
                }
                TemDomicilio = TemDomicilio + Domicilio.charAt(i);
            }
            
            Domicilio = TemDomicilio;
        }catch(Exception e){
        }
        return Domicilio;
    }
}
