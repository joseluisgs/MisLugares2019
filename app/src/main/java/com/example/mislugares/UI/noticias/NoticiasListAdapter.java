package com.example.mislugares.UI.noticias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mislugares.Modelos.Noticia;
import com.example.mislugares.R;
import com.example.mislugares.Utilidades.CirculoTransformacion;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticiasListAdapter extends RecyclerView.Adapter<NoticiasListAdapter.ViewHolder> {

    // Objeto con el modelo de datos (lista)
    private ArrayList<Noticia> listaNoticias;

    // Fragment Manager para trabajar con el
    private FragmentManager fm;

    public NoticiasListAdapter(ArrayList<Noticia> listaNoticias, FragmentManager fm) {
        this.listaNoticias = listaNoticias;
        this.fm = fm;
    }

    // Asociamos la vista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Layout de la noticia
        View listItem = layoutInflater.inflate(R.layout.item_noticia, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    // Procesamos las noticias y la metemos en un holder, que es que gestiona los elementos individuales
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Noticia noticia = listaNoticias.get(position);
        String titular = listaNoticias.get(position).getTitulo();

        //Controlamos la longitud para que si llega a una cantidad de caracteres, recortarlo
        if (titular.length() >= 30){
            titular = titular.substring(0,30);
            holder.tvTitular.setText(titular+"...");
        }else{
            holder.tvTitular.setText(titular);
        }

        //Formateamos la fecha
        Date date = new Date(listaNoticias.get(position).getFecha());
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormato = formatoFecha.format(date);
        holder.tvFecha.setText(fechaFormato);
        //Sacamos la hora
        holder.tvHora.setText(listaNoticias.get(position).getFecha().substring(16,25));
        //Usando Picasso para poder obtener las fotos y redondearlas
        Picasso.get().load(listaNoticias.get(position).getImagen())
                //Instanciamos un objeto de la clase (creada más abajo) para redondear la imagen
                .transform(new CirculoTransformacion())
                .resize(375, 200)
                .into(holder.ivNoticia);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Transacción entre fragments. Lo primero es llamar al fragment manager
                 * A continuación instanciamos un objeto fragment correspondiente al
                 * fragment que va a entrar y le pasamos el objeto noticia en la posición de
                 * la lista correspondiente. Iniciamos la transacción, dándole si queremos
                 * animaciones. Lo más importante es el replace, en el que remplazamos el
                 * host por el objeto fragment detalle
                 * De esta manera lo hacemos nosotros de manera manual
                 */
                NoticiaDetalleFragment detalle = new NoticiaDetalleFragment(noticia);
                FragmentTransaction transaction = fm.beginTransaction();
                // La animación es opcional
                transaction.setCustomAnimations(R.anim.animacion_fragment1,
                        R.anim.animacion_fragment1, R.anim.animacion_fragment2, R.anim.animacion_fragment1);
                //Llamamos al replace
                transaction.replace(R.id.nav_host_fragment,detalle);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

    }

    // Metodo para borrar el elemento de la lista
    public void removeItem(int position) {
        listaNoticias.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaNoticias.size());

    }

    // Método para recuperar el elemento de la lista
    public void restoreItem(Noticia item, int position) {
        listaNoticias.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listaNoticias.size());
    }

    // Metodo para obtener el numero de elementos de la lista
    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }


    //Esta clase Holder representa a los elementos que vamos a manejar en cada item
    //Sus atributos coinciden con la clase que simboliza cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivNoticia;
        public TextView tvTitular;
        public TextView tvFecha;
        public TextView tvHora;
        public RelativeLayout relativeLayout;

        // Casamos con cada elemento de la interfaz
        public ViewHolder(View itemView) {
            super(itemView);
            this.ivNoticia = (ImageView) itemView.findViewById(R.id.ivItemImagenNoticia);
            this.tvTitular = (TextView) itemView.findViewById(R.id.tvItemTitularNoticia);
            this.tvFecha = (TextView) itemView.findViewById(R.id.tvItemFechaNoticia);
            this.tvHora = (TextView)itemView.findViewById(R.id.tvItemHoraNoticia);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeItemNoticia);

        }
    }
}
