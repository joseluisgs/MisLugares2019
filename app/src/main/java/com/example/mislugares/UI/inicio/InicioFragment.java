package com.example.mislugares.UI.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mislugares.MainActivity;
import com.example.mislugares.R;

/**
 * Clase de inicio
 */
public class InicioFragment extends Fragment {

    private TextView txtHomeTitulo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        // Obtengo la vista
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Obtenego los elementos interactivos
        txtHomeTitulo = root.findViewById(R.id.txtHomeTitulo);

        // Elementos de la interfaz
        actualizarInterfaz();

        return root;
    }

    private void actualizarInterfaz() {
        // Actualizo menú y botones
        try {
            ((MainActivity) getActivity()).ocultarElementosIU();
        } catch (Exception e) {
            //Log.e("ATRAS", "Home");
        }
    }
}