package com.example.mislugares.Utilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Clase Utiidades
 */
public class Utilidades {

    /**
     * Convierte una cadena Base64 a Bitmap
     *
     * @param b64String cadena Base 64
     * @return Bitmap
     */
    public static Bitmap base64ToBitmap(String b64String) {
        byte[] imageAsBytes = Base64.decode(b64String.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * Convierte un Bitmap a una cadena Base64
     *
     * @param bitmap Bitmap
     * @return Cadena Base74
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        // Comrimimos al 60 % la imagen
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    /**
     * Salva una imagen en un directorio publico
     *
     * @param path Path
     * @return Apuntador File al lugar
     */
    public static File salvarFoto(String path) {
        // Nombre del fichero
        String fichero = crearNombreFichero();
        return salvarFicheroPublico(path, fichero);
    }

    /**
     * Función para opbtener el nombre del fichero
     *
     * @return Nombre del fichero
     */
    private static String crearNombreFichero() {
        return "lugares" + Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    /**
     * Salva un fichero en un directorio
     *
     * @param path   Path de almacenamiento
     * @param nombre nombre del fichero
     * @return Apuntador File del fichero
     */
    private static File salvarFicheroPublico(String path, String nombre) {
        // Vamos a obtener los datos de almacenamiento externo
        File dirFotos = new File(Environment.getExternalStorageDirectory() + path);
        // Si no existe el directorio, lo creamos solo si es publico
        if (!dirFotos.exists()) {
            dirFotos.mkdirs();
        }
        try {
            File f = new File(dirFotos, nombre);
            f.createNewFile();
            return f;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }


}
