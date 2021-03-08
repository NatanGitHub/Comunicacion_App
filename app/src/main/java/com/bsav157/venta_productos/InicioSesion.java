package com.bsav157.venta_productos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bsav157.venta_productos.Interfaces.RegistroListener;
import com.bsav157.venta_productos.fragmentos.Registro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.ornach.nobobutton.NoboButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InicioSesion extends AppCompatActivity implements RegistroListener {

    private EditText campoCorreo, campoClave;
    private NoboButton ingresar;
    private TextView registrar;
    private ProgressDialog proceso;
    private Context context = this;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedpreferences;
    private String mypreference = "mypref";
    private Extras extras = new Extras(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        initItems();
        rellenarDatosSesion();

    }

    public void guardarDatosSesion(String correo, String clave) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("correo", correo);
        editor.putString("clave", clave);
        editor.commit();
    }

    public void rellenarDatosSesion() {
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("correo")) {
            campoCorreo.setText(sharedpreferences.getString("correo", ""));
        }
        if (sharedpreferences.contains("clave")) {
            campoClave.setText(sharedpreferences.getString("clave", ""));
        }
    }

    public void initItems(){

        mAuth = FirebaseAuth.getInstance();
        mAuth.getInstance().signOut();
        campoCorreo = findViewById(R.id.campo_correo);
        campoClave = findViewById(R.id.campo_clave);
        ingresar = findViewById(R.id.ingresar);
        registrar = findViewById(R.id.registrar);
        proceso = new ProgressDialog(this);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!extras.isOnline()){
                    Dialog verificarConexion = new Dialog(context);
                    verificarConexion.setContentView(R.layout.mensaje_sin_internet);
                    verificarConexion.show();
                    return;
                }

                FragmentManager fm = getSupportFragmentManager();
                Registro editNameDialogFragment = Registro.newInstance("Registro");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            }
        });

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!extras.isOnline()){
                    Dialog verificarConexion = new Dialog(context);
                    verificarConexion.setContentView(R.layout.mensaje_sin_internet);
                    verificarConexion.show();
                    return;
                }

                if(campoCorreo.getText().toString().isEmpty()){
                    campoCorreo.setError("Introduzca su correo");
                    return;
                }

                if (!extras.correoValido(campoCorreo.getText().toString().trim())) {
                    campoCorreo.setError("Formato de correo invalido");
                    return;
                }

                if(campoClave.getText().toString().isEmpty()){
                    campoClave.setError("Introduzca su clave");
                    return;
                }

                proceso.setMessage("Iniciando sesion ...");
                proceso.setCancelable(false);
                proceso.show();

                mAuth.getInstance().signOut();

                mAuth.signInWithEmailAndPassword(campoCorreo.getText().toString().trim(), campoClave.getText().toString().trim())
                        .addOnCompleteListener(InicioSesion.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    guardarDatosSesion(campoCorreo.getText().toString().trim(), campoClave.getText().toString().trim());

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Intent pantalla;

                                    if(user.getEmail().equals("alejo@gmail.com")){
                                        pantalla = new Intent(InicioSesion.this, VistaAdmin.class);
                                    }else{
                                        pantalla = new Intent(InicioSesion.this, Usuarios.class);
                                    }
                                    startActivity(pantalla);
                                    finish();

                                } else {
                                    Toast.makeText(InicioSesion.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                                    proceso.dismiss();
                                    return;
                                }
                                proceso.dismiss();
                            }
                        });

            }

        });

    }

    @Override
    public void onFinishRegistroDialog(String correo, String clave) {

        proceso.setMessage("Registrando");
        proceso.setCancelable(false);
        proceso.show();

        mAuth.createUserWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent pantalla = new Intent(InicioSesion.this, Usuarios.class);
                            startActivity(pantalla);
                        } else {
                            // Si el usuario ya ha sido creado .......
                            if( task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(InicioSesion.this, "El Usuario ya se encuentra creado", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(InicioSesion.this, "No se pudo registrar el Usuario.", Toast.LENGTH_LONG).show();
                            }

                        }

                        proceso.dismiss();
                    }
                });

    }
}