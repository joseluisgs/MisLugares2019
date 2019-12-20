package com.example.mislugares.UI.lugares;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mislugares.Modelos.Lugar;
import com.example.mislugares.R;
import com.example.mislugares.Utilidades.Utilidades;

import java.util.ArrayList;

/**
 * Adaptador de la lista mis lugares
 */
public class LugaresListAdapter extends RecyclerView.Adapter<LugaresListAdapter.ViewHolder> {

    private static final int VISUALIZAR = 4;

    // ArrayList con Lugares
    private ArrayList<Lugar> listaLugares;
    // Fragment Manager para trabajar con el
    private FragmentManager fm;

    // Acceso a recursos
    Resources res;

    // Constructor
    public LugaresListAdapter(ArrayList<Lugar> listaLugares, FragmentManager fm, Resources res) {
        this.listaLugares = listaLugares;
        this.fm = fm;
        this.res = res;
    }

    // Asociamos la vista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaJuegos = layoutInflater.inflate(R.layout.item_lugar, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemListaJuegos);
        return viewHolder;
    }

    // Procesamos los lugares y la metemos en un holder, que es que gestiona los elementos individuales
    @Override
    public void onBindViewHolder(LugaresListAdapter.ViewHolder holder, final int position) {

        final Lugar lugar = listaLugares.get(position);


        // Gestionamos la imagen, la sacamos del string, costruimos el bitmap y lo pintamos
        Bitmap imagenLugar = Utilidades.base64ToBitmap(lugar.getImagen());
        float proporcion = 600 / (float) imagenLugar.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenLugar, 600,
                (int) (imagenLugar.getHeight() * proporcion), false);

        // Si lo queremos redondeado
        //RoundedBitmapDrawable redondeado = RoundedBitmapDrawableFactory.create(res, imagenFinal);
        //redondeado.setCornerRadius(imagenFinal.getHeight());
        holder.ivItemLugarImagen.setImageBitmap(imagenFinal);

        //Resto de datos a mostrar
        holder.tvItemLugarNombre.setText(lugar.getNombre());
        holder.tvItemLugarFecha.setText(lugar.getFecha());
        holder.tvItemLugarTipo.setText(lugar.getTipo());


        /**
         * Importante evento de click
         */
        holder.relativeLugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verElemento(position);
            }
        });
    }

    /**
     * Ver un elemento
     *
     * @param position
     */
    private void verElemento(int position) {
        // Esto es redindate, obtener el lugar porque ya lo tendríamos arriba em su contexto
        Lugar lugar = listaLugares.get(position);
        LugarDetalleFragment detalle = new LugarDetalleFragment(lugar, VISUALIZAR);
        FragmentTransaction transaction = fm.beginTransaction();
        // La animación es opcional
        transaction.setCustomAnimations(R.anim.animacion_fragment1,
                R.anim.animacion_fragment1, R.anim.animacion_fragment2, R.anim.animacion_fragment1);
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public int getItemCount() {
        return listaLugares.size();
    }


    // Metodo para borrar el elemento de la lista
    public void removeItem(int position) {
        listaLugares.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaLugares.size());

    }

    // Método para recuperar el elemento de la lista
    public void restoreItem(Lugar item, int position) {
        listaLugares.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listaLugares.size());
    }

    /**
     * Holder que casa los elementos interactivos
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivItemLugarImagen;
        public TextView tvItemLugarNombre;
        public TextView tvItemLugarFecha;
        public TextView tvItemLugarTipo;
        public CardView relativeLugar;


        public ViewHolder(View itemView) {
            super(itemView);
            this.ivItemLugarImagen = itemView.findViewById(R.id.ivItemLugarImagen);
            this.tvItemLugarNombre = itemView.findViewById(R.id.tvItemLugarNombre);
            this.tvItemLugarFecha = itemView.findViewById(R.id.tvItemLugarFecha);
            this.tvItemLugarTipo = itemView.findViewById(R.id.tvItemLugarTipo);
            relativeLugar = itemView.findViewById(R.id.relativeItemLugar);
        }
    }

}
