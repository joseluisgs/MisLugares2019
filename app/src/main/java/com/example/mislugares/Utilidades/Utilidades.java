package com.example.mislugares.Utilidades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class Utilidades {

    // Pasamos de base64 a bitmap
    public static Bitmap base64ToBitmap(String b64String) {
        byte[] imageAsBytes = Base64.decode(b64String.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    // Bitmp a base 64
    public static String bitmapToBase64(Bitmap bitmap) {
        // Comrimimos al 60 %
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    // Salva una foto en el directorio público
    public static File salvarFoto(String path) {
        // Nombre del fichero
        String fichero = crearNombreFichero();
        return salvarFicheroPublico(path, fichero);
    }

    // Crea el nombre de la foto con el mombre el mili segudnos
    private static String crearNombreFichero() {
        return "lugares"+Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

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
