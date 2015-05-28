package com.mariizcal.sqltestmanager.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mariizcal.sqltestmanager.R;
import com.mariizcal.sqltestmanager.database.DBConfiguration;
import com.mariizcal.sqltestmanager.database.DaoUsuario;
import com.mariizcal.sqltestmanager.model.Rol;
import com.mariizcal.sqltestmanager.model.Usuario;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBConfiguration.setUpDataBase(this, "test.db", 1, Usuario.class, Rol.class);
        DBConfiguration db = DBConfiguration.getInstance();
        System.out.println(db.getDbName());

        String hello = "hello";
        String getMethod = "get" + hello.substring(0, 1).toUpperCase() + hello.substring(1);
        System.out.println(getMethod);

        try {
            System.out.println(Rol.class.getDeclaredField("enabled").getType().getSimpleName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        final DaoUsuario dao = new DaoUsuario();
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario u = new Usuario();
                u.setAge(3);
                u.setEnable(true);
                u.setHeight(1.20F);
                u.setName("Porematis");
                u.setLastName("Lopertun");

                dao.insert(u);
//                dao.update(u, 2);
            }
        });

        List<Object> usuarios = dao.getAll()
                .find();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = (Usuario) usuarios.get(i);
            System.out.println("****** ----> " + usuario.getName() + " " + usuario.getLastName());
        }

    }

}
