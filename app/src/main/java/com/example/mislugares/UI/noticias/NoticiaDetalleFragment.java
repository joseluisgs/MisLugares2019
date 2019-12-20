package com.example.mislugares.UI.noticias;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mislugares.MainActivity;
import com.example.mislugares.Modelos.Noticia;
import com.example.mislugares.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

/**
 * Fragmen de Noticia
 */
public class NoticiaDetalleFragment extends Fragment {

    // El Objeto
    private Noticia noticia;

    // Elementos de la interfaz
    private TextView tvDetalleTitulo;
    private WebView wvDetalleContenido;
    private ImageView ivDetalleImagen;
    private FloatingActionButton fabDetallesIr;

    // Esto debemos hacerlo porque la opción de copratir está en el menú y debe ser accesible
    // Como variable de clase
    public static Noticia noticiaActual;


    // Constructores
    public NoticiaDetalleFragment(Noticia noticia) {
        this.noticia = noticia;
        noticiaActual = noticia;
    }

    public NoticiaDetalleFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticia_detalle, container, false);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtenemos los elementos de la interfaz
        iniciarComponentesIU();

        // iniciamos los eventos Asociados
        iniciarEventosIU();

        // Procesamos la noticia
        procesarNoticia();
        //Para mostrar el item de compartir

        // Elementos de la interfaz
        actualizarInterfaz();


    }

    /**
     * Inicia la componentes de la IU
     */
    private void iniciarComponentesIU() {
        tvDetalleTitulo = getView().findViewById(R.id.tvNoticiaDetalleTitular);
        wvDetalleContenido = getView().findViewById(R.id.wvNoticiaDetalleContenido);
        ivDetalleImagen = getView().findViewById(R.id.ivNoticiaDetalleImagen);
        fabDetallesIr = getView().findViewById(R.id.fabNoticiaDetalleIr);
    }

    /**
     * Inicia los Eventos de la IU
     */
    private void iniciarEventosIU() {
        // Enviamos el email
        fabDetallesIr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                abrirEnlaceNavegador(noticia.getLink());
            }
        });


    }

    /**
     * Procesamos una noticia
     */
    private void procesarNoticia() {
        tvDetalleTitulo.setText(this.noticia.getTitulo());
        wvDetalleContenido.loadData(this.noticia.getContenido(), "text/html", null);
        Picasso.get().load(this.noticia.getImagen()).into(this.ivDetalleImagen);

    }


    /**
     * Llamamos al navegador con un enlace
     *
     * @param enlance
     */
    private void abrirEnlaceNavegador(String enlance) {
        Uri uri = Uri.parse(enlance);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    /**
     * ACtualizamos la interfaz de usuario
     */
    private void actualizarInterfaz() {
        // Oculto lo que no me interesa
        ((MainActivity) getActivity()).ocultarElementosIU();

        // Muestro los elementos de menú que quiero en este fragment
        // Menú
        ((MainActivity) getActivity()).getMenu().findItem(R.id.menu_compartir_noticia).setVisible(true);
        ((MainActivity) getActivity()).getMenu().findItem(R.id.menu_atras).setVisible(true);

        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


    }

}
