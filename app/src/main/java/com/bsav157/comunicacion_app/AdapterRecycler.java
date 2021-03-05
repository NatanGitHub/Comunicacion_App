package com.bsav157.comunicacion_app;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {

        private ArrayList<Productos> datos;
        private LayoutInflater mInflater;
        private Context context;

        public AdapterRecycler(ArrayList<Productos> item, Context context){
            this.mInflater= LayoutInflater.from(context);
            this.context=context;
            this.datos= item;
        }

        @Override
        public int getItemCount(){
            return datos.size();
        }

        @Override
        public AdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){
            //View view= mInflater.inflate(R.layout.lista_elemetos, null);
            View view= mInflater.from(parent.getContext()).inflate(R.layout.cards_recycler, parent,false);
            return  new AdapterRecycler.ViewHolder(view);
        }

    @Override
        public void onBindViewHolder(final AdapterRecycler.ViewHolder holder, final int position){
            //holder.cv.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade));
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogCaracteristicas(datos.get(position));
                }
            });
            holder.bindData(datos.get(position));
        }

        public void setItems(ArrayList<Productos> items){
            datos=items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView nombreProducto, precioProducto;
            ImageView imagen;
            CardView cv;

            ViewHolder(View ItemView){
                super(ItemView);
                cv = ItemView.findViewById(R.id.cardView);
                nombreProducto = ItemView.findViewById(R.id.nombre_producto);
                precioProducto = ItemView.findViewById(R.id.precio_producto);
                imagen = ItemView.findViewById(R.id.imagen);
                //estado=ItemView.findViewById(R.id.estado);
                //cv=ItemView.findViewById(R.id.cv);
            }
            void bindData(final Productos item){
                //imagen.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
                nombreProducto.setText(item.getNombre());
                precioProducto.setText("$ "+item.getPrecio());

                String currentUrl = item.getUrl();

                Glide.with(context)
                        .load(currentUrl)
                        .into(imagen);
                //estado.setText(item.getEstado());
            }
        }

    public void dialogCaracteristicas(Productos item){

        Dialog dialogPersonalizado = new Dialog(context);
        dialogPersonalizado.setContentView(R.layout.caracteristicas_producto);

        TextView nombre = dialogPersonalizado.findViewById(R.id.details_nombre);
        TextView detalles = dialogPersonalizado.findViewById(R.id.details_datos);
        detalles.setText( item.getDetalles() );
        nombre.setText( item.getNombre() );
        // Después mostrarla:
        dialogPersonalizado.show();

    }

    }