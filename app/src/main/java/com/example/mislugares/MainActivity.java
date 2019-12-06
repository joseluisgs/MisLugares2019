package com.example.mislugares;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.mislugares.Controladores.ControladorLugares;
import com.example.mislugares.Modelos.Noticia;
import com.example.mislugares.UI.noticias.NoticiaDetalleFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

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
                R.id.nav_home, R.id.nav_lugares,  R.id.nav_noticias, R.id.nav_acerca_de)
                .setDrawerLayout(drawer)
                .build();

        // Creamos el controlador de fragment de navegación
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ahora vamos con los botones flotantes de toda la app
        iniciarBotonesFlotantes();

        pedirMultiplesPermisos();

        // Cargamos los datos que debe haber en la BD
        // Para que nuestra App Funcione, si los hay
        //iniciarDatosBD();

    }

    // Inicia todos los botones flotantes
    private void iniciarBotonesFlotantes() {
        // Botones genericos de toda la app si se repiten
        //fabEmail = (FloatingActionButton) findViewById(R.id.fabEmail);

        // Eventos de todos lso botones flotantes de la app
        iniciarEventosBotonesFlotantes();

    }

    // Ocultamos los botones flotantes de toda la app
    private void ocultarBotonesFlotantes(){
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

    // GETTER & SETTER de botones flotantes
    // Uno por cada uno
    /*
    public FloatingActionButton getFabEmail() {
        return fabEmail;
    }
    */

    // Función que crea el menú (lo infla)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu= menu;
        // Ocultamos todos los items
        ocultarOcionesMenu();
        return true;
    }

    private void ocultarOcionesMenu(){
        menu.findItem(R.id.menu_atras).setVisible(false);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_compartir).setVisible(false);
    }

    // GETER & SETTER Menu
    public Menu getMenu() {
        return this.menu;
    }

    // Elementos del menú
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                return true;
            case R.id.menu_atras:
                //se llama a la función para recuperar el fragment de la pila
                onBackPressed();
                return true;
            case R.id.menu_compartir:
                //llamamos a comartir noticias
                compartirNoticia();
                return true;
            default:
                break;
        }
        return false;
    }


    //aqui controlo cuando se le da a la tecla de atrás que es lo que va a hacer
    //si hay fragments en la pila va tirando de ellos hasta hacer el super.onBackPressed();
    //que es el método de siempre para volver atrás.
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
            //aqui no llega por que controla el null arriba
            //Log.e("ATRAS", "Error al hacer backpressed");
        }
    }

    // Función que actualiza la navegación
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // Función para enviar un correo electrónico
    // Si la ponemos aquí podremos usarla en cada Fragment al necesitar la vista principal
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
    // Comprarte una noticia
    public void compartirNoticia(){
        // Esto debemos hacerlo, porque la opción de copratir está en la barra de herramientas
        // en un menú
        Noticia na =  NoticiaDetalleFragment.noticiaActual;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //
        String body = na.getTitulo()+" vía @el_pais "+ na.getLink();
        intent.putExtra(Intent.EXTRA_SUBJECT,"Últimas noticias");
        intent.putExtra(Intent.EXTRA_TEXT,body);
        startActivity(Intent.createChooser(intent,"Compartir con"));

    }

    // Inicia la interfaz por defectro en cada vista de Fragment o actividad
    public void ocultarElementosIU(){
        ocultarBotonesFlotantes();
        ocultarOcionesMenu();
    }

    private void iniciarDatosBD(){
        // En nuestro caso necesitamos cargar estos datos
        //ControladorLugares c = ControladorLugares.getControlador(this);
        //c.insertarTiposLugares();
        return;
    }

    // Vamos a manejar multiples permisso con la librería Drexler, ver el Gradle
    private void pedirMultiplesPermisos(){
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
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
