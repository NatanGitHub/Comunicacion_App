package com.bsav157.venta_productos;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extras {

    Context context;

    public Extras(Context context) {
        this.context = context;
    }

    public boolean isOnline() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

}
