package com.example.addmin.paquete.ui.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addmin.paquete.R;
import com.example.addmin.paquete.modelo.Cotizacion;

import java.util.List;

public class CotizacionAdapter extends RecyclerView.Adapter<CotizacionAdapter.MetaViewHolder>
        implements ItemClickListener_cotizacion {

    public int mSelectedItem = -1;
    public String embalaje = "";
    public String precio = "";
    public String carrier = "";
    private List<Cotizacion> items;
    private Context context;

    private static AdapterView.OnItemClickListener onItemClickListener;

    public CotizacionAdapter(List<Cotizacion> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MetaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cotizacion_item_list, viewGroup, false);
        return new MetaViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(MetaViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.precio.setText(items.get(i).getPrecio());
        viewHolder.precio_embalaje.setText(items.get(i).getPrecio_embalaje());
        if(items.get(i).getPrecio_embalaje()  == null) {
             viewHolder.label_embalaje.setText(items.get(i).getLabel_embalaje());
        }
        viewHolder.operador_logistico.setText(items.get(i).getOperador_logistico());
        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedItem= position;
                precio = items.get(position).getPrecio();
                embalaje = items.get(position).getPrecio_embalaje();
                carrier = items.get(position).getOperador_logistico();
                notifyDataSetChanged();
            }
        });
        if(mSelectedItem==position){viewHolder.itemView.setBackgroundResource(R.drawable.gradiente_cotizacion);}
        else {viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);}
    }

    @Override
    public void onItemClick(View view, int position) {
        view.setBackgroundColor(Color.BLUE);
        Toast.makeText(context, "ll", Toast.LENGTH_LONG).show();
    }

    public class MetaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView precio;
        public TextView operador_logistico;
        public TextView precio_embalaje;
        public TextView label_embalaje;
        public ImageView imagen;
        public ItemClickListener_cotizacion listener;

        public MetaViewHolder(View v, ItemClickListener_cotizacion listener)
        {
            super(v);
            itemView.setClickable(true);
            precio = (TextView) v.findViewById(R.id.precio_txt);
            precio_embalaje = (TextView) v.findViewById(R.id.embalaje_txt);
            label_embalaje = (TextView) v.findViewById(R.id.label_embalaje);
            operador_logistico = (TextView) v.findViewById(R.id.operador_txt);
            imagen = (ImageView) v.findViewById(R.id.image_operadorL);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }
}

interface ItemClickListener_cotizacion {
    void onItemClick(View view, int position);
}
