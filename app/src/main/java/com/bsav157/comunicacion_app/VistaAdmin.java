package com.bsav157.comunicacion_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ornach.nobobutton.NoboButton;

import java.util.HashMap;

public class VistaAdmin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference referenciaBD;
    NoboButton crearProducto, verProductos;
    private StorageReference storageReference;
    private static final int GALLERY_INTENT = 1;
    Context context = this;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_admin);
        initItems();

    }

    public void initItems(){

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        referenciaBD = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        verProductos = findViewById(R.id.ver_productos);
        crearProducto = findViewById(R.id.crear_producto);

        crearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCrearProducto();
            }
        });

    }

    public void dialogCrearProducto(){

        Dialog dialogPersonalizado = new Dialog(this);
        dialogPersonalizado.setContentView(R.layout.nuevo_producto);

        EditText nombre = dialogPersonalizado.findViewById(R.id.nombre_producto);
        EditText detalles = dialogPersonalizado.findViewById(R.id.descripcion_producto);
        EditText precio = dialogPersonalizado.findViewById(R.id.precio_producto);
        EditText stock = dialogPersonalizado.findViewById(R.id.stock_producto);
        NoboButton imagen = dialogPersonalizado.findViewById(R.id.subir_imagen);
        NoboButton guardar = dialogPersonalizado.findViewById(R.id.guardar_producto);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nombre.getText().toString().trim().isEmpty()){
                    nombre.setError("Rellenar este Campo");
                    return;
                }

                if(detalles.getText().toString().trim().isEmpty()){
                    detalles.setError("Rellenar este Campo");
                    return;
                }

                if(precio.getText().toString().trim().isEmpty()){
                    precio.setError("Rellenar este Campo");
                    return;
                }

                if(stock.getText().toString().trim().isEmpty()){
                    stock.setError("Rellenar este Campo");
                    return;
                }

                Dialog dialogSubiendo = new Dialog(context);
                dialogSubiendo.setContentView(R.layout.layout_cargando);
                dialogSubiendo.setCancelable(false);
                TextView texto = dialogSubiendo.findViewById(R.id.texto_editable);
                texto.setText("Subiendo Producto");
                // Después mostrarla:
                dialogSubiendo.show();

                StorageReference destino = storageReference.child("zapatos").child(uri.getLastPathSegment());
                destino.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        HashMap<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("nombre", nombre.getText().toString().trim());
                        datosUsuario.put("descripcion", detalles.getText().toString().trim());
                        datosUsuario.put("precio", Long.parseLong(precio.getText().toString().trim()));
                        datosUsuario.put("stock", Integer.parseInt(stock.getText().toString().trim()));

                        destino.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                datosUsuario.put("url", uri.toString());
                                referenciaBD.child("zapatos").push().setValue(datosUsuario);
                                dialogSubiendo.dismiss();
                                dialogPersonalizado.dismiss();
                            }
                        });

                    }
                });

                dialogPersonalizado.dismiss();

            }
        });

        dialogPersonalizado.setCancelable(false);
        onResume(dialogPersonalizado);
        // Después mostrarla:
        dialogPersonalizado.show();

    }

    public void onResume(Dialog dialog) {
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.94), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == GALLERY_INTENT && resultCode == RESULT_OK ){

            Toast.makeText(this, "Imagen Escogida", Toast.LENGTH_SHORT).show();
            uri = data.getData();

        }else{
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_LONG).show();
        }

    }

}