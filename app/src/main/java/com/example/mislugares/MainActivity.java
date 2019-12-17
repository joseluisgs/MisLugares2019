package com.example.mislugares;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.mislugares.Modelos.Lugar;
import com.example.mislugares.Modelos.Noticia;
import com.example.mislugares.UI.lugares.LugarDetalleFragment;
import com.example.mislugares.UI.noticias.NoticiaDetalleFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    // Menú
    private Menu menu;

    // Botones flotantes genericos para toda la app
    //private FloatingActionButton fabEmail;

    // Fragment Manager
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Aquí creamos la Barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Mostramos el Drawe Layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Mostramos cada menu ID con los ids de los frames para que todo coincida al renderizar
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_lugares, R.id.nav_noticias, R.id.nav_acerca_de)
                .setDrawerLayout(drawer)
                .build();

        // Creamos el controlador de fragment de navegación
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ahora vamos con los botones flotantes de toda la app
        //iniciarBotonesFlotantes();

        pedirMultiplesPermisos();

        // Cargamos los datos que debe haber en la BD
        // Para que nuestra App Funcione, si los hay
        //iniciarDatosBD();

    }

    /**
     * Inicia los botones flotantes de la actividad
     */
    private void iniciarBotonesFlotantes() {
        // Botones genericos de toda la app si se repiten
        //fabEmail = (FloatingActionButton) findViewById(R.id.fabEmail);

        // Eventos de todos lso botones flotantes de la app
        iniciarEventosBotonesFlotantes();

    }

    /**
     * Oculta los botones flotantes de la actividad, si los hay
     */
    private void ocultarBotonesFlotantes() {
        //fabEmail.hide();
    }

    // Inicia los eventos de los botones flotantes
    private void iniciarEventosBotonesFlotantes() {
        // Para cada botón flotante
        /*
        fabEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                enviarCorreoElectronico(v,"jlgs@cifpvirgendegracia.com","","Mis Lugares", "Este es un email de prueba para 2º DAM");
            }
        });
         */
    }


    /**
     * Crea las opciones del menú de inicio
     *
     * @param menu Menú
     * @return true o false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        // Ocultamos todos los items
        ocultarOcionesMenu();
        return true;
    }

    /**
     * Oculta las opciones del menú
     */
    private void ocultarOcionesMenu() {
        menu.findItem(R.id.menu_atras).setVisible(false);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_compartir_lugar).setVisible(false);
        menu.findItem(R.id.menu_compartir_noticia).setVisible(false);
    }

    /**
     * Devuelve el menú
     *
     * @return Menú de la apliación
     */
    public Menu getMenu() {
        return this.menu;
    }

    /**
     * Procesa las acciones del menú
     *
     * @param item Item del menu
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                return true;
            case R.id.menu_atras:
                //se llama a la función para recuperar el fragment de la pila
                onBackPressed();
                return true;
            case R.id.menu_compartir_noticia:
                //llamamos a comartir noticias
                compartirNoticia();
                return true;
            case R.id.menu_compartir_lugar:
                //llamamos a comartir lugar
                compartirLugar();
                return true;
            default:
                break;
        }
        return false;
    }


    /**
     * Controla cuando pulsamos atras en el menú
     * Lo vas desapilando
     */
    @Override
    public void onBackPressed() {
        iniciarBotonesFlotantes();
        try {
            if (fm != null) {
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStackImmediate();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            super.onBackPressed();
        }
    }

    /**
     * Actualiza la navegación
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /**
     * Función para mandar el correo electrónico. Si la ponemos aquí es para usarlo en todo la app
     *
     * @param para   Para
     * @param cc     CC
     * @param asunto Asunto
     * @param texto  Texto del correo
     */
    public void enviarCorreoElectronico(String para, String cc, String asunto, String texto) {
        String[] TO = {para}; // Dirección por defecto
        String[] CC = {cc}; // copia
        // Creamos el intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, texto);

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            //Log.e("EMAIL", "Error Email: " + ex.getMessage());
            Snackbar.make(null, "No se ha podido enviar Email.", Snackbar.LENGTH_SHORT).show();

        }

    }

    /**
     * Función para que compartir noticias. Si la ponemos aquí es para usarla en toda la app
     */
    public void compartirNoticia() {
        // Esto debemos hacerlo, porque la opción de copratir está en la barra de herramientas
        // en un menú
        Noticia na = NoticiaDetalleFragment.noticiaActual;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //
        String body = na.getTitulo() + " vía @el_pais " + na.getLink();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Últimas noticias");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Compartir con"));

    }

    /**
     * Comparte un lugar. Si lo hacemos aquí es pata usarlo en toda la app
     */
    public void compartirLugar() {
        // Esto debemos hacerlo, porque la opción de copratir está en la barra de herramientas
        // en un menú
        Lugar la = LugarDetalleFragment.lugarActual;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //
        String body = la.getNombre() + " día: " + la.getFecha() +
                "\nen lat: " + la.getLatitud() + " - long: " + la.getLongitud() + "\n" +
                "por Mis Lugares App";
        intent.putExtra(Intent.EXTRA_SUBJECT, "Uno de mis lugares favoritos");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Compartir con"));

    }


    /**
     * Oculta los elementos en la IU
     */
    private void ocultarElementosIU() {
        ocultarBotonesFlotantes();
        ocultarOcionesMenu();
    }


    /**
     * Pedir mútiples permisos respecto al manifest usando Drexler. Ver Gradle
     */
    private void pedirMultiplesPermisos() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }

                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

}
