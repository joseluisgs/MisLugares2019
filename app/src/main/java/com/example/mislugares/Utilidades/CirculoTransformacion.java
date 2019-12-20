package com.example.mislugares.Utilidades;

import android.graphics.*;
import com.squareup.picasso.Transformation;

/**
 * Clase de trasformación para las imagen Picasso
 */
public class CirculoTransformacion implements Transformation {

    boolean mCircleSeparator = false;
    String color = "#ffffff";

    /**
     * Constructor
     */
    public CirculoTransformacion() {
    }

    /**
     * Constructor con color
     *
     * @param color Color
     */
    public CirculoTransformacion(String color) {
        this.color = color;
    }

    /**
     * Transformación
     *
     * @param circleSeparator Separador
     */
    public CirculoTransformacion(boolean circleSeparator) {
        mCircleSeparator = circleSeparator;
    }

    /**
     * Transmormación
     *
     * @param source Fuernte
     * @return Destino
     */
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setShader(shader);
        float r = size / 2f;
        canvas.drawCircle(r, r, r - 1, paint);

        Paint paintBorder = new Paint();
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(Color.argb(84, 0, 0, 0));
        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(1);
        canvas.drawCircle(r, r, r - 1, paintBorder);


        if (mCircleSeparator) {
            Paint paintBorderSeparator = new Paint();
            paintBorderSeparator.setStyle(Paint.Style.STROKE);
            paintBorderSeparator.setColor(Color.parseColor(this.color));
            paintBorderSeparator.setAntiAlias(true);
            paintBorderSeparator.setStrokeWidth(4);
            canvas.drawCircle(r, r, r + 1, paintBorderSeparator);
        }
        squaredBitmap.recycle();
        return bitmap;
    }

    /**
     * Key
     *
     * @return
     */
    @Override
    public String key() {
        return "circle";
    }
}