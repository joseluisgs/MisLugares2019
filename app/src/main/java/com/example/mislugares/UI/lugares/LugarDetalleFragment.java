package com.example.mislugares.UI.lugares;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.mislugares.Controladores.ControladorLugares;
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
    // Eso se hace para compartir
    public static Lugar lugarActual;

    // Variables de la interfaz
    private Spinner spinnerLugarDetalleTipo;
    private TextInputLayout etTipo;
    private EditText tvFecha;
    private Button btnFecha;
    private FloatingActionButton fabAccion;
    private TextInputLayout etNombre;
    private ImageView ivLugar;
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
    private Bitmap imagen;

    // Mapa
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    // Marcador
    private Marker marcador = null;
    // Posicion
    private FusedLocationProviderClient mPosicion;
    private Location localizacion;
    private LatLng posicion;






    // Le pasamos el tipo de modo y objeto para activar o desactivar controles
    // Edición y Actualización --> Los componentes del fragment estarán activados
    // Visualización y Borrado --> Los componentes del fragment estarán desactivados

    public LugarDetalleFragment() {
        this.modo = INSERTAR;
    }

    public LugarDetalleFragment(int modo) {
        this.modo = modo;
    }

    public LugarDetalleFragment(Lugar lugar, int modo) {
        this.lugar = lugar;
        this.modo = modo;
        lugarActual = lugar;
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
        this.ivLugar = (ImageView) getView().findViewById(R.id.ivDetalleLugar);

        // Procesamos la fecha
        calendar = Calendar.getInstance();
        tvFecha.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1)
                + "/" + calendar.get(Calendar.YEAR));

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
        this.etTipo.getEditText().setText(this.lugar.getTipo());
        // Procesamos la imagen proprocional
        Bitmap imagen = Utilidades.base64ToBitmap(lugar.getImagen());
        float prop= PROPORCION / (float) imagen.getWidth();
        Bitmap imagenLugar = Bitmap.createScaledBitmap(imagen, PROPORCION, (int) (imagen.getHeight() * prop), false);
        this.ivLugar.setImageBitmap(imagenLugar);
        this.imagen = imagenLugar;


    }

    private void modoInsertar() {
        // Ponemos el spinner
        iniciarSpiner();
        //ponemos el botón del color e icono
        fabAccion.setImageResource(R.drawable.ic_lugar);
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#52B0EC")));

        // Ponemos como foto, la foto por defecto
        this.imagen = ((BitmapDrawable)this.ivLugar.getDrawable()).getBitmap();

    }

    // Actualiza los datos
    private void modoActualizar() {
        mostrarDatosLugar();
        iniciarSpiner();
        fabAccion.setImageResource(R.drawable.ic_actualizar);
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#6699ff")));
    }

    // Elimina los datos
    private void modoEliminar() {
        desactivarComponentesIU();
        mostrarDatosLugar();
        this.fabAccion.setImageResource(R.drawable.ic_eliminar);
        fabAccion.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#FF3377")));
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
        ((MainActivity) getActivity()).getMenu().findItem(R.id.menu_compartir_lugar).setVisible(true);
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
            // Leemos los datos del XML lo cogemos del XML Strings
                ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.tipos_lugares,
                        android.R.layout.simple_spinner_dropdown_item);

            this.spinnerLugarDetalleTipo.setAdapter(adapter);

            // Su listener
            this.spinnerLugarDetalleTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //El valor escogido en el spinner se lo ponemos al TextInputLayout de Tipo
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


        //Abrimos el DataPickerDialog
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                tvFecha.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                //Log.i("Fecha",mDay + " " + mMonth + " " + mYear);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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

    // Elimina un lugar
    private void eliminarLugar() {
        // Mostramos el dialogo de eliminar
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());

        deleteDialog.setTitle("¿Estás seguro de querer eliminar el lugar: "+lugar.getNombre()+" ?");
        String[] deleteDialogItems = {
                "Sí",
                "No"};
        deleteDialog.setItems(deleteDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Cargamos el controlador
                                ControladorLugares c = ControladorLugares.getControlador(getContext());
                                if (c.eliminarLugar(lugar)) {
                                    Snackbar.make(getView(), "¡Lugar eliminado con éxito!", Snackbar.LENGTH_LONG).show();
                                    // Volver
                                    volver();
                                } else {
                                    Snackbar.make(getView(), "Ha existido un error al eliminar su lugar", Snackbar.LENGTH_LONG).show();
                                }
                                break;
                            case 1:
                                Snackbar.make(getView(),"No se ha realizado ninguna acción",Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
        deleteDialog.show();

    }

    // Inserta un lugar en la base de datos
    private void insertarLugar() {
        // Comprbamos que todos los datos se han introducido
        if(camposNoNulos()) {
            // Cargamos los datos, lo hago poco a poco para ver que ha pasado
            lugar = new Lugar();
            lugar.setNombre(this.etNombre.getEditText().getText().toString());
            lugar.setFecha(this.tvFecha.getText().toString());
            lugar.setTipo(this.spinnerLugarDetalleTipo.getSelectedItem().toString());
            lugar.setLatitud((float) posicion.latitude);
            lugar.setLongitud((float) posicion.longitude);
            lugar.setImagen(Utilidades.bitmapToBase64(imagen));

            ControladorLugares c = ControladorLugares.getControlador(getContext());
            if (c.insertarLugar(lugar)) {
                Snackbar.make(getView(), "¡Lugar añadido con éxito!", Snackbar.LENGTH_LONG).show();
                // Volvemos
                volver();
            } else {
                Snackbar.make(getView(), "Ha habido un error al insertar su lugar", Snackbar.LENGTH_LONG).show();
            }
        }


    }

    private void actualizarLugar() {
        // Mostramos el dialogo de eliminar
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());

        deleteDialog.setTitle("¿Estás seguro de querer actualizar el lugar: "+lugar.getNombre()+" ?");
        String[] deleteDialogItems = {
                "Sí",
                "No"};
        deleteDialog.setItems(deleteDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if(camposNoNulos()) {
                                    // No crear que machacamos el id
                                    lugar.setNombre(etNombre.getEditText().getText().toString());
                                    lugar.setFecha(tvFecha.getText().toString());
                                    lugar.setTipo(spinnerLugarDetalleTipo.getSelectedItem().toString());
                                    lugar.setLatitud((float) posicion.latitude);
                                    lugar.setLongitud((float) posicion.longitude);
                                    lugar.setImagen(Utilidades.bitmapToBase64(imagen));
                                    ControladorLugares c = ControladorLugares.getControlador(getContext());
                                    if (c.actualizarLugar(lugar)) {
                                        Snackbar.make(getView(), "¡Lugar actualizado con éxito!", Snackbar.LENGTH_LONG).show();
                                        // Volvemos
                                        volver();
                                    } else {
                                        Snackbar.make(getView(), "Ha habido un error al actualizar su lugar", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                break;
                            case 1:
                                Snackbar.make(getView(),"No se ha realizado ninguna acción",Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
        deleteDialog.show();

    }

    // Para volver una vez insertado
    // O bloqueamos la interfaz para no icializarlo
    public void volver(){
        ((MainActivity)getActivity()).onBackPressed();
    }

    public boolean camposNoNulos(){
        boolean sal = true;
        if(TextUtils.isEmpty(this.etNombre.getEditText().getText().toString())) {
            this.etNombre.setError("El nombre no puede ser vacío");
            sal = false;
        }
        if(TextUtils.isEmpty(this.etTipo.getEditText().getText().toString())) {
            this.etNombre.setError("El tipo no puede ser vacío");
            sal = false;
        }
        return sal;
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
                    imagen = foto;
                    this.ivLugar.setImageBitmap(imagen);



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
                imagen = bitmap;
                this.ivLugar.setImageBitmap(imagen);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Fallo al guardar", Toast.LENGTH_SHORT).show();
            }
        }


    }

    // Método para iniciar el mapa indepoendientemente del modo
    private void iniciarMapa(){
        // Para Obtener el mapa dentro de un Fragment
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());
        supportMapFragment = SupportMapFragment.newInstance();
        FragmentManager fm =  getChildFragmentManager();/// getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mMap);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mMap, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    /**
     * Voy a programar aquí por comodidad toda la lógica del mapa
     *
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

       // Configuración del mapa por defecto para todos los modos

        mMap = googleMap;
        // Mapa híbrido, lo normal es usar el
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Modos de comprtamiento del mapa no genéricos
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
        switch (modo) {
            case INSERTAR:
                mapaInsertar();
                break;
            case VISUALIZAR:
                mapaVisualizar();
                break;
            case ELIMINAR:
                mapaVisualizar();
                break;
            case ACTUALIZAR:
                mapaActualizar();

            default:
                break;
        }

        mMap.setOnMarkerClickListener(this);

    }

    // Comportamiento del mapa particular para insertar
    private void mapaInsertar(){
        // Configuramos el botón de localización
        mMap.setMyLocationEnabled(true);
        // Activo el evento del marcador
        activarEventosMarcdores();
        obtenerPosicionActualMapa();
    }

    // Comportamiento del mapa en modo visualizar y eliminar
    private void mapaVisualizar(){
        // Vamos a dejar que nos deje ir a l lugar obteniendo la psoición actual
        //mMap.setMyLocationEnabled(true);
        // procesamos el mapa moviendo la camara allu
        posicion = new LatLng(lugar.getLatitud(), lugar.getLongitud());
        marcador = mMap.addMarker(new MarkerOptions()
                        // Posición
                        .position(posicion)
                        // Título
                        .title("Tu posición")
                        // Subtitulo
                        .snippet(lugar.getNombre())
                        // Color o tipo d icono
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
    }

    // Comportamiento del mapa a actualizar
    private void mapaActualizar(){
        // Vamos a dejar que nos deje ir a l lugar obteniendo la psoición actual
        mMap.setMyLocationEnabled(true);
        // Activamos eventos marcadores
        activarEventosMarcdores();
        // procesamos el mapa moviendo la camara allu
        posicion = new LatLng(lugar.getLatitud(), lugar.getLongitud());
        marcador = mMap.addMarker(new MarkerOptions()
                // Posición
                .position(posicion)
                // Título
                .title("Tu posición")
                // Subtitulo
                .snippet(lugar.getNombre())
                // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
    }

    private void obtenerPosicionActualMapa() {
        try {
                // Lo lanzamos como tarea concurrente
                Task<Location> local = mPosicion.getLastLocation();
                local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Actualizamos la última posición conocida
                            localizacion = task.getResult();
                            if(localizacion!=null) {
                                posicion = new LatLng(localizacion.getLatitude(),
                                        localizacion.getLongitude());
                                // Añadimos un marcador especial para poder operar con esto
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
                            }else{
                                Snackbar.make(getView(),"No se ha encontrado su posoción actual",Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Log.d("GPS", "No se encuetra la última posición.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });
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
                        .title("Tu posición")
                        // Subtitulo
                        .snippet(etNombre.getEditText().getText().toString())
                        // Color o tipo d icono
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                );
                // Guardo la posición porque me va a servir y muevo la camara
                posicion = point;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));

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
