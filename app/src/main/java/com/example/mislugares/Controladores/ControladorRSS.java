package com.example.mislugares.Controladores;

import android.util.Log;

import com.example.mislugares.Modelos.Noticia;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ControladorRSS {
    private ArrayList<Noticia> noticias;

    private static String uri;

    // Hacemos un Singleton
    private static ControladorRSS instancia;


    private ControladorRSS(String direccion){
        this.noticias = new ArrayList<>();
        uri = direccion;
    }

    public static ControladorRSS getControlador(String direccion) {
        if (instancia == null){
            instancia = new ControladorRSS(direccion);
        }
        //else{

           // Log.i("RSS", "Usando el controlador RSS existente con URI: "+ uri);
        //}
        uri = direccion;
        return instancia;
    }

    // Devolvemos el array de las noticias
    public ArrayList<Noticia> getNoticias() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<Noticia> noticias = new ArrayList();

        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(uri);
            NodeList items = document.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Node nodo = items.item(i);
                Noticia noticia = new Noticia();
                // Vamos a contar las imagenes que hay
                int contadorImagenes = 0;
                for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {

                    if (n.getNodeName().equals("title")) {
                        String titulo = n.getTextContent();
                        noticia.setTitulo(titulo);
                        //System.out.println("Título: " + titulo);
                    }
                    if (n.getNodeName().equals("link")) {
                        String enlace = n.getTextContent();
                        noticia.setLink(enlace);
                        //System.out.println("Enlace: " + enlace);
                    }
                    if (n.getNodeName().equals("description")) {
                        String descripcion = n.getTextContent();
                        noticia.setDescripcion(descripcion);
                        //System.out.println("Descripción: " + descripcion);
                    }
                    if (n.getNodeName().equals("pubDate")) {
                        String fecha = n.getTextContent();
                        noticia.setFecha(fecha);
                        //System.out.println("Fecha: " + fecha);
                    }
                    if (n.getNodeName().equals("content:encoded")) {
                        String contenido = n.getTextContent();
                        noticia.setContenido(contenido);
                        //System.out.println("Contenido: " + contenido);

                    }
                    if (n.getNodeName().equals("enclosure")) {
                        Element e = (Element) n;
                        String imagen = e.getAttribute("url");
                        //Controlamos que solo rescate una imagen
                        if (contadorImagenes == 0) {
                            noticia.setImagen(imagen);
                        }
                        contadorImagenes++;
                    }
                }
                noticias.add(noticia);
            }

        } catch (ParserConfigurationException e) {

            Log.e("RSS", "Error: " + e.getMessage());

        } catch (IOException e) {
            Log.e("RSS", "Error: " + e.getMessage());

        } catch (DOMException e) {
            Log.e("RSS", "Error: " + e.getMessage());

        } catch (SAXException e) {
            Log.e("RSS", "Error: " + e.getMessage());

        }

        return noticias;
    }
}
