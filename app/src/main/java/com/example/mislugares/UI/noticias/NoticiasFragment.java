package com.example.mislugares.UI.noticias;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.mislugares.Controladores.ControladorRSS;
import com.example.mislugares.MainActivity;
import com.example.mislugares.Modelos.Noticia;
import com.example.mislugares.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Fragmen para el manejo de ls noticias
 */
public class NoticiasFragment extends Fragment {

    private RecyclerView rv; // Recycler donde pondremos las cosas
    private NoticiasListAdapter ad; // Adaptador de noticias
    private ArrayList<Noticia> noticias; // ArrayList de noticias
    private TareaCargarNoticias tarea;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View root; // Vista raiz
    private Paint p = new Paint();

    // Dirección del RSS
    private String direccionRSS = "http://ep00.epimg.net/rss/tags/ultimas_noticias.xml";


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_noticias, container, false);
        return root;
    }

    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        // Obtenemos los elementos de la interfaz
        //iniciarComponentesIU();

        // Activamos la acción Swipe Reccargar
        iniciarSwipeRecarga();
        //Log.d("Noticias","Swipe Horizontal");

        // Cargamos los Datos la primera vez
        tarea = new TareaCargarNoticias();
        // Vamos a cargar el RSS del Pais
        tarea.execute(direccionRSS);
        //Log.d("Noticias","Carga de Datos OK");

        // Mostramos las vistas de listas y adaptador asociado
        rv = getView().findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        //Log.d("Noticias","asignado al RV");

        // Iniciamos el Swipe Horizontal
        iniciarSwipeHorizontal();
        //Log.d("Noticias","Wipe Horizontal OK");

        // Elementos de la interfaz
        actualizarInterfaz();
        //Log.d("Noticias","Actualizar Interfaz OK");
    }

    /**
     * Actualiza la interfaz de IU
     */
    private void actualizarInterfaz() {
        // Oculto lo que no me interesa
        ((MainActivity) getActivity()).ocultarElementosIU();

        // Activamos el home
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Muestro los elementos de menú que quiero en este fragment

    }

    /**
     * Inicia los componentes de la IU
     */
    private void iniciarComponentesIU() {
        return;
    }


    /**
     * Evento de Swipe de Recarga
     */
    private void iniciarSwipeRecarga() {

        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.textColor);
                //Al refrescar llama a la tarea asíncrona
                tarea = new TareaCargarNoticias();
                tarea.execute(direccionRSS);
            }
        });
    }

    /**
     * Swipe Horizontal
     */
    private void iniciarSwipeHorizontal() {
        // Eventos pata procesar los eventos de swipe, en nuestro caso izquierda y derecha
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {

                    // Sobreescribimos los métodos
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    // Analizamos el evento según la dirección
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();

                        // Si pulsamos a la de izquierda a la derecha
                        // Borramos
                        if (direction == ItemTouchHelper.LEFT) {
                            // Log.d("Noticias", "Tocado izquierda");
                            borrarElemento(position);

                            // Si no editamos
                        } else {
                            //  Log.d("Noticias", "Tocado derecha");
                            verElemento(position);

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
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        Bitmap icon;
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            View itemView = viewHolder.itemView;
                            float height = (float) itemView.getBottom() - (float) itemView.getTop();
                            float width = height / 3;
                            // Si es dirección a la derecha: izquierda->derecta
                            // Pintamos de azul y ponemos el icono
                            if (dX > 0) {
                                // Pintamos el botón izquierdo
                                botonIzquierdo(c, dX, itemView, width);

                            } else {
                                // Caso contrario
                                botonDerecho(c, dX, itemView, width);
                            }
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        // Añadimos los eventos al RV
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    /**
     * Al pulsar el botón derecho del swipe
     *
     * @param c
     * @param dX
     * @param itemView
     * @param width
     */
    private void botonDerecho(Canvas c, float dX, View itemView, float width) {
        // Pintamos de rojo y ponemos el icono
        Bitmap icon;
        p.setColor(Color.RED);
        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
        c.drawRect(background, p);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_eliminar_sweep);
        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
        c.drawBitmap(icon, null, icon_dest, p);
    }

    /**
     * AL pulsar el botón izquiero del swipe
     *
     * @param c
     * @param dX
     * @param itemView
     * @param width
     */
    private void botonIzquierdo(Canvas c, float dX, View itemView, float width) {
        // Pintamos de azul y ponemos el icono
        Bitmap icon;
        p.setColor(Color.BLUE);
        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
        c.drawRect(background, p);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_detalles);
        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
        c.drawBitmap(icon, null, icon_dest, p);
    }

    /**
     * Ver el elemnto aosicado en una posición
     *
     * @param position
     */
    private void verElemento(int position) {
        /**
         * Transacción entre fragments. Lo primero es llamar al fragment manager
         * A continuación instanciamos un objeto fragment correspondiente al
         * fragment que va a entrar y le pasamos el objeto noticia en la posición de
         * la lista correspondiente. Iniciamos la transacción, dándole si queremos
         * animaciones. Lo más importante es el replace, en el que remplazamos el
         * host por el objeto fragment detalle
         */
        Noticia noticia = noticias.get(position);
        FragmentManager fm = getFragmentManager();
        //Instanciamos un objeto de nuestro nuevo fragment
        NoticiaDetalleFragment detalle = new NoticiaDetalleFragment(noticia);
        FragmentTransaction transaction = fm.beginTransaction();
        // animaciones
        transaction.setCustomAnimations(R.anim.animacion_fragment1,
                R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                R.anim.animacion_fragment1);
        //Llamamos al replace
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();
        //Esto es para que no se quede con el color del deslizamiento
        ad.removeItem(position);
        ad.restoreItem(noticia, position);
    }

    /**
     * Borra un elemento de una posoción
     *
     * @param position
     */
    private void borrarElemento(int position) {
        final Noticia deletedModel = noticias.get(position);
        final int deletedPosition = position;
        ad.removeItem(position);

        // Mostramos la barra. Se la da opción al usuario de recuperar lo borrado con el el snackbar
        Snackbar snackbar = Snackbar.make(getView(), "Noticia eliminada", Snackbar.LENGTH_LONG);
        snackbar.setAction("DESHACER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                ad.restoreItem(deletedModel, deletedPosition);
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * Comprueba si tenemos conexión
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) root.getContext().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Tarea asíncrona para la carga de noticias
     */
    class TareaCargarNoticias extends AsyncTask<String, Void, Void> {

        // Primero comprobamos si hay internet, si no lo hay, pintamos en el thread de la
        //interfaz de usuario un snackbar que avise de que no hay conexión, y le decimos
        // al refreshlayout que deje de refrescar.  Tambíen pararemos la ejecución de la
        // tarea asíncrona (no se ejecutará el doInBackground)

        /**
         * Acciones ante sde ejecutarse
         */
        @Override
        protected void onPreExecute() {
            if (!isNetworkAvailable()) {

                this.cancel(true);
                ((Activity) root.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(getView(), "Es necesario una conexión a internet. Por favor activa la conexión",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            //Log.d("Prueba", "onPreExecute OK");
        }

        /**
         * Procedimiento asíncrono
         *
         * @param url
         * @return
         */
        @Override
        protected Void doInBackground(String... url) {
            //Log.d("Noticias", "Entrado en doInBackgroud con: " + url[0]);

            try {
                ControladorRSS parseador = ControladorRSS.getControlador(url[0]);
                noticias = parseador.getNoticias();
                //Log.d("Noticias", "Noticias tamaño: " + noticias.size());
            } catch (Exception e) {
                //Log.e("T2Plano ", e.getMessage());
            }
            // Log.d("Noticias", "onDoInBackgroud OK");
            return null;
        }


        /**
         * Procedimiento a realizar al terminar
         * Cargamos la lista
         *
         * @param args
         */
        @Override
        protected void onPostExecute(Void args) {
            //Log.d("Noticias", "entrando en onPostExecute");
            ad = new NoticiasListAdapter(noticias, getFragmentManager());
            rv.setAdapter(ad);
            // Avismos que ha cambiado
            ad.notifyDataSetChanged();
            rv.setHasFixedSize(true);
            swipeRefreshLayout.setRefreshing(false);
            //Log.d("Noticias", "onPostExecute OK");
        }


    }

}
