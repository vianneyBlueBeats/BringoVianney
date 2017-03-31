package com.example.addmin.paquete.ui.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.addmin.paquete.R;
import com.example.addmin.paquete.modelo.Paquete;

import java.util.List;

public class PaqueteAdapter extends RecyclerView.Adapter<PaqueteAdapter.MetaViewHolder>
        implements ItemClickListener_cotizacion {

    public int mSelectedItem = -1;

    private List<Paquete> items;

    private Context context;

    public PaqueteAdapter(List<Paquete> items, Context context) {
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
                .inflate(R.layout.paquetes_item_list, viewGroup, false);
        return new MetaViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(MetaViewHolder viewHolder, int i) {
        viewHolder.label_paquete.setText(items.get(i).getlabel_paqute(i));
        viewHolder.ancho_rt.setText(items.get(i).getAncho());
        viewHolder.largo_rt.setText(items.get(i).getLargo());
        viewHolder.altura_rt.setText(items.get(i).getAltura());
        if(items.get(i).getAltura()  == null) {
            viewHolder.label_altura.setText(items.get(i).getLabel_altura());
        }
        viewHolder.peso_rt.setText(items.get(i).getPeso());
        viewHolder.descripcion_rt.setText(items.get(i).getContenido());
        viewHolder.imageView.setImageResource(items.get(i).getImagen());
    }

    @Override
    public void onItemClick(View view, int position) {
        //DetailActivity.launch(
          //      (Activity) context, items.get(position).getIdMeta());
    }


    public class MetaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView ancho_rt;
        public TextView largo_rt;
        public TextView altura_rt;
        public TextView peso_rt;
        public TextView descripcion_rt;
        public ImageView imageView;
        public TextView label_altura;
        public TextView label_paquete;
        public ItemClickListener_cotizacion listener;

        public MetaViewHolder(View v, ItemClickListener_cotizacion listener) {
            super(v);
            ancho_rt = (TextView) v.findViewById(R.id.ancho_rt);
            largo_rt = (TextView) v.findViewById(R.id.largo_rt);
            altura_rt = (TextView) v.findViewById(R.id.altura_rt);
            peso_rt = (TextView) v.findViewById(R.id.peso_rt);
            descripcion_rt = (TextView) v.findViewById(R.id.descripcion_rt);
            imageView = (ImageView)v.findViewById(R.id.imagen_paquete);
            label_altura = (TextView)v.findViewById(R.id.label_altura);
            label_paquete = (TextView)v.findViewById(R.id.label_paquete);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }
}


interface ItemClickListener_paquete {
    void onItemClick(View view, int position);
}
