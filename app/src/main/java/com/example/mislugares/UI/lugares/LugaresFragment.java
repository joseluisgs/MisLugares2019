package com.example.mislugares.UI.lugares;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
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

public class LugaresFragment extends Fragment {

    private static final int INSERTAR = 1;
    private static final int ELIMINAR = 2;
    private static final int ACTUALIZAR = 3;

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
        rv = (RecyclerView) getView().findViewById(R.id.recyclerLugares);
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
        fabNuevo =(FloatingActionButton)getView().findViewById(R.id.fabLugaresNuevo);
        fabVoz = (FloatingActionButton)getView().findViewById(R.id.fabLugaresVoz);
    }

    // iniciamos los eventos de la IU
    private void iniciarEventosIU(){
        // Enviamos el email
        fabNuevo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               nuevoLugar();
            }
        });


    }

    // Evento del gesto Swipe hacia abajo
    private void iniciarSwipeRecarga() {
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayoutLugares);
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
        this.spinnerFiltro = (Spinner) getView().findViewById(R.id.spinnerLugaresFiltro);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaFiltro);
        spinnerFiltro.setAdapter(dataAdapter);
        this.spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoFiltro = "";
                switch (spinnerFiltro.getSelectedItemPosition()) {
                    case 1:
                        tipoFiltro = "nombre ASC";
                        break;
                    case 2:
                        tipoFiltro = "nombre ASC";
                        break;
                    case 3:
                        tipoFiltro = "fecha ASC";
                        break;
                    case 4:
                        tipoFiltro = "tipo ASC";
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
        p.setColor(Color.RED);
        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
        c.drawRect(background, p);
    }
    // Pintamos un botón izquierdo en la posicón del color que se indica
    private void botonIzquierdo(Canvas c, float dX, View itemView) {
        // Pintamos de azul y ponemos el icono
        p.setColor(Color.BLUE);
        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
        c.drawRect(background, p);
    }

    // Actualizar elemento de la lista
    private void actualizarElemento(int position){
        Lugar lugar = lugares.get(position);
        FragmentManager fm = getFragmentManager();
        // Lo abrimos en modo actualizar
        LugarDetalleFragment detalle = new LugarDetalleFragment(lugar, ACTUALIZAR);;
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