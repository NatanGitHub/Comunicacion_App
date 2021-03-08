package com.bsav157.venta_productos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bsav157.venta_productos.fragmentos.AdminVerProductos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ornach.nobobutton.NoboButton;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VistaAdmin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference referenciaBD;
    private NoboButton crearProducto, verProductos;
    private StorageReference storageReference;
    private int CAPTURAR_FOTO = 0;
    private List<Uri> listaImagenes = new ArrayList<>();
    private Context context = this;
    private ArrayList<Productos> productos = new ArrayList<>();
    private Extras extras = new Extras(this);
    private LinearLayout linearLayout;
    private TextView salir;
    private Uri uri;

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
        salir = findViewById(R.id.salir);

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        crearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!extras.isOnline()){
                    Dialog verificarConexion = new Dialog(context);
                    verificarConexion.setContentView(R.layout.mensaje_sin_internet);
                    verificarConexion.show();
                    return;
                }

                verProductos.setEnabled(false);
                dialogCrearProducto();
            }
        });

        verProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!extras.isOnline()){
                    Dialog verificarConexion = new Dialog(context);
                    verificarConexion.setContentView(R.layout.mensaje_sin_internet);
                    verificarConexion.show();
                    return;
                }

                Dialog dialogPersonalizado = new Dialog(context);
                dialogPersonalizado.setContentView(R.layout.layout_cargando);
                dialogPersonalizado.setCancelable(false);

                // Después mostrarla:
                dialogPersonalizado.show();
                productZapatos();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        AdminVerProductos adminVerProductos = AdminVerProductos.newInstance("Registro", productos);
                        adminVerProductos.show(fm, "fragment_admin_ver_productos");
                        dialogPersonalizado.dismiss();
                    }
                }, 3000);

            }
        });

    }

    public void productZapatos(){

        referenciaBD = FirebaseDatabase.getInstance().getReference();

        referenciaBD.child("zapatos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productos.clear();

                for( DataSnapshot datos :  snapshot.getChildren()){

                    Productos p = new Productos();

                    p.setDescripcion( datos.child("descripcion").getValue().toString() );
                    p.setNombre( datos.child("nombre").getValue().toString() );
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

    public void dialogCrearProducto(){

        Dialog dialogPersonalizado = new Dialog(this);
        dialogPersonalizado.setContentView(R.layout.nuevo_producto);
        dialogPersonalizado.setCancelable(false);

        linearLayout = dialogPersonalizado.findViewById(R.id.imagen_editada);
        EditText nombre = dialogPersonalizado.findViewById(R.id.nombre_producto);
        EditText detalles = dialogPersonalizado.findViewById(R.id.descripcion_producto);
        EditText precio = dialogPersonalizado.findViewById(R.id.precio_producto);
        EditText stock = dialogPersonalizado.findViewById(R.id.stock_producto);
        NoboButton imagen = dialogPersonalizado.findViewById(R.id.subir_imagen);
        NoboButton guardar = dialogPersonalizado.findViewById(R.id.guardar_producto);
        TextView cancelar = dialogPersonalizado.findViewById(R.id.cancelar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaImagenes.clear();
                dialogPersonalizado.dismiss();
                verProductos.setEnabled(true);
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione Imagenes"), CAPTURAR_FOTO);

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

                if(listaImagenes.size() < 1){
                    Toast.makeText(context, "NO HA CARGADO NINGUNA IMAGEN", Toast.LENGTH_LONG).show();
                    return;
                }

                Dialog dialogSubiendo = new Dialog(context);
                dialogSubiendo.setContentView(R.layout.layout_cargando);
                dialogSubiendo.setCancelable(false);
                TextView texto = dialogSubiendo.findViewById(R.id.texto_editable);
                texto.setText("Subiendo Producto");
                // Después mostrarla:
                dialogSubiendo.show();

                HashMap<String, Object> datosUsuario = new HashMap<>();

                String urlFotos[] = new String[listaImagenes.size()];

                for ( int i = 0; i < listaImagenes.size(); i++ ){

                    StorageReference guardardo = storageReference.child("zapatos").child(listaImagenes.get(i).getLastPathSegment());
                    //Uri uri = listaImagenes.get(0);
                    int finalI = i;
                    guardardo.putFile(listaImagenes.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            guardardo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlFotos[finalI] = uri.toString();
                                }
                            });
                        }
                    });
                }

                // Esperamos unos segundos antes de proseguir

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String urlFinal = "";

                        if(urlFotos.length == 1){
                            urlFinal = urlFotos[0];
                        }else{

                            for(int i = 0; i < urlFotos.length; i++){
                                urlFinal += "\f";
                                urlFinal += urlFotos[i];
                            }

                        }

                        datosUsuario.put("nombre", nombre.getText().toString().trim());
                        datosUsuario.put("descripcion", detalles.getText().toString().trim());
                        datosUsuario.put("precio", Long.parseLong(precio.getText().toString().trim()));
                        datosUsuario.put("stock", Integer.parseInt(stock.getText().toString().trim()));
                        datosUsuario.put("fotos", listaImagenes.size());
                        datosUsuario.put("url", urlFinal);
                        referenciaBD.child("zapatos").push().setValue(datosUsuario);

                        listaImagenes.clear();
                        dialogPersonalizado.dismiss();
                        dialogSubiendo.dismiss();

                        // Le doy unos segundos de espera para poder ver productos
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                verProductos.setEnabled(true);
                            }
                        }, 3000);

                    }
                }, 5000);

            }
        });

        dialogPersonalizado.setCancelable(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int i;

        if( requestCode == CAPTURAR_FOTO && resultCode == RESULT_OK ){

            uri = data.getData();

            CropImage.activity(uri)
                    .start(this);

        }else if( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK ){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                ImageView imageView = new ImageView(context);

                imageView.setAdjustViewBounds(true);
                imageView.setPadding(15, 0, 10, 0);
                imageView.setImageURI(result.getUri());
                imageView.setMinimumHeight(100);
                linearLayout.addView(imageView);

                listaImagenes.add(result.getUri());

                //Uri resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(context, "No se cargo una imagen", Toast.LENGTH_SHORT).show();

            }

            //Toast.makeText(context, "Imagen Editada", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_LONG).show();
        }

    }

}