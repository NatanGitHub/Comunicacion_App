package com.bsav157.venta_productos;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.StringTokenizer;

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

                String currentUrl = "";
                int i = 0;

                if(item.getFotos() == 1){
                    currentUrl = item.getUrl();
                    Glide.with(context)
                            .load(currentUrl)
                            .into(imagen);
                }else{
                    String urlFotos[] = new String[item.getFotos()];
                    StringTokenizer st = new StringTokenizer(item.getUrl());
                    while (st.hasMoreTokens()){
                        urlFotos[i] = st.nextToken();
                        i++;
                    }
                    Glide.with(context)
                            .load(urlFotos[0])
                            .into(imagen);
                }
            }
        }

    public void dialogCaracteristicas(Productos item){

        Dialog dialogPersonalizado = new Dialog(context);
        dialogPersonalizado.setContentView(R.layout.caracteristicas_producto);

        TextView nombre = dialogPersonalizado.findViewById(R.id.details_nombre);
        TextView detalles = dialogPersonalizado.findViewById(R.id.details_datos);
        LinearLayout linearLayout = dialogPersonalizado.findViewById(R.id.cargaImagenes);

        String currentUrl = "";
        String urlFotos[] = new String[item.getFotos()];
        int i = 0;

        if(item.getFotos() == 1){// Si solo viene un link
            currentUrl = item.getUrl();

            ImageView imageView = new ImageView(context);

            imageView.setAdjustViewBounds(true);
            imageView.setPadding(10, 0, 10, 0);

            Glide.with(context)
                    .load(currentUrl)
                    .into(imageView);

            linearLayout.addView(imageView);

        }else{// Si vienen varios links de varias imagenes

            StringTokenizer st = new StringTokenizer(item.getUrl());
            while (st.hasMoreTokens()){
                urlFotos[i] = st.nextToken();
                i++;
            }

            GradientDrawable shape = new GradientDrawable();
            shape.setStroke(3, Color.YELLOW);
            shape.setCornerRadius(20);

            for(int j = 0; j < item.getFotos(); j++ ){

                ImageView imageView = new ImageView(context);

                imageView.setAdjustViewBounds(true);
                imageView.setPadding(10, 0, 10, 0);
                //imageView.setBackgroundDrawable(shape);

                Glide.with(context)
                        .load(urlFotos[j])
                        .into(imageView);

                linearLayout.addView(imageView);

            }

        }

        detalles.setText( item.getDetalles() );
        nombre.setText( item.getNombre() );
        onResume(dialogPersonalizado);
        // Después mostrarla:
        dialogPersonalizado.show();

    }

    public void onResume(Dialog dialog) {
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    }