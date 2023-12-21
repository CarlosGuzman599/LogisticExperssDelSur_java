/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logisticexperssdelsur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author CARLITOS
 */
public class GoogleMaps {
    
    private static String Estado = "Jal";
    private static String Key =  "AquiVaUnaClaveDeGoogleCloudPlataform";
        
    public static String[] GetPoblacionPlaces(String Poblacion, String Cp){

        String Longitud = "Undefined";
        String Latitud  = "Undefined";
        String LocationGoogle = "Undefined";

        String QueryMaps = PlacesURLGeneradorPoblacion(Poblacion);
        
        try{
            URL url = new URL(QueryMaps);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("FindPlaceFromTextResponse");

            if(doc.getElementsByTagName("status").item(0).getTextContent().equals("OK")){
                Longitud = doc.getElementsByTagName("lng").item(0).getTextContent();
                Latitud  = doc.getElementsByTagName("lat").item(0).getTextContent();
                LocationGoogle = doc.getElementsByTagName("formatted_address").item(0).getTextContent();
            }
        }catch(IOException | ParserConfigurationException | DOMException | SAXException | NullPointerException e){
            System.out.println("GetCordenadasByPlaces - ERROR: "+e);
        }
        return new String[]{Latitud,Longitud,LocationGoogle};
    }
    
    public static String[] GetCordenadasPlaces(String Coordenadas){

        String Longitud = "Undefined";
        String Latitud  = "Undefined";
        String LocationGoogle = "Undefined";

        String QueryMaps = PlacesURLGeneradorCoordenadas(Coordenadas);
        
        try{
            URL url = new URL(QueryMaps);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("GeocodeResponse");

            if(doc.getElementsByTagName("status").item(0).getTextContent().equals("OK")){
                Longitud = doc.getElementsByTagName("lng").item(0).getTextContent();
                Latitud  = doc.getElementsByTagName("lat").item(0).getTextContent();
                LocationGoogle = doc.getElementsByTagName("formatted_address").item(0).getTextContent();
            }
        }catch(IOException | ParserConfigurationException | DOMException | SAXException | NullPointerException e){
            System.out.println("GetCordenadasByPlaces - ERROR: "+e);
        }
        return new String[]{Latitud,Longitud,LocationGoogle};
    }
    
    public static String[] GetCordenadasDomicilio(String Calle,String Colonia,String CP,String Poblacion){
        String Longitud = "Undefined";
        String Latitud  = "Undefined";
        String DesGoogle = "Undefined";
        
        try{
            URL url = new URL(CreaURLDomicilio(Calle, Colonia, CP, Poblacion));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("FindPlaceFromTextResponse");

            if(doc.getElementsByTagName("status").item(0).getTextContent().equals("OK")){
                Longitud = doc.getElementsByTagName("lng").item(0).getTextContent();
                Latitud  = doc.getElementsByTagName("lat").item(0).getTextContent();
                DesGoogle = doc.getElementsByTagName("formatted_address").item(0).getTextContent();
            }
            
            if(Latitud.equals("Undefined")){
                //String LatiLogi[] = DosGetCordenadasDomicilio(Calle, CP);
                //Latitud = LatiLogi[0];
                //Longitud = LatiLogi[1];
                System.out.println(url);
            }
        }catch(IOException | ParserConfigurationException | DOMException | SAXException | NullPointerException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al obtener datos de "+DataToSearch+"\n"+e,"GOOGLE MAPS",JOptionPane.ERROR_MESSAGE);
            System.out.println("GetCordenadasDomicilio - ERROR: "+e);
        }
        return new String[]{Latitud,Longitud};
    }
    
    public static String[] GetDistanciaTiempo(String OriPoblacion,String OriLatitu,String OriLongi,String DesPoblacion,String DesLatitud,String DesLongitud){
       String Distancia = "Undefined";
       String Tiempo = "Undefined";
       
       URL Holi = null;
       
       try{
           
            URL url = new URL(GiveFormatDistanciaTiempo(OriLatitu, OriLongi, DesLatitud, DesLongitud));
            Holi = url;
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("GeocodeResponse");
            
            if(doc.getElementsByTagName("status").item(0).getTextContent().equals("OK")){
                Distancia = doc.getElementsByTagName("distance").item(0).getTextContent();
                Tiempo = doc.getElementsByTagName("duration").item(0).getTextContent(); 
            }
       }catch(IOException | ParserConfigurationException | DOMException | SAXException | NullPointerException e){
            //JOptionPane.showMessageDialog(null, "Ups! Error al obtener tiempo y distancia de "+OriPoblacion+" a "+DesPoblacion+ "\n"+e,"GOOGLE MAPS",JOptionPane.ERROR_MESSAGE);
            System.out.println("GetDistanciaTiempo - ERROR: "+e+"\n URL: "+Holi);
       }
       return new String[]{Distancia,Tiempo};
    }

    private static String GiveFormatDistanciaTiempo(String OriLatitu,String OriLongi,String DesLatitud,String DesLongitud){
        String QueryDistanciaTiempo = "https://maps.googleapis.com/maps/api/distancematrix/xml?units=metric&origins="+OriLatitu+","+OriLongi+"&destinations="+DesLatitud+","+DesLongitud+"&key="+Key;
        return QueryDistanciaTiempo;
    }
    
    private static String CreaURLDomicilio(String Calle,String Colonia,String CP,String Poblacion){        
        Calle = Calle.replace(" ", "%20").concat(",");
        Colonia = Colonia.replace(" ", "%20").concat(",");
        Poblacion = Poblacion.replace(" ", "%20").concat(",");
        CP = CP.concat(",");
        
        String url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/xml?input="+Calle+Colonia+CP+Poblacion+"%20Jalisco,%20MX&inputtype=textquery&fields=formatted_address,name,rating,geometry&key=AIzaSyC2Uwg_PKLD7_ralRpNORJE6YylZm8eg1M";

        return url;
    }
    
    private static String PlacesURLGeneradorPoblacion(String Poblacion){
        String URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/xml?input="+Poblacion.replace(" ", "%20")+",%20"+Estado+",MX&inputtype=textquery&fields=formatted_address,name,rating,geometry&key="+Key;
        return URL;
    }
    
    private static String PlacesURLGeneradorCoordenadas(String Cooordenadas){
        String URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng="+Cooordenadas.replace(" ","")+"&key="+Key;
        return  URL;
    }
}
