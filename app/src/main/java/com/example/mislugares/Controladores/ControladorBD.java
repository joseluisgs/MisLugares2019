package com.example.mislugares.Controladores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// Podemos hacer uno por cada Base de Datos o uno genérico
// Optaré por uno para todas

public class ControladorBD extends SQLiteOpenHelper {

    // Nombre del fichero de la BD que usaremos
    // Uno por cada base de datos que manejemos
    public static String db_lugares ="BDLugares";

    //Sentencia que creerá la tabla
    private final static String CREATE_TABLA_LUGAR = "CREATE TABLE Lugares " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre VARCHAR, " +
            "tipo VARCHAR, " +
            "fecha DATE," +
            "latitud REAL," +
            "longitud REAL, " +
            "imagen BLOB )";

            // Si quisisesemos hacer clave externa con tipos
            // Pero los gestionaemos con un string en values
            //"FOREIGN KEY (tipo) REFERENCES Tipos(tipo) )";

    // Esta la hago para cargar un combo box (Spinner)
    // No lo vamos a hacer así porque son valores estáticos
    // Mira en la clase string
    /*
    private final static String CREATE_TABLA_TIPO = "CREATE TABLE Tipos " +
            "(tipo VARCHAR(30) PRIMARY KEY)";

    */

    public ControladorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Crea la tabla de la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Cuando se llame al onCreate se realiza la sentencia
        //db.execSQL(CREATE_TABLA_TIPO);
        db.execSQL(CREATE_TABLA_LUGAR);

    }

    // Actualiza la tabla de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se borra y se vuelve a crear para "actualizar" la versión de la tabla
        db.execSQL("DROP TABLE IF EXISTS Lugares");
        //db.execSQL("DROP TABLE IF EXISTS Tipos");
        //db.execSQL(CREATE_TABLA_TIPO);
        db.execSQL(CREATE_TABLA_LUGAR);
    }
}
