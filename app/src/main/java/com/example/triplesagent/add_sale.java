package com.example.triplesagent;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class add_sale extends AppCompatActivity {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    String product = "";
    String quantity = "";
    String price = "";
    String subtotal = "";
    double overall = 0;
    ArrayList<String[]> data = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sale);

        change_tab();
        add();
        clearAll();
        done();

        shared_preference.printSharedPreferences(this, "products");
        shared_preference.printSharedPreferences(this, "record");
    }

    private void change_tab(){
        TextView add_product_button = findViewById(R.id.add_product);
        TextView manage_product_button = findViewById(R.id.manage_product);

        add_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_sale.this, add_products.class);
                startActivity(intent);
            }
        });

        manage_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_sale.this, manage_products.class);
                startActivity(intent);
            }
        });

        TextView sale_report_button = findViewById(R.id.sale_report);

        sale_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_sale.this, sale_record.class);
                startActivity(intent);
            }
        });
    }

    private void add(){
        TextView add_button = findViewById(R.id.add);

        add_button.setOnClickListener(view -> {
            Dialog customDialog = new Dialog(add_sale.this);
            customDialog.setContentView(R.layout.add_popup);

            SharedPreferences sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);

            Map<String, ?> allEntries = sharedPreferences.getAll();

            List<String> suggestions = new ArrayList<>();

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.contains("name")) {
                    suggestions.add(value.toString());
                    System.out.println(value.toString());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions
            );

            AutoCompleteTextView product_name = customDialog.findViewById(R.id.product_name);

            product_name.setAdapter(adapter);


            TextView closeButton = customDialog.findViewById(R.id.cancel);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });

            TextView doneButton = customDialog.findViewById(R.id.done);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("HELLO");
                    TextView strproduct = customDialog.findViewById(R.id.product_name);
                    product = strproduct.getText().toString();

                    TextView strquantity = customDialog.findViewById(R.id.quantity);
                    quantity = strquantity.getText().toString();

                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if (key.equals(product + "sale price")) {
                            price = value.toString();
                        }
                    }

                    subtotal = decimalFormat.format(Double.parseDouble(price) * Integer.parseInt(quantity));

                    overall += Double.parseDouble(subtotal);

                    customDialog.dismiss();

                    String[] strdata = {product, price, quantity, subtotal};
                    data.add(strdata);

                    table();
                }
            });

            customDialog.show();
        });
    }

    private void clearAll(){
        TextView clear = findViewById(R.id.clear);

        clear.setOnClickListener(view -> {
            TableLayout table = findViewById(R.id.table_layout);
            int childCount = table.getChildCount();

            for (int i = childCount - 1; i > 0; i--) {
                View childView = table.getChildAt(i);
                if (childView instanceof TableRow) {
                    table.removeView(childView);
                }
            }

            data = new ArrayList<>();
            overall = 0;

            TextView total_row = findViewById(R.id.total);
            total_row.setText("₱" + decimalFormat.format(overall));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void done(){
        SharedPreferences record_sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);
        SharedPreferences.Editor record_editor = record_sharedPreferences.edit();

        SharedPreferences product_sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);
        SharedPreferences.Editor product_editor = product_sharedPreferences.edit();

        TextView done = findViewById(R.id.done);

        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        String formattedDate = currentDate.format(formatter);

        done.setOnClickListener(view -> {
            for (String[] array : data) {
                String strRandom = getRandom(6);
                record_editor.putString("date_" + strRandom, formattedDate);
                record_editor.putString("product name_" + strRandom, array[0]);
                record_editor.putString("product price_" + strRandom, array[1]);
                record_editor.putString("quantity_" + strRandom, array[2]);
                record_editor.putString("subtotal_" + strRandom, array[3]);
                record_editor.apply();

                Map<String, ?> allEntries = product_sharedPreferences.getAll();

                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if(key.equals(array[0] + "quantity")){
                        System.out.println(decimalFormat.format(Integer.parseInt(value.toString()) - Integer.parseInt(array[2])));
                        product_editor.putString(key, decimalFormat.format(Integer.parseInt(value.toString()) - Integer.parseInt(array[2])));
                    }
                    else if(key.equals(array[0] + "over")){
                        product_editor.putString(key, decimalFormat.format(Integer.parseInt(value.toString()) + Integer.parseInt(array[3])));
                    }

                    product_editor.apply();
                }
            }

            TableLayout table = findViewById(R.id.table_layout);
            int childCount = table.getChildCount();

            for (int i = childCount - 1; i > 0; i--) {
                View childView = table.getChildAt(i);
                if (childView instanceof TableRow) {
                    table.removeView(childView);
                }
            }

            data = new ArrayList<>();
            overall = 0;

            TextView total_row = findViewById(R.id.total);
            total_row.setText("₱" + decimalFormat.format(overall));
        });
    }

    private void table(){
        TableLayout table = findViewById(R.id.table_layout);
        TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.add_table_row, null);

        TextView name_row = tableRow.findViewById(R.id.name);
        name_row.setText(product);

        TextView quantity_row = tableRow.findViewById(R.id.quantity);
        quantity_row.setText(quantity);

        TextView price_row = tableRow.findViewById(R.id.product_price);
        price_row.setText("₱" + price);

        TextView subtotal_row = tableRow.findViewById(R.id.subtotal);
        subtotal_row.setText("₱" + subtotal);

        TextView total_row = findViewById(R.id.total);
        total_row.setText("₱" + decimalFormat.format(overall));

        table.addView(tableRow);
    }

    private static String getRandom(int length) {
        // Define the character set from which to choose random characters
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a StringBuilder to store the random characters
        StringBuilder randomStringBuilder = new StringBuilder(length);

        // Create a Random object
        Random random = new Random();

        // Generate random characters
        for (int i = 0; i < length; i++) {
            // Get a random index from the character set
            int randomIndex = random.nextInt(characterSet.length());

            // Append the random character to the StringBuilder
            randomStringBuilder.append(characterSet.charAt(randomIndex));
        }

        // Convert the StringBuilder to a String and return
        return randomStringBuilder.toString();
    }
}
