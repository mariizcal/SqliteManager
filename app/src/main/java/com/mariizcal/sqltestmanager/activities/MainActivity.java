package com.mariizcal.sqltestmanager.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mariizcal.sqltestmanager.R;
import com.mariizcal.sqltestmanager.database.DBConfiguration;
import com.mariizcal.sqltestmanager.database.DaoUsuario;
import com.mariizcal.sqltestmanager.model.DBModel;
import com.mariizcal.sqltestmanager.model.Rol;
import com.mariizcal.sqltestmanager.model.Usuario;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBConfiguration.setUpDataBase(this, "test.db", 1, Usuario.class, Rol.class);
        DBConfiguration db = DBConfiguration.getInstance();
        System.out.println(db.getDbName());

        String hello = "hello";
        String getMethod = "get" + hello.substring(0,1).toUpperCase() + hello.substring(1);
        System.out.println(getMethod);

        try {
            System.out.println(Rol.class.getDeclaredField("enabled").getType().getSimpleName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        DaoUsuario dao = new DaoUsuario();
        Usuario u = new Usuario();
        u.setAge(23);
        u.setEnable(false);
        u.setHeight(1.70F);
        u.setLastName("Yo");
        u.setName("El actualizado");
//        dao.insert(u);
//        dao.update(u, 3);

    }

}
