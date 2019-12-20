package com.example.mislugares.UI.acerca_de;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mislugares.MainActivity;
import com.example.mislugares.R;
import com.example.mislugares.Utilidades.CirculoTransformacion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

/**
 * Clase Acerca De
 */
public class AcercaDeFragment extends Fragment {
    private ImageView ivAcercaDeFoto;
    private FloatingActionButton fabEmail;
    private FloatingActionButton fabTwitter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Cargamos el layout del fragment
        return inflater.inflate(R.layout.fragment_acerca_de, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtenemos los elementos de la interfaz
        iniciarComponentesIU();

        // iniciamos los eventos Asociados
        iniciarEventosIU();

        // Mejoramos la foto con Picasso
        // Foto redonda con Picasso, redondo blanco
        Picasso.get().load(R.drawable.autor)
                .transform(new CirculoTransformacion("D0BAF7"))
                .into(ivAcercaDeFoto);

        // Elementos de la interfaz
        actualizarInterfaz();


    }

    /**
     * Inicia los componentes de la interfaz
     */
    private void iniciarComponentesIU() {
        // Botones
        fabEmail = getView().findViewById(R.id.fabEmailAcercaDe);
        fabTwitter = getView().findViewById(R.id.fabTwitterAcercaDe);
        //Imagenes
        ivAcercaDeFoto = getView().findViewById(R.id.ivAcercaDeFoto);
    }

    /**
     * Iniciamos los Eventos de la IU
     */
    private void iniciarEventosIU() {
        // Enviamos el email
        fabEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).enviarCorreoElectronico("jlgs@cifpvirgendegracia.com", "", "Mis Lugares", "Este es un email de prueba para 2º DAM");
            }
        });

        // Twitter
        fabTwitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/joseluisgonsan"));
                startActivity(intent);
            }
        });
    }

    /**
     * Actualizamos la Interfaz
     */
    private void actualizarInterfaz() {
        // Oculto lo que no me interesa
        ((MainActivity) getActivity()).ocultarElementosIU();

        // Muestro los elementos de menú que quiero en este fragment
        // Menú
        ((MainActivity) getActivity()).getMenu().findItem(R.id.menu_atras).setVisible(true);

        // Botones
        //((MainActivity) getActivity()).getFabEmail().show();

    }

}
