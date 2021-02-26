package com.bsav157.comunicacion_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.bsav157.comunicacion_app.fragmentos.Registro;

public class InicioSesion extends AppCompatActivity {

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

}