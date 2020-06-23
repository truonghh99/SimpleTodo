package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> items;

    Button buttonAdd;
    EditText newItemName;
    RecyclerView itemListView;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        loadItems();

        buttonAdd = findViewById(R.id.buttonAdd);
        newItemName = findViewById(R.id.newItemName);
        itemListView = findViewById(R.id.itemListView);
        itemsAdapter = new ItemsAdapter(items, onLongClickListener);

        itemListView.setAdapter(itemsAdapter);
        itemListView.setLayoutManager(new LinearLayoutManager(this));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = newItemName.getText().toString();
                items.add(itemName);
                itemsAdapter.notifyItemInserted(items.size() - 1);
                newItemName.setText("Add new item here");
                Toast.makeText(getApplicationContext(), "Item was added!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }


    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // Load stored items from data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error Reading Items", e);
            items = new ArrayList<>();
        }
    }
    // Write items to data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error Saving Items", e);
        }
    }
}