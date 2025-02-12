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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    ArrayList<Art> artArrayList;
    ArtAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        artArrayList = new ArrayList<Art>();


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArtAdapter(artArrayList);
        binding.recyclerView.setAdapter(adapter);

        getdata();

    }

    //SqLite verilerini çekmek için getData metodunu oluşturduk
    public void getdata() {
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM arts", null);
            int nameIx = cursor.getColumnIndex("artname");
            int idIx = cursor.getColumnIndex("id");
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Art art = new Art(name, id);
                artArrayList.add(art);
            }
            adapter.notifyDataSetChanged();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Menüyü koda bağlamak için onCreateOptionsMenu ve onOptionsItemSelected metodlarını kullanıyoruz.

    //Menüyü oluşturmak için onCreateOptionsMenu metodunu oluşturduk
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menüdeki elemanlara tıklanınca ne olacağını belirlemek için onOptionsItemSelected metodunu oluşturduk
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_art) {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("info", "new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}