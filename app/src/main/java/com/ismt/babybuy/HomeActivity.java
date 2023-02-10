package com.ismt.babybuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class HomeActivity extends AppCompatActivity {


    //public static SQLiteHelper sqLiteHelper
    Items items;
    DatabaseHelper databaseHelper;
    TextView textView, logout;
    RecyclerView recyclerView;
    ItemAdapter mAdapter;
    ArrayList<Items> allItems;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setTitle("Alert !!!");
                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(false);
                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click yes button then app will close
                        Toast.makeText(HomeActivity.this, "Item deleted successfully.", Toast.LENGTH_SHORT).show();
                        databaseHelper.deleteItem(allItems.get(position).getId());
                        startActivity(homeIntent);
                        finish();
                    });
                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.

                        dialog.cancel();
                    });
                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();
                    break;
//                case ItemTouchHelper.RIGHT:
//                    Toast.makeText(HomeActivity.this, "Swipe right to left", Toast.LENGTH_SHORT).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("id", allItems.get(position).getId());
//                    bundle.putString("name", allItems.get(position).getItemsName());
//                    bundle.putByteArray("image", allItems.get(position).getImages());
//                    bundle.putFloat("price", allItems.get(position).getPrice());
//                    bundle.putString("description", allItems.get(position).getDescription());
//                    Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
//                    intent.putExtra("itemData", bundle);
//                    startActivity(intent);
//                    finish();
//                    break;
                case ItemTouchHelper.RIGHT:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                    builder1.setMessage("Do you want to mark item as purchased ?");
                    builder1.setTitle("Alert !!!");
                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder1.setCancelable(false);
                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click yes button then app will close
                        Toast.makeText(HomeActivity.this, "Item marked as purchased.", Toast.LENGTH_SHORT).show();
                        databaseHelper.markItemAsPurchased(allItems.get(position).getId());
                        startActivity(homeIntent);
                        finish();
                    });
                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                    builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        dialog.cancel();
                    });
                    // Create the Alert dialog
                    AlertDialog alertDialog1 = builder1.create();
                    // Show the Alert Dialog box
                    alertDialog1.show();

                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.purple_500))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initRecyclerView();
        intFab();
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void intFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddItems);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initRecyclerView() {

        databaseHelper = new DatabaseHelper(HomeActivity.this);
        items = new Items();
        textView = findViewById(R.id.textViewEmpty);
        recyclerView = findViewById(R.id.recyclerItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        allItems = databaseHelper.listItems();
        if (allItems.size() > 0) {
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ItemAdapter(HomeActivity.this, allItems);
            recyclerView.setAdapter(mAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        } else {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(getResources().getText(R.string.empty_image));
            Toast.makeText(this, "No data exists. Start adding items.", Toast.LENGTH_LONG).show();
        }


    }
}