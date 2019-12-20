package com.example.mislugares.UI.lugares;

import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mislugares.Controladores.ControladorLugares;
import com.example.mislugares.MainActivity;
import com.example.mislugares.Modelos.Lugar;
import com.example.mislugares.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class LugaresFragment extends Fragment {

    private static final int INSERTAR = 1;
    private static final int ELIMINAR = 2;
    private static final int ACTUALIZAR = 3;
    private static final int VOZ = 10;

    private ArrayList<Lugar>lugares =new ArrayList<>();
    private RecyclerView rv;
    private LugaresListAdapter ad;

    // Botones
    private FloatingActionButton fabNuevo;
    private FloatingActionButton fabVoz;
    private Paint p = new Paint();

    private SwipeRefreshLayout swipeRefreshLayout;

    // Valores del spinner
    private Spinner spinnerFiltro;
    private String[] listaFiltro =
            {"Filtros", "Ordenar por nombre", "Ordenar por fecha","Ordenar por tipo"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Cargamos su Layout
        return inflater.inflate(R.layout.fragment_lugares, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtenemos los elementos de la interfaz
        iniciarComponentesIU();

        // iniciamos los eventos Asociados
        iniciarEventosIU();

        // Activamos la acción Swipe Reccargar
        iniciarSwipeRecarga();

        // Gestión del Spinner
        gestionSpinner();

        // Mostramos las vistas de listas y adaptador asociado
        rv = getView().findViewById(R.id.recyclerLugares);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        iniciarSwipeHorizontal();

        // Elementos de la interfaz
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        // Oculto lo que no me interesa
        ((MainActivity) getActivity()).ocultarElementosIU();

        // Activamos el home
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Muestro los elementos de menú que quiero en este fragment

    }

    // Iniciamos los componentes
    private void iniciarComponentesIU(){
        fabNuevo = getView().findViewById(R.id.fabLugaresNuevo);
        fabVoz = getView().findViewById(R.id.fabLugaresVoz);
    }

    // iniciamos los eventos de la IU
    private void iniciarEventosIU(){
        // Enviamos el email
        fabNuevo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               nuevoLugar();
            }
        });
        fabVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlVoz();
            }
        });


    }

    // Evento del gesto Swipe hacia abajo
    private void iniciarSwipeRecarga() {
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayoutLugares);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.textColor);
                //Le pasamos el fragment manager para gestionar las transacciones necesarias
                // Consultamos los lugares y se lo pasamos al adaptador
                listarLugares("");
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    // Metodos de control por voz
    private void controlVoz(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //reconoce en el idioma del telefono
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cómo quieres ordenar la lista?");
        try {
            startActivityForResult(intent, VOZ);
        } catch (Exception e) {
        }
    }

    // Se llama en su activity result
    // Se ejecuta al llamar a starActivityforResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_CANCELED) {
            return;
        }


        if (requestCode == VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> voz = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // Analizamos los que nos puede llegar
                String secuencia="";
                String tipoFiltro;
                // Concatenamos todo lo que tiene la cadena encontrada para buscar palabras clave
                for(String v: voz){
                    secuencia += " " + v;
                }

                // A partir de aquí podemos crear el if todo lo complejo que queramos o irnos a otro fichero
                // O métpdp
                if (secuencia != null) {
                    tipoFiltro = analizarFiltroVoz(secuencia);
                    //Log.d("Filtro", secuencia);
                    //Log.d("Filtro", tipoFiltro);
                    this.listarLugares(tipoFiltro);
                }
            }

        }
    }

    private String analizarFiltroVoz(String secuencia) {
        String tipoFiltro;
        // Nombre
        if ((secuencia.contains("nombre")) &&
                !((secuencia.contains("descendente")|| secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(nombre) ASC";
        }else if ((secuencia.contains("nombre")) &&
                ((secuencia.contains("descendente")|| secuencia.contains("inverso")))){

            tipoFiltro = "UPPER(nombre) DESC";
            // Fecha
        }else if ((secuencia.contains("fecha"))  &&
                !((secuencia.contains("descendente")|| secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(fecha) ASC";
        }else if ((secuencia.contains("fecha")) &&
                ((secuencia.contains("descendente") || secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(fecha) DESC";

            // Tipo
        }else if ((secuencia.contains("tipo"))  &&
                !((secuencia.contains("descendente")|| secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(tipo) ASC";
        }else if ((secuencia.contains("tipo")) &&
                ((secuencia.contains("descendente") || secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(tipo) DESC";

            // Lugar = nombre
        }else if ((secuencia.contains("lugar"))  &&
        !((secuencia.contains("descendente")|| secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(nombre) ASC";
        }else if ((secuencia.contains("lugar")) &&
                ((secuencia.contains("descendente")|| secuencia.contains("inverso")))){
            tipoFiltro = "UPPER(nombre) DESC";

            // Por defecto
        } else{
            tipoFiltro = "UPPER(nombre) ASC";
        }
        return tipoFiltro;
    }

    // Llamamos a nuevo lugar
    private void nuevoLugar(){
        LugarDetalleFragment detalle = new LugarDetalleFragment(INSERTAR);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, detalle);
        // animaciones opcionales
        transaction.setCustomAnimations(R.anim.animacion_fragment1,
                R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                R.anim.animacion_fragment1);
        transaction.addToBackStack(null);
        transaction.commit();

    }


     //Gestionamos el filtro a través de un spinner. En función de la posición del array
     //de strings que componen el adaptador del filtro, mandamos al método de consultar
     //un filtro u otro (que concatenamos a lla consulta del listado
     private void gestionSpinner() {
        this.spinnerFiltro = getView().findViewById(R.id.spinnerLugaresFiltro);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaFiltro);
        spinnerFiltro.setAdapter(dataAdapter);
        this.spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoFiltro = "";
                switch (spinnerFiltro.getSelectedItemPosition()) {
                    case 1:
                        tipoFiltro = "UPPER(nombre) ASC";
                        break;
                    case 2:
                        tipoFiltro = "DATE(fecha) ASC";
                        break;
                    case 3:
                        tipoFiltro = "UPPER(tipo) ASC";
                        break;
                    case 4:
                        tipoFiltro = "nombre ASC";
                        break;
                    default:
                        break;
                }
                // Listamos los lugares y cargamos el recycler
                listarLugares(tipoFiltro);
            }

            // Probar a quitar si puedes ;)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No hace nada,
            }
        });
    }



    // Swipe horizontal
    private void iniciarSwipeHorizontal() {
        // Eventos pata procesar los eventos de swipe, en nuestro caso izquierda y derecha
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // Sobreescribimos los métodos
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            // Analizamos el evento según la dirección
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Si pulsamos a la izquierda borramos el elemento
                if (direction == ItemTouchHelper.LEFT) {
                    borrarElemento(position);

                    // Si no lo actualizamos
                } else {
                    actualizarElemento(position);
                }
            }

            // Dibujamos los botones y eveneto. Nos lo creemos :):)
            // IMPORTANTE
            // Para que no te rebiente, las imagenes deben ser PNG
            // Así que añade un IMAGE ASEET bjándtelos de internet
            // https://material.io/resources/icons/?style=baseline
            // como PNG y cargas el de mayor calidad
            // de otra forma Bitmap no funciona bien
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // Si es dirección a la derecha: izquierda->derecta
                    // Pintamos de azul y ponemos el icono
                    if (dX > 0) {
                        // Pintamos el botón izquierdo
                        botonIzquierdo(c, dX, itemView);
                    } else {
                        // Pintamos el botón derecho
                        botonDerecho(c, dX, itemView);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        // Añadimos los eventos al RV
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }
    // Es que vamos de derecha a izquierda
    private void botonDerecho(Canvas c, float dX, View itemView) {
        // Pintamos de rojo y ponemos el icono
        Bitmap icon;
        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;
        p.setColor(Color.RED);
        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
        c.drawRect(background, p);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_eliminar_sweep);
        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
        c.drawBitmap(icon, null, icon_dest, p);
    }
    // Pintamos un botón izquierdo en la posicón del color que se indica
    private void botonIzquierdo(Canvas c, float dX, View itemView) {
        // Pintamos de azul y ponemos el icono
        Bitmap icon;
        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;
        p.setColor(Color.BLUE);
        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
        c.drawRect(background, p);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_detalles);
        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
        c.drawBitmap(icon, null, icon_dest, p);

    }

    // Actualizar elemento de la lista
    private void actualizarElemento(int position){
        Lugar lugar = lugares.get(position);
        FragmentManager fm = getFragmentManager();
        // Lo abrimos en modo actualizar
        LugarDetalleFragment detalle = new LugarDetalleFragment(lugar, ACTUALIZAR);
        // inicamos la transición
        FragmentTransaction transaction;
        transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.animacion_fragment1,
                R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                R.anim.animacion_fragment1);
        //Llamamos al replace
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();
        //Esto es para que no se quede con el color del deslizamiento
        ad.removeItem(position);
        ad.restoreItem(lugar, position);

    }

    // Borra un elemento de la lista
    private void borrarElemento(int position) {
        Lugar lugar = lugares.get(position);
        FragmentManager fm = getFragmentManager();
        // Lo abrimos en modo borrar
        LugarDetalleFragment detalle = new LugarDetalleFragment(lugar, ELIMINAR);
        // inciamos la transición
        FragmentTransaction transaction;
        transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.animacion_fragment1,
                R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                R.anim.animacion_fragment1);
        //Llamamos al replace
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();

    }

     //Consultamos el listado de lugares, limpiando antes la lista
    private void listarLugares(String filtro) {
        lugares.clear();
        ControladorLugares c = ControladorLugares.getControlador(getContext());
        lugares = c.listarLugares(filtro);
        ad = new LugaresListAdapter(lugares, getFragmentManager(), getResources());
        rv.setAdapter(ad);
        // Avismos que ha cambiado
        ad.notifyDataSetChanged();
        rv.setHasFixedSize(true);

    }


}