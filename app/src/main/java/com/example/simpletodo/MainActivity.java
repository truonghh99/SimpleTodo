package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button buttonAdd;
    EditText newItemName;
    RecyclerView itemListView;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open edit view for clicked item
        ItemsAdapter.OnClickListener onClickListener= new ItemsAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(KEY_ITEM_TEXT, items.get(position));
                intent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(intent, EDIT_TEXT_CODE);
            }
        };

        // Remove long-clicked item
        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                removeItem(position);
            }
        };

        // Initialize list using stored list from last use (if any)
        loadItems();

        buttonAdd = findViewById(R.id.buttonAdd);
        newItemName = findViewById(R.id.newItemName);
        itemListView = findViewById(R.id.itemListView);
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);

        itemListView.setAdapter(itemsAdapter);
        itemListView.setLayoutManager(new LinearLayoutManager(this));

        // Implement the Add button: Add new item from user input to the current list
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(newItemName.getText().toString());
            }
        });
    }

    // Handle result from EditActivity by editing selected item based on user's input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Required call to super class for override method
        super.onActivityResult(requestCode, resultCode, data);

        // Update item if result is valid
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemName = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            updateItem(itemName, position);
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    // Get data file that stores the current list
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

    // Add new item to list & notify user
    private void addItem(String itemName) {
        items.add(itemName);
        itemsAdapter.notifyItemInserted(items.size() - 1);
        newItemName.setText("");
        Toast.makeText(getApplicationContext(), "Item was added!", Toast.LENGTH_SHORT).show();
        saveItems();
    }

    // Remove item from list & notify user
    private void removeItem(int position) {
        items.remove(position);
        itemsAdapter.notifyItemRemoved(position);
        Toast.makeText(getApplicationContext(), "Item was removed!", Toast.LENGTH_SHORT).show();
        saveItems();
    }

    // Update existing item in list & notify user
    private void updateItem(String itemName, int position) {
        items.set(position, itemName);
        itemsAdapter.notifyItemChanged(position);
        Toast.makeText(getApplicationContext(), "Item was updated!", Toast.LENGTH_SHORT).show();
        saveItems();
    }
}