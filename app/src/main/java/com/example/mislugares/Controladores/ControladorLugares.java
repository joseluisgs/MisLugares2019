package com.example.mislugares.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mislugares.MainActivity;
import com.example.mislugares.Modelos.Lugar;

import java.io.File;
import java.util.ArrayList;

public class ControladorLugares {

    // Hacemos un Singleton
    private static ControladorLugares instancia;
    private static Context context;


    private ControladorLugares(){

    }

    public static ControladorLugares getControlador(Context contexto) {
        if (instancia == null){
            instancia = new ControladorLugares();
        }
        //else{
        //    // Log.i("CL", "Usando el controlador Lugares existente ");
        //}
        context = contexto;
        return instancia;
    }

    // Lista todos los lugares...
    //Filtro sería el order
    public ArrayList<Lugar> listarLugares(String filtro) {
        // Abrimos la BD en Modo Lectura
        ArrayList<Lugar> lista = new ArrayList<Lugar>();
        Lugar aux;
        ControladorBD bdLugares = new ControladorBD(context, "BDLugares", null, 1);
        SQLiteDatabase bd = bdLugares.getReadableDatabase();
        if (bd != null) {
            // Podemos hacer la consulta directamente o parametrizada
            //Cursor c = bd.rawQuery("SELECT * FROM Lugares " + filtro, null);
            // http://www.sgoliver.net/blog/bases-de-datos-en-android-iii-consultarrecuperar-registros/
            //String[] campos = null; // new String[] {"campo1, campo2"}; // null implica todos *
            //String[] args = null; // new String[] {"usu1"}; // Sería un filtro del where
            Cursor c = bd.query("Lugares", null, null, null, null, null, filtro, null);
            if (c.moveToFirst()) {
                do {

                    aux = new Lugar(c.getInt(0), c.getString(1),
                            c.getString(2), c.getString(3),
                            c.getFloat(4), c.getFloat(5), c.getString(6));
                    lista.add(aux);
                } while (c.moveToNext());
            }
            bd.close();
            bdLugares.close();

        }
        return lista;
    }

    // Métodp para insertar un lugar
    public boolean insertarLugar(Lugar lugar){
        // se insertan sin problemas porque lugares es clave primaria, si ya están no hace nada
        // Abrimos la BD en modo escritura
        ControladorBD bdLugares = new ControladorBD(context, ControladorBD.db_lugares, null, 1);
        SQLiteDatabase bd = bdLugares.getWritableDatabase();
        boolean sal = false;
        try{
            //Cargamos los parámetros
            ContentValues nr = new ContentValues();
            nr.put("nombre", lugar.getNombre());
            nr.put("tipo", lugar.getTipo());
            nr.put("fecha", lugar.getFecha());
            nr.put("latitud", lugar.getLatitud());
            nr.put("longitud", lugar.getLongitud());
            nr.put("imagen", lugar.getImagen());
            // insertamos en su tabla
            bd.insert("Lugares", null, nr);
            sal = true;
        }catch(SQLException ex){
            Log.d("Lugares", "Error al insertar un nuevo lugar " + ex.getMessage());
        }finally {
            bd.close();
            bdLugares.close();
            return sal;
        }

    }

    /*
    // Aquí insertamos una serie de lugares por defecto para tenerlos siempre disponibles
    // Pero lo vamos a hacer con un XML, mira la clase String
    public void insertarTiposLugares(){
        // Si no existe la base de datos en un path, que es lo normal la primera vez
        // Creamos cosas
        // se insertan sin problemas porque lugares es clave primaria, si ya están no hace nada
        // Abrimos la BD en modo escritura
            ControladorBD bdLugares = new ControladorBD(context, ControladorBD.db_lugares, null, 1);
            SQLiteDatabase bd = bdLugares.getWritableDatabase();

            // Podríamos hacerlo así, pero podemos sufrir SQL Inyection, así que vamos a usar
            // Consultas preparadas
            // Además nos ahorramos equivocarnos con las comillas en las consultas
            //bd.execSQL("INSERT INTO Tipos (tipo) VALUES ('Ciudad')");
            //bd.execSQL("INSERT INTO Tipos (tipo) VALUES ('Pueblo')");

            //Creamos el registro a insertar como objeto ContentValues
            // Clave -> Valor
        try{
            ContentValues nr = new ContentValues();
            nr.put("tipo", "Ciudad");
            //Insertamos el registro en la base de datos
            bd.insert("Tipos", null, nr);

            // Los demas
            nr.put("tipo", "Pueblo");
            bd.insert("Tipos", null, nr);
            nr.put("tipo", "Monumento");
            bd.insert("Tipos", null, nr);
            nr.put("tipo", "Paisaje");
            bd.insert("Tipos", null, nr);
            nr.put("tipo", "Playa");
            bd.insert("Tipos", null, nr);
            nr.put("tipo", "Montaña");
            bd.insert("Tipos", null, nr);

        }catch(SQLException ex){
            Log.d("Lugares", "Error al cargar datos de Tipos lugares: " + ex.getMessage());
        }finally {
            bd.close();
            bdLugares.close();
        }

    }

     */


}
