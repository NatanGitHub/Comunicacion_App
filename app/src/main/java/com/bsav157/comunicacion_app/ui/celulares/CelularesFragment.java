package com.bsav157.comunicacion_app.ui.celulares;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsav157.comunicacion_app.AdapterRecycler;
import com.bsav157.comunicacion_app.InicioSesion;
import com.bsav157.comunicacion_app.Productos;
import com.bsav157.comunicacion_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CelularesFragment extends Fragment {

    private AdapterRecycler adapterRecycler;
    private ArrayList<Productos> productos = new ArrayList<>();
    Productos creaObjeto = new Productos();
    DatabaseReference referenciaBD;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_celulares, container, false);

        dialogEspera();

        productCelulares();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(productos.isEmpty()){
                    Intent pantalla;
                    pantalla = new Intent(getContext(), InicioSesion.class);
                    Toast.makeText(getContext(), "No hay Conexion a Internet", Toast.LENGTH_SHORT).show();
                    startActivity(pantalla);
                }

                AdapterRecycler myAdapter = new AdapterRecycler( productos, getContext() );
                RecyclerView recyclerView = root.findViewById(R.id.my_recycler);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(myAdapter);

            }
        },2500);

        return root;
    }

    public void productCelulares(){

        referenciaBD = FirebaseDatabase.getInstance().getReference();

        referenciaBD.child("celulares").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productos.clear();

                for( DataSnapshot datos :  snapshot.getChildren()){

                    Productos p = new Productos();

                    p.setNombre( datos.child("nombre").getValue().toString() );
                    p.setDetalles( datos.child("descripcion").getValue().toString() );
                    p.setPrecio( Long.parseLong(datos.child("precio").getValue().toString()) );
                    p.setUrl( datos.child("url").getValue().toString() );
                    p.setStock(15);

                    productos.add(p);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void dialogEspera(){

        Dialog dialogPersonalizado = new Dialog(getContext());
        dialogPersonalizado.setContentView(R.layout.layout_cargando);
        dialogPersonalizado.setCancelable(false);

        // Después mostrarla:
        dialogPersonalizado.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogPersonalizado.dismiss();
            }
        },2500);

    }

}