package com.example.artbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    ArrayList<Art> artArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getData();

        artArrayList=new ArrayList<Art>();

    }

    //Verileri çekmek içn getData metodunu oluşturduk
    private void getData(){
        SQLiteDatabase sqLitDatabase=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        Cursor cursor=sqLitDatabase.rawQuery("SELECT * FROM arts",null);
        int nameIx=cursor.getColumnIndex("artName");
        int idIx= cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            String name=cursor.getString(nameIx);
            int id=cursor.getInt(idIx);
            Art art=new Art(name,id);
            artArrayList.add(art);
        }
        cursor.close();

    }

    //Menüyü koda bağlamak için onCreateOptionsMenu ve onOptionsItemSelected metodlarını kullanıyoruz.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_art){
            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}