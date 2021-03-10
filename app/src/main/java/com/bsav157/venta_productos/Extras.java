package com.bsav157.venta_productos;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extras {

    private Context context;
    private DatabaseReference referenciaBD;
    private ArrayList<Productos> productos = new ArrayList<>();
    private StorageReference storageRef;

    public Extras(Context context) {
        this.context = context;
        referenciaBD = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public boolean isOnline() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean correoValido(String correo){

        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(correo);

        if (!mather.find()) {
            return false;
        }

        return true;

    }

    public void actualizaProducto(Productos producto){

        Dialog actualizando = new Dialog(context);
        actualizando.setContentView(R.layout.layout_cargando);
        actualizando.setCancelable(false);
        TextView texto = actualizando.findViewById(R.id.texto_editable);
        texto.setText("Actualizando Producto");
        actualizando.show();

        referenciaBD.child("zapatos").child(producto.getKeyElemento()).setValue(producto);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                actualizando.dismiss();
            }
        }, 3800);

    }

    public void eliminarProducto(Productos productos){

        referenciaBD.child("zapatos").child(productos.getKeyElemento()).removeValue();

        String currentUrl = "";
        String urlFotos[] = new String[productos.getFotos()];
        int i = 0;

        if(productos.getFotos() == 1){// Si solo viene un link

            String nombre = urlFotos[0].substring(85);
            int indice = nombre.indexOf("?", 0);
            nombre = nombre.substring(0, indice);

            StorageReference reference = storageRef.child( "zapatos/" + nombre );
            reference.delete();


            return;

        }else {// Si vienen varios links de varias imagenes

            StringTokenizer st = new StringTokenizer(productos.getUrl());
            while (st.hasMoreTokens()) {
                urlFotos[i] = st.nextToken();
                i++;
            }

        }

        for ( i = 0; i < urlFotos.length; i++ ){

            String nombre = urlFotos[i].substring(85);
            int indice = nombre.indexOf("?", 0);
            nombre = nombre.substring(0, indice);

            StorageReference reference = storageRef.child( "zapatos/" + nombre );
            reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });

        }

    }

    public void descargarProductos(){

        Dialog descargando = new Dialog(context);
        descargando.setContentView(R.layout.layout_cargando);
        descargando.setCancelable(false);
        TextView texto = descargando.findViewById(R.id.texto_editable);
        texto.setText("Descargando Productos");
        descargando.show();

        referenciaBD.child("zapatos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productos.clear();

                for( DataSnapshot datos :  snapshot.getChildren()){

                    Productos p = new Productos();
                    p.setKeyElemento( datos.getKey() );
                    p.setPrecio( Long.parseLong(datos.child("precio").getValue().toString()) );
                    p.setNombre( datos.child("nombre").getValue().toString() );
                    p.setUrl( datos.child("url").getValue().toString() );
                    p.setDescripcion( datos.child("descripcion").getValue().toString() );
                    p.setFotos( Integer.parseInt(datos.child("fotos").getValue().toString()) );
                    p.setStock( Integer.parseInt(datos.child("stock").getValue().toString()) );
                    productos.add(p);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VistaAdmin.productos = productos;
                descargando.dismiss();
            }
        }, 3500);

    }

}
