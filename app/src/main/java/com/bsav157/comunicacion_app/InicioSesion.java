package com.bsav157.comunicacion_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bsav157.comunicacion_app.Interfaces.RegistroListener;
import com.bsav157.comunicacion_app.fragmentos.Registro;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesion extends AppCompatActivity implements RegistroListener {

    EditText campoCorreo, campoClave;
    Button ingresar;
    TextView registrar;
    String registrarCorreo, registrarClave;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        initItems();

    }

    public void initItems(){

        mAuth = FirebaseAuth.getInstance();
        campoCorreo = findViewById(R.id.campo_correo);
        campoClave = findViewById(R.id.campo_clave);
        ingresar = findViewById(R.id.ingresar);
        registrar = findViewById(R.id.registrar);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Registro editNameDialogFragment = Registro.newInstance("Registro");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            }
        });

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InicioSesion.this, "Ingresando", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFinishRegistroDialog(String correo, String clave) {
        registrarCorreo = correo;
        registrarClave = clave;
    }
}