package com.bsav157.comunicacion_app.fragmentos;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bsav157.comunicacion_app.R;

public class Registro extends DialogFragment {

    public Registro() {
        // Required empty public constructor
    }

    public static Registro newInstance(String title) {
        Registro frag = new Registro();
        Bundle args = new Bundle();
        args.putString("Titulo", title);
        frag.setArguments(args);
        frag.setCancelable(false);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_registro, container);

        return vista;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("Titulo", "Enter Name");
        getDialog().setTitle(title);
    }

}