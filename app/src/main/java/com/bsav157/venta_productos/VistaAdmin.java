package com.bsav157.venta_productos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VistaAdmin extends AppCompatActivity {

    // Variables FireBase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference referenciaBD;
    private StorageReference storageReference;

    // Variables de la vista
    private NoboButton crearProducto, verProductos;
    private LinearLayout parentLinearLayout;
    private Dialog dialogCrear;
    private TextView salir;
    private ConstraintLayout constraintBotones, constraintRecycler;
    private RecyclerView recyclerView;
    private TextView regresarMenu;

    // Otras variables
    private int CAPTURAR_FOTO = 0;
    public static List<Uri> listaImagenes = new ArrayList<>();
    private Context context = this;
    public static ArrayList<Productos> productos = new ArrayList<>();
    private Extras extras = new Extras(this);
    private AdapterRecycler myAdapterRecycler;
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
        recyclerView = findViewById(R.id.recycler_admin);
        constraintBotones = findViewById(R.id.botones);
        constraintRecycler = findViewById(R.id.recycler_view);
        regresarMenu = findViewById(R.id.regresar);
        salir = findViewById(R.id.salir);

        // OnClicks
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

                productos.clear();
                extras.descargarProductos();

                constraintBotones.setVisibility(View.GONE);
                constraintRecycler.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapterRecycler = new AdapterRecycler( productos, context, "admin" );
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager( new LinearLayoutManager(context));
                        recyclerView.setAdapter(myAdapterRecycler);
                        regresarMenu.setVisibility(View.VISIBLE);
                    }
                }, 3700);
            }
        });
        regresarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getVisibility() == View.VISIBLE){
                    view.setVisibility(View.INVISIBLE);
                    constraintRecycler.setVisibility(View.GONE);
                    constraintBotones.setVisibility(View.VISIBLE);
                    productos.clear();
                    myAdapterRecycler.notifyDataSetChanged();
                }
            }
        });
    }

    public void dialogCrearProducto(){

        dialogCrear = new Dialog(this);
        dialogCrear.setContentView(R.layout.nuevo_producto);
        parentLinearLayout = (LinearLayout) dialogCrear.findViewById(R.id.imagen_editada);
        dialogCrear.setCancelable(false);

        EditText nombre = dialogCrear.findViewById(R.id.nombre_producto);
        EditText detalles = dialogCrear.findViewById(R.id.descripcion_producto);
        EditText precio = dialogCrear.findViewById(R.id.precio_producto);
        EditText stock = dialogCrear.findViewById(R.id.stock_producto);
        NoboButton imagen = dialogCrear.findViewById(R.id.subir_imagen);
        NoboButton guardar = dialogCrear.findViewById(R.id.guardar_producto);
        TextView cancelar = dialogCrear.findViewById(R.id.cancelar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaImagenes.clear();
                dialogCrear.dismiss();
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
                        dialogCrear.dismiss();
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

        dialogCrear.setCancelable(true);
        onResume(dialogCrear);
        dialogCrear.show();

    }

    public void onResume(Dialog dialog) {
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.94), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        if( requestCode == CAPTURAR_FOTO && resultCode == RESULT_OK ){

            uri = data.getData();
            CropImage.activity(uri)
                    .start(this);

        }else if( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK ){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                listaImagenes.add(result.getUri());
                crearImagen(result.getUri());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "No se pudo cortar la imagen", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
        }

    }

    public void eliminarImagen(Uri uri){
        int i;
        for(i = 0; i < listaImagenes.size(); i++){
            if( listaImagenes.get(i).equals(uri) ){
                listaImagenes.remove(i);
                Toast.makeText(context, "Cantidad fotos: " + listaImagenes.size(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void crearImagen(Uri uri){

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_imagen, null);
        ImageView imageView = rowView.findViewById(R.id.imagen);
        imageView.setAdjustViewBounds(true);
        imageView.setMinimumHeight(100);
        imageView.setPadding(15, 0, 10, 0);
        imageView.setImageURI(uri);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                parentLinearLayout.removeView((View) view.getParent());
                eliminarImagen(uri);
                return false;
            }
        });
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

}