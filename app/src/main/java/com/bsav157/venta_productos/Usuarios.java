package com.bsav157.venta_productos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Usuarios extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView salir;
    private DatabaseReference referenciaBD;
    private RecyclerView recyclerView;
    private TextView usuario;
    private Context context = this;
    private ArrayList<Productos> productos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        initItems();
        dialogEspera();
        productZapatos();
        cargarRecycler();

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Advertencia");
        builder.setMessage("¿Desea salir de la Aplicacion?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        //ejecuta super.onBackPressed() para que finalice el metodo cerrando el activity
        //super.onBackPressed();
    }

    public void initItems(){

        recyclerView = findViewById(R.id.recycler_usuarios);
        usuario = findViewById(R.id.email_usuario);
        salir = findViewById(R.id.salir);

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        usuario.setText( user.getEmail() );

    }

    public void dialogEspera(){

        Dialog dialogPersonalizado = new Dialog(this);
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

    public void productZapatos(){

        referenciaBD = FirebaseDatabase.getInstance().getReference();

        referenciaBD.child("zapatos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productos.clear();

                for( DataSnapshot datos :  snapshot.getChildren()){

                    Productos p = new Productos();

                    p.setNombre( datos.child("nombre").getValue().toString() );
                    p.setDescripcion( datos.child("descripcion").getValue().toString() );
                    p.setPrecio( Long.parseLong(datos.child("precio").getValue().toString()) );
                    p.setUrl( datos.child("url").getValue().toString() );
                    p.setFotos( Integer.parseInt(datos.child("fotos").getValue().toString()) );
                    p.setStock( Integer.parseInt(datos.child("stock").getValue().toString()) );

                    productos.add(p);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void cargarRecycler(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(productos.isEmpty()){
                    Toast.makeText(context, "No hay Conexion a Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                AdapterRecycler myAdapter = new AdapterRecycler( productos, context, "usuario" );
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager( new LinearLayoutManager(context));
                recyclerView.setAdapter(myAdapter);

            }
        },2500);

    }

}