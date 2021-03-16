package com.bsav157.venta_productos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.transition.Explode;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsav157.venta_productos.Interfaces.RegistroListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ornach.nobobutton.NoboButton;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> implements RegistroListener {

        private ArrayList<Productos> datos;
        private LayoutInflater mInflater;
        private Context context;
        private String tipo;
        private Extras extras;
        private LinearLayout parentLinearLayout;
        private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        int CAPTURAR_FOTO = 0;

        public AdapterRecycler(ArrayList<Productos> item, Context context, String recibeTipo){
            this.mInflater= LayoutInflater.from(context);
            this.context=context;
            this.datos= item;
            tipo = recibeTipo;
            extras = new Extras(context);
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

                    if(tipo.equals("usuario"))
                        dialogCaracteristicasUsuario(datos.get(position));
                    else{

                        dialogCaracteristicasAdmin(datos.get(position));
                    }
                }
            });
            holder.bindData(datos.get(position));
        }

        public void setItems(ArrayList<Productos> items){
            datos=items;
        }

    @Override
    public void onFinishRegistroDialog(String correo, String clave) {

    }

    @Override
    public void onFinishQuestionDialog() {

        Toast.makeText(context, "Volvimos al hilo principal", Toast.LENGTH_SHORT).show();

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
            }
            void bindData(final Productos item){
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

    public void dialogCaracteristicasUsuario(Productos item){

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

        detalles.setText( item.getDescripcion() );
        nombre.setText( item.getNombre() );
        onResume(dialogPersonalizado);
        // Después mostrarla:
        dialogPersonalizado.show();

    }

    public void dialogCaracteristicasAdmin(Productos item){

        String urlFotos[] = new String[item.getFotos()];

        Dialog dialogVerProductos = new Dialog(context);
        dialogVerProductos.setContentView(R.layout.editar_producto);
        parentLinearLayout = (LinearLayout) dialogVerProductos.findViewById(R.id.imagen_editada);
        dialogVerProductos.setCancelable(false);

        TextView cancelar = dialogVerProductos.findViewById(R.id.cancelar);
        NoboButton actualizar = dialogVerProductos.findViewById(R.id.editar_producto);
        //NoboButton eliminar = dialogVerProductos.findViewById(R.id.eliminar);
        NoboButton abrirImagen = dialogVerProductos.findViewById(R.id.subir_imagen);
        EditText nombre = dialogVerProductos.findViewById(R.id.nombre_producto);
        EditText detalles = dialogVerProductos.findViewById(R.id.descripcion_producto);
        EditText precio = dialogVerProductos.findViewById(R.id.precio_producto);
        EditText stock = dialogVerProductos.findViewById(R.id.stock_producto);

        String currentUrl = "";
        int i;

        if(item.getFotos() == 1){
            urlFotos[0] = item.getUrl();
        }else{
            urlFotos = extras.tokenizarLinksImagen(item.getFotos(), item.getUrl());
        }

        for(i = 0; i < item.getFotos(); i++ ){

            final String urlUri = urlFotos[i];

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_imagen, null);
            ImageView imageView = rowView.findViewById(R.id.imagen);
            imageView.setAdjustViewBounds(true);
            imageView.setMinimumHeight(100);
            imageView.setPadding(15, 0, 10, 0);
            Glide.with(context)
                    .load(urlFotos[i])
                    .into(imageView);
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        }

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogVerProductos.dismiss();
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nombre.getText().toString().isEmpty()){
                    nombre.setError("El campo no puede estar vacio");
                    return;
                }

                if(detalles.getText().toString().isEmpty()){
                    detalles.setError("El campo no puede estar vacio");
                    return;
                }

                if(precio.getText().toString().isEmpty()){
                    precio.setError("El campo no puede estar vacio");
                    return;
                }

                if(stock.getText().toString().isEmpty()){
                    stock.setError("El campo no puede estar vacio");
                    return;
                }

                item.setNombre( nombre.getText().toString().trim() );
                item.setDescripcion( detalles.getText().toString().trim() );
                item.setPrecio( Long.parseLong( precio.getText().toString().trim() ) );
                item.setStock( Integer.parseInt( stock.getText().toString().trim() ) );

                extras.actualizaProducto(item);
                dialogVerProductos.dismiss();
            }
        });

        precio.setText( String.valueOf(item.getPrecio()) );
        stock.setText( String.valueOf(item.getStock()) );
        detalles.setText( item.getDescripcion() );
        nombre.setText( item.getNombre() );
        abrirImagen.setEnabled(false);
        onResume(dialogVerProductos);

        dialogVerProductos.show();

    }

    public void dialogEliminaImagen(){

        Dialog questionDialog = new Dialog(context);
        questionDialog.setContentView(R.layout.dialog_question);
        questionDialog.setCancelable(false);

        TextView titulo = questionDialog.findViewById(R.id.question_tittle);
        TextView mensaje = questionDialog.findViewById(R.id.question_message);
        TextView si = questionDialog.findViewById(R.id.question_si);
        TextView no = questionDialog.findViewById(R.id.question_no);

        titulo.setText("Advertencia");
        mensaje.setText("¿Desea elminar la imagen?");

        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Se eliminara la imagen", Toast.LENGTH_LONG).show();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionDialog.dismiss();
            }
        });

        //onResume(dialogPersonalizado);
        questionDialog.show();

    }

    /*public void eliminarImagen(Uri uri){
        int i;
        for(i = 0; i < listaImagenes.size(); i++){
            if( listaImagenes.get(i).equals(uri) ){
                listaImagenes.remove(i);
                Toast.makeText(context, "listaImagenes.size(): " + listaImagenes.size(), Toast.LENGTH_LONG).show();
            }
        }
    }*/

    public void onResume(Dialog dialog) {
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}