package com.example.mislugares.Modelos;

/**
 * Clase Lugar
 */
public class Lugar {
    private long id;
    private String nombre;
    private String tipo;
    private String fecha;
    private float latitud;
    private float longitud;
    private String imagen;

    public Lugar() {

    }

    /**
     * Constructor de la clase Lugar
     * @param id Identificador, ID
     * @param nombre Nombre de Lugar
     * @param tipo Tipo de Lugar
     * @param fecha Fecha de Lugar
     * @param latitud Latitud del Lugar
     * @param longitud Longitud de Lugar
     * @param imagen Imagen de Lugar
     */
    public Lugar(long id, String nombre, String tipo, String fecha, float latitud, float longitud, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagen = imagen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
