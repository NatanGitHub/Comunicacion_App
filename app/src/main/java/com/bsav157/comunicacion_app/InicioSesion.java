package com.bsav157.comunicacion_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

import com.bsav157.comunicacion_app.Interfaces.RegistroListener;
import com.bsav157.comunicacion_app.fragmentos.Registro;

public class InicioSesion extends AppCompatActivity implements RegistroListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        cargar();

    }

    public void cargar(){

        FragmentManager fm = getSupportFragmentManager();
        Registro editNameDialogFragment = Registro.newInstance("Registro");
        editNameDialogFragment.show(fm, "fragment_edit_name");

    }

    @Override
    public void onFinishRegistroDialog(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}