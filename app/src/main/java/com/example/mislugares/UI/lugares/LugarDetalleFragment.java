package com.example.mislugares.UI.lugares;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.mislugares.MainActivity;
import com.example.mislugares.Modelos.Lugar;
import com.example.mislugares.R;
import com.example.mislugares.Utilidades.Utilidades;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executor;

public class LugarDetalleFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    // Permisos
    private static final int LOCATION_REQUEST_CODE = 1; // Para los permisos

    // Modo de visualización para el CRUD
    private int modo;
    private static final int INSERTAR = 1;
    private static final int ELIMINAR = 2;
    private static final int ACTUALIZAR = 3;
    private static final int VISUALIZAR = 4;


    // Lugar
    private Lugar lugar;

    // Variables de la interfaz
    private Spinner spinnerLugarDetalleTipo;
    private TextInputLayout etTipo;
    private EditText tvFecha;
    private Button btnFecha;
    private FloatingActionButton fabAccion;
    private TextInputLayout etNombre;
    private TextInputLayout etCoordenadas;
    private ImageView ivJuego;
    private FloatingActionButton btnCamara;
    // Otras
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    // Camara
    private static final int GALERIA = 1;
    private static final int CAMARA = 2;
    private Uri photoURI;
    private static final String IMAGE_DIRECTORY = "/lugares";
    private static final int PROPORCION = 600;

    // Mapa
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private LatLng posicion;
    // Marcador
    private Marker marcador = null;
    // Posicion
    private FusedLocationProviderClient mPosicion;
    private Location localizacion;




    // Le pasamos el tipo de modo y objeto para activar o desactivar controles
    // Edición y Actualización --> Los componentes del fragment estarán activados
    // Visualización y Borrado --> Los componentes del fragment estarán desactivados

    public LugarDetalleFragment() {
        this.modo = INSERTAR;
    }

    @SuppressLint("ValidFragment")
    public LugarDetalleFragment(int modo) {
        this.modo = modo;
    }

    public LugarDetalleFragment(Lugar lugar, int modo) {
        this.lugar = lugar;
        this.modo = modo;
        //Para el evento de compartir, que se gestiona en el main (es donde se crea el Toolbar)
        //juegoMain = juego;
    }


    public static LugarDetalleFragment newInstance() {
        return new LugarDetalleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lugar_detalle, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        // Obtenemos los elementos de la interfaz
        iniciarComponentesIU();

        // iniciamos los eventos Asociados
        iniciarEventosIU();

        iniciarSpiner();

        // iniciar Mapa
        iniciarMapa();

        // Procesamos el lugar
        procesarModoVisualizacion();
        //Para mostrar el item de compartir

        // Elementos de la interfaz
        actualizarInterfaz();

    }

    // Enlazamos los elementos de la interfaz
    private void iniciarComponentesIU() {
        // Tipo
        this.spinnerLugarDetalleTipo = (Spinner) getView().findViewById(R.id.spinnerLugarDetalleTipo);
        this.etTipo = (TextInputLayout) getView().findViewById(R.id.tvDetalleLugarTipo);
        // Fecha
        this.tvFecha = (EditText) getView().findViewById(R.id.etDetalleLugarFecha);
        this.btnFecha = (Button) getView().findViewById(R.id.btnFecha);

        // Botones
        this.fabAccion = (FloatingActionButton) getView().findViewById(R.id.fabDetalleLugarAccion);
        this.btnCamara = (FloatingActionButton) getView().findViewById(R.id.ibDetalleLugarCamara);
        //
        this.etNombre = (TextInputLayout) getView().findViewById(R.id.tvDetalleLugarNombre);
        //this.etCoordenadas = (TextInputLayout) getView().findViewById(R.id.tvDetalleLugarCoordenadas);
        this.ivJuego = (ImageView) getView().findViewById(R.id.ivDetalleLugar);


    }

    // iniciamos los eventos de la IU
    private void iniciarEventosIU() {
        // boton fecha
        btnFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                escogerFecha();
                tvFecha.setEnabled(false);
            }
        });
        // Botón insertar
        fabAccion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                realizarAccion();
            }
        });
        // boton camara
        btnCamara.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tomarFoto();
                tvFecha.setEnabled(false);
            }
        });

    }


    // Procesamos el modo de visualización según el modo
    // Activando los componentes oporrunos
    private void procesarModoVisualizacion() {
        switch (this.modo) {
            case VISUALIZAR:
                modoVisualizar();
                break;
            case INSERTAR:
                modoInsertar();
                break;
            case ELIMINAR:
                modoEliminar();
                break;
            case ACTUALIZAR:
                modoActualizar();
                break;
            default:
                break;
        }
    }



    // Desactiva los componentes
    private void desactivarComponentesIU() {
        this.etNombre.setEnabled(false);
        this.tvFecha.setEnabled(false);
        this.etCoordenadas.setEnabled(false);
        this.etTipo.setEnabled(false);
        this.spinnerLugarDetalleTipo.setEnabled(false);
        this.btnFecha.setEnabled(false);
        this.btnCamara.hide();
    }

    // Mostramos los datos del lugar
    private void mostrarDatosLugar(){
        // Datos
        this.etNombre.getEditText().setText(this.lugar.getNombre());
        this.tvFecha.setText(this.lugar.getFecha());
        this.etCoordenadas.getEditText().setText("Lat: "+ String.valueOf(this.lugar.getLatitud() +
                "Long: "+ String.valueOf(this.lugar.getLongitud())));
        this.etTipo.getEditText().setText(this.lugar.getTipo());
        // Procesamos la imagen proprocional
        Bitmap imagen = Utilidades.base64ToBitmap(lugar.getImagen());
        float prop= PROPORCION / (float) imagen.getWidth();
        Bitmap imagenLugar = Bitmap.createScaledBitmap(imagen, PROPORCION, (int) (imagen.getHeight() * prop), false);
        this.ivJuego.setImageBitmap(imagenLugar);
    }

    private void modoInsertar() {
        // Ponemos el spinner
        iniciarSpiner();
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#9171CF")));
    }

    // Actualiza los datos
    private void modoActualizar() {
        mostrarDatosLugar();
        iniciarSpiner();
        fabAccion.setImageResource(R.drawable.ic_actualizar);
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("33FFDA")));
    }

    // Elimina los datos
    private void modoEliminar() {
        desactivarComponentesIU();
        mostrarDatosLugar();
        this.fabAccion.setImageResource(R.drawable.ic_eliminar);
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("FF3377")));
    }

    // Modo visualizar
    private void modoVisualizar() {
        desactivarComponentesIU();
        mostrarDatosLugar();
        iniciarSpiner(); // Esto lo quietaré
        this.fabAccion.hide();
    }

    // Actualizamos la interfaz y los menús que heredamos según nos convenga
    private void actualizarInterfaz() {
        // Oculto lo que no me interesa
        ((MainActivity) getActivity()).ocultarElementosIU();

        // Muestro los elementos de menú que quiero en este fragment
        // Menú
        //((MainActivity) getActivity()).getMenu().findItem(R.id.menu_compartir).setVisible(true);
        ((MainActivity) getActivity()).getMenu().findItem(R.id.menu_atras).setVisible(true);

        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }


     //Gestión del spinner. le pasamos como se va a mostrar el layout y la lista que va a recibir.
     //Se trata de un proceso muy parecido al de construir un adaptador para un recyclerView
     //solo que este ya nos lo "regala" Android
    private void iniciarSpiner(){
            // Leemos los datos del XML
                ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.tipos_lugares,
                        android.R.layout.simple_spinner_dropdown_item);

            this.spinnerLugarDetalleTipo.setAdapter(adapter);

            // Su listener
            this.spinnerLugarDetalleTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //El valor escogido en el spinner se lo seteamos al TextInputLayout de la plataforma
                    etTipo.getEditText().setText(spinnerLugarDetalleTipo.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Nada
                }
            });
        }

    // Escogemos la fecha
    private void escogerFecha() {
         calendar = Calendar.getInstance();
         int day = calendar.get(Calendar.DAY_OF_MONTH);
         int month = calendar.get(Calendar.MONTH);
         int year = calendar.get(Calendar.YEAR);

        //Abrimos el DataPickerDialog
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                tvFecha.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                //Log.i("Fecha",mDay + " " + mMonth + " " + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // Realiza una acción según el modo
    private void realizarAccion() {
        switch (modo) {
            case INSERTAR:
                insertarLugar();
                break;
            case ELIMINAR:
                eliminarLugar();
                break;
            case ACTUALIZAR:
                actualizarLugar();
            default:
                break;
        }
    }

    private void actualizarLugar() {
        Snackbar.make(getView(), "¡Lugar actualizado!", Snackbar.LENGTH_LONG).show();
    }

    private void eliminarLugar() {
    }

    // Inserta un lugar en la base de datos
    private void insertarLugar() {
        Snackbar.make(getView(), "¡Lugar añadido!", Snackbar.LENGTH_LONG).show();
    }

    // Tomamos la foto y seleccionamos de la galeria
    private void tomarFoto() {
        AlertDialog.Builder fotoDialogo = new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Seleccionar Acción");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara"};
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    private void tomarFotoCamara() {
        // Si queremos hacer uso de fotos en aklta calidad
        // Primero miramos si complimos todo
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Eso para alta o baja
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Esto para alta calidad
        photoURI = Uri.fromFile(Utilidades.salvarFoto(this.IMAGE_DIRECTORY));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);

        // Esto para alta y baja
        startActivityForResult(intent, CAMARA);
    }



    // Elige la foto de la galería
    private void elegirFotoGaleria() {
        // Abrimos el itent de la galería
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALERIA);
    }

    // Resultado del intent de Galería
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_CANCELED) {
            return;
        }
        // Elegimos la foto de la galería
        if (requestCode == GALERIA) {
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    // Actualizamos el tamaño
                    float prop = PROPORCION / (float) bitmap.getWidth();
                    // Actuializamos el bitmap para ese tamaño
                    Bitmap foto = Bitmap.createScaledBitmap(bitmap, PROPORCION, (int) (bitmap.getHeight() * prop), false);
                    // Asignamos la imagen
                    this.ivJuego.setImageBitmap(foto);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "¡Fallo Galeria!", Toast.LENGTH_SHORT).show();
                }
            }
            // Echamos la foto
        } else if (requestCode == CAMARA) {
            // Cogemos la imagen, pero podemos coger la imagen o su modo en baja calidad (thumbnail
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoURI);
                //ponemos la imagen
                this.ivJuego.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Fallo al guardar", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void iniciarMapa(){
        // Inicio la localizacion
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());

        // Para Obtener el mapa dentro de un Fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mMap);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mMap, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Queda cambiar los modos

        mMap = googleMap;

        // Configuramos el botón de localización
        mMap.setMyLocationEnabled(true);

        // Cargamos el evento del mapa click en marcador
        mMap.setOnMarkerClickListener(this);
        // Mapa híbrido, lo normal es usar el
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Que se vea la interfaz y la brújula por ejemplo
        // También podemos quitar gestos
        UiSettings uiSettings = mMap.getUiSettings();
        // Activamos los gestos
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        // Activamos los controles de zoom
        uiSettings.setZoomControlsEnabled(true);
        // Actiovamos la barra de herramientas
        uiSettings.setMapToolbarEnabled(true);

        // Hacemos el zoom por defecto mínimo
        mMap.setMinZoomPreference(12.0f);

        // Activo el evento del marcador
        activarEventosMarcdores();
        
        obtenerPosicion();

        // La llevamos a un lugar la camara
        // LatLng ll = new LatLng(38.9860385, -3.9620074);
        // Movemos la camara
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    private void obtenerPosicion() {
        try {
            if (true) {
                // Lo lanzamos como tarea concurrente
                Task<Location> local = mPosicion.getLastLocation();
                local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Actualizamos la última posición conocida
                            localizacion = task.getResult();
                            posicion = new LatLng(localizacion.getLatitude(),
                                    localizacion.getLongitude());
                            // Añadimos un marcador especial para poder operar con esto
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));

                        } else {
                            Log.d("GPS", "No se encuetra la última posición.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    private void activarEventosMarcdores() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Creamos el marcador
                // Borramos el marcador Touch si está puesto
                if (marcador != null) {
                    marcador.remove();
                }
                marcador = mMap.addMarker(new MarkerOptions()
                        // Posición
                        .position(point)
                        // Título
                        .title("Nuevo Lugar")
                        // Subtitulo
                        //.snippet("Tu")
                        // Color o tipo d icono
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

            }
        });
    }

        @Override
        public boolean onMarkerClick(Marker marker) {
            // Si pulsas ayunatmiento, si muestro el toast si no nada
            //String titulo = marker.getTitle();
            //Toast.makeText(getContext(),"Estás en: " + marker.getPosition().latitude+","+marker.getPosition().longitude,
            //                Toast.LENGTH_SHORT).show();
            return false;
        }

}
