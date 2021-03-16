package com.bsav157.venta_productos.fragmentos;

import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.bsav157.venta_productos.Interfaces.RegistroListener;
import com.bsav157.venta_productos.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends DialogFragment implements RegistroListener {

    EditText correo, clave, confirmaClave;
    Button registrar, cancelar;

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
        String title = getArguments().getString("Titulo", "Registro");
        getDialog().setTitle(title);
        onResume();
        initItems(view);

    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.93), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void initItems(View vista){

        correo = vista.findViewById(R.id.correo);
        clave = vista.findViewById(R.id.clave);
        confirmaClave = vista.findViewById(R.id.valide_clave);
        registrar = vista.findViewById(R.id.boton);
        cancelar = vista.findViewById(R.id.cancelar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( correo.getText().toString().isEmpty() ){
                    correo.setError("Introduzca un correo");
                    return;
                }

                if (!correoValido(correo.getText().toString().trim())) {
                    correo.setError("Formato de correo invalido");
                    return;
                }

                if( clave.getText().toString().trim().length() < 5 ){
                    clave.setError("La contraseña es muy corta");
                    return;
                }

                if( clave.getText().toString().isEmpty() || confirmaClave.getText().toString().isEmpty() ){
                    clave.setError("Rellene ambos campos");
                    confirmaClave.setError("Rellene ambos campos");
                    return;
                }


                RegistroListener listener = (RegistroListener) getActivity();
                listener.onFinishRegistroDialog(correo.getText().toString().trim(), clave.getText().toString().trim());
                dismiss();

            }
        });

    }

    public boolean correoValido(String correo){

        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(correo);

        if (!mather.find()) {
            return false;
        }

        return true;

    }


    @Override
    public void onFinishRegistroDialog(String correo, String Clave) {
        
    }

    @Override
    public void onFinishQuestionDialog() {

    }
}