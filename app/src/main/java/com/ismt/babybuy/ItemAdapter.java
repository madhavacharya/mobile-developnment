package com.ismt.babybuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Items> listItems;
    private final DatabaseHelper databaseHelper;

    ItemAdapter(Context context, ArrayList<Items> listItems) {
        this.context = context;
        this.listItems = listItems;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        final Items items = listItems.get(position);
        holder.itemName.setText("Item Name:" + items.getItemsName());
        byte[] image = items.getImages();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.itemsImages.setImageBitmap(bitmap);
        holder.itemPrice.setText("Item Price: " + items.getPrice());
        holder.itemDescription.setText("Description: " + items.getDescription());
        if (items.getPurchased() != 1) {
            holder.itemPurchased.setText("Not Purchased");
        }


        holder.itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Home Activity intent
                Intent homeIntent = new Intent(context, HomeActivity.class);
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.itemMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Bundle bundle = new Bundle();
                                bundle.putInt("id", items.getId());
                                bundle.putString("name", items.getItemsName());
                                bundle.putByteArray("image", items.getImages());
                                bundle.putFloat("price", items.getPrice());
                                bundle.putString("description", items.getDescription());
                                Intent intent = new Intent(context, AddItemActivity.class);
                                intent.putExtra("itemData", bundle);
                                context.startActivity(intent);

                                ((Activity) context).finish();

                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Do you want to delete ?");
                                builder.setTitle("Alert !!!");
                                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                builder.setCancelable(false);
                                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    // When the user click yes button then app will close
                                databaseHelper.deleteItem(items.getId());
                                context.startActivity(homeIntent);
                                ((Activity) context).finish();
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
                            case R.id.purchase:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setMessage("Do you want to mark as purchased ?");
                                builder1.setTitle("Alert !!!");
                                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                builder1.setCancelable(false);
                                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    // When the user click yes button then app will close
                                    databaseHelper.markItemAsPurchased(items.getId());
                                    context.startActivity(homeIntent);
                                    ((Activity) context).finish();
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
                            case R.id.sms:
                                sendSmsDialog(items);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    private void sendSmsDialog(Items item) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.sms_items);
        dialog.setTitle("SEND SMS");
        final EditText edNumber = dialog.findViewById(R.id.edtPhone);
        Button btnSend = dialog.findViewById(R.id.btnSend);
        // set width for dialog
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS(edNumber.getText().toString(), item);
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private void sendSMS(String phoneNo, Items item) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String msg = "Please purchase this Item."+
                    "Item Name: " + item.getItemsName() +
                    "Price: " + item.getPrice() +
                    " Description: " + item.getDescription();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemMenu, itemPrice, itemPurchased, itemDescription;
        ImageView itemsImages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.txtName);
            itemsImages = itemView.findViewById(R.id.imgItems);
            itemMenu = itemView.findViewById(R.id.textViewOptions);
            itemPurchased = itemView.findViewById(R.id.txtPurchased);
            itemPrice = itemView.findViewById(R.id.txtPrice);
            itemDescription = itemView.findViewById(R.id.txtDescription);
        }
    }


}
