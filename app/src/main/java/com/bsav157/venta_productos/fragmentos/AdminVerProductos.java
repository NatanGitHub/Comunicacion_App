package com.bsav157.venta_productos.fragmentos;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bsav157.venta_productos.AdapterRecycler;
import com.bsav157.venta_productos.Interfaces.RegistroListener;
import com.bsav157.venta_productos.Productos;
import com.bsav157.venta_productos.R;
import com.google.firebase.database.DatabaseReference;
import com.ornach.nobobutton.NoboButton;

import java.util.ArrayList;

public class AdminVerProductos extends DialogFragment implements RegistroListener {

    private static ArrayList<Productos> productos = new ArrayList<>();;
    private DatabaseReference referenciaBD;
    private NoboButton regresar;

        public AdminVerProductos() {
            // Required empty public constructor
        }

        public static AdminVerProductos newInstance(String title, ArrayList<Productos> recibeProductos) {
            AdminVerProductos frag = new AdminVerProductos();
            productos = recibeProductos;
            Bundle args = new Bundle();
            args.putString("Titulo", title);
            frag.setArguments(args);
            frag.setCancelable(false);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View vista = inflater.inflate(R.layout.fragment_admin_ver_productos, container);

            initItems(vista);
            cargarRecycler(vista);

            return vista;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Fetch arguments from bundle and set title
            String title = getArguments().getString("Titulo", "Productos");
            getDialog().setTitle(title);
            onResume();
        }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 1.0), (int) (size.y * 1.0));
        //window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void initItems(View vista){

        regresar = vista.findViewById(R.id.regresar);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

    }

    public void cargarRecycler(View vista){

        AdapterRecycler myAdapter = new AdapterRecycler( productos, getContext(), "admin", getDialog() );
        RecyclerView recyclerView = vista.findViewById(R.id.recycler_admin);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void onFinishRegistroDialog(String correo, String clave) {

    }

    @Override
    public void onFinishQuestionDialog() {

    }
}