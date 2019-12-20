package com.example.mislugares.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.mislugares.Modelos.Lugar;

import java.util.ArrayList;


/**
 * Controlador de de Lugares
 */
public class ControladorLugares {

    // Hacemos un Singleton
    private static ControladorLugares instancia;
    private static Context context;


    private ControladorLugares() {

    }

    /**
     * Constructor mediante isntancia Singleton
     *
     * @param contexto Coontexto de la palicación
     * @return instancia de Controlador
     */
    public static ControladorLugares getControlador(Context contexto) {
        if (instancia == null) {
            instancia = new ControladorLugares();
        }
        //else{
        //    // Log.i("CL", "Usando el controlador Lugares existente ");
        //}
        context = contexto;
        return instancia;
    }

    /**
     * Lista todos los lugares almacenados en el sistema de almacenamiento
     *
     * @param filtro Filtro de ordenación
     * @return lista de lugares
     */
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

            /* Ejemplo de cada campo de la consulta
            String table = "table2";
            String[] columns = {"column1", "column3"};
            String selection = "column3 =?";
            String[] selectionArgs = {"apple"};
            String groupBy = null;
            String having = null;
            String orderBy = "column3 DESC";
            String limit = "10";

            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
             */

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
    // Manejar un CRUD
    // https://parzibyte.me/blog/2019/02/04/tutorial-sqlite-android-crud-create-read-update-delete/

    /**
     * Inserta un lugar en el sistema de almacenamiento
     *
     * @param lugar Lugar a insertar
     * @return verdadero si insertado
     */
    public boolean insertarLugar(Lugar lugar) {
        // se insertan sin problemas porque lugares es clave primaria, si ya están no hace nada
        // Abrimos la BD en modo escritura
        ControladorBD bdLugares = new ControladorBD(context, ControladorBD.db_lugares, null, 1);
        SQLiteDatabase bd = bdLugares.getWritableDatabase();
        boolean sal = false;
        try {
            //Cargamos los parámetros
            ContentValues valores = new ContentValues();
            valores.put("nombre", lugar.getNombre());
            valores.put("tipo", lugar.getTipo());
            valores.put("fecha", lugar.getFecha());
            valores.put("latitud", lugar.getLatitud());
            valores.put("longitud", lugar.getLongitud());
            valores.put("imagen", lugar.getImagen());
            // insertamos en su tabla, en long tenemos el id más alto creado
            long res = bd.insert("Lugares", null, valores);
            sal = true;
        } catch (SQLException ex) {
            Log.d("Lugares", "Error al insertar un nuevo lugar " + ex.getMessage());
        } finally {
            bd.close();
            bdLugares.close();
            return sal;
        }

    }

    /**
     * Elimina un lugar del sistema de almacenamiento
     *
     * @param lugar Lugar a eliminar
     * @return número de lugares eliminados.
     */
    public boolean eliminarLugar(Lugar lugar) {
        // Abrimos la BD en modo escritura
        ControladorBD bdLugares = new ControladorBD(context, ControladorBD.db_lugares, null, 1);
        SQLiteDatabase bd = bdLugares.getWritableDatabase();
        boolean sal = false;
        try {

            // Creamos el where
            String where = "id = ?";
            //Cargamos los parámetros es un vector, en este caso es solo uno, pero podrían ser mas
            String[] args = {String.valueOf(lugar.getId())};
            // En el fondo hemos hecho where id = lugar.id
            // Eliminamos. En res tenemos el numero de filas eliminadas por si queremos tenerlo en cuenta
            int res = bd.delete("Lugares", where, args);
            sal = true;
        } catch (SQLException ex) {
            Log.d("Lugares", "Error al eliminar este lugar " + ex.getMessage());
        } finally {
            bd.close();
            bdLugares.close();
            return sal;
        }

    }

    /**
     * Actualiza un lugar en el sistema de almacenamiento
     *
     * @param lugar Lugar a actualizar
     * @return número de lugares actualizados
     */
    public boolean actualizarLugar(Lugar lugar) {
        // Abrimos la BD en modo escritura
        ControladorBD bdLugares = new ControladorBD(context, ControladorBD.db_lugares, null, 1);
        SQLiteDatabase bd = bdLugares.getWritableDatabase();
        boolean sal = false;
        try {
            // Cargamos los valores
            ContentValues valores = new ContentValues();
            valores.put("nombre", lugar.getNombre());
            valores.put("tipo", lugar.getTipo());
            valores.put("fecha", lugar.getFecha());
            valores.put("latitud", lugar.getLatitud());
            valores.put("longitud", lugar.getLongitud());
            valores.put("imagen", lugar.getImagen());

            // Creamos el where
            String where = "id = ?";
            //Cargamos los parámetros es un vector, en este caso es solo uno, pero podrían ser mas
            String[] args = {String.valueOf(lugar.getId())};
            // En el fondo hemos hecho where id = lugar.id
            // Actualizamos. En res tenemos el numero de filas actualizadas por si queremos tenerlo en cuenta
            int res = bd.update("Lugares", valores, where, args);
            sal = true;
        } catch (SQLException ex) {
            Log.d("Lugares", "Error al actualizar este lugar " + ex.getMessage());
        } finally {
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
