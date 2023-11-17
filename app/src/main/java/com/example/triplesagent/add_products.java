package com.example.triplesagent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class add_products extends AppCompatActivity {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    double gross_calc = 0;
    double profit_calc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product); // Set the layout for the AddProductActivity

        shared_preference.printSharedPreferences(this, "products");
        Intent intent = getIntent();

        String receivedValue = intent.getStringExtra("name");
        EditText textView = findViewById(R.id.product_name);
        textView.setText(receivedValue);

        change_tab();
        done();
        autocomplete_name();
    }

    private void change_tab(){
        TextView manage_product_button = findViewById(R.id.manage_product);
        TextView add_sale_button = findViewById(R.id.add_sale);

        manage_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_products.this, manage_products.class);
                startActivity(intent);
            }
        });

        add_sale_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_products.this, add_sale.class);
                startActivity(intent);
            }
        });

        TextView sale_report_button = findViewById(R.id.sale_report);

        sale_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_products.this, sale_record.class);
                startActivity(intent);
            }
        });
    }

    private void done(){
        TextView done_button = findViewById(R.id.done);
        TextView cancel_button = findViewById(R.id.cancel);
        AutoCompleteTextView product_name = findViewById(R.id.product_name);
        EditText quantity = findViewById(R.id.quantity);
        EditText purchase_price = findViewById(R.id.purchase_price);
        EditText sale_price = findViewById(R.id.sale_price);
        EditText gross_sale = findViewById(R.id.gross_sale);
        EditText profit = findViewById(R.id.profit);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int valid = 0;
                String strproduct_name = product_name.getText().toString();
                String strquantity = quantity.getText().toString();
                String strpurchase_price = purchase_price.getText().toString();
                String strsale_price = sale_price.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);

                Map<String, ?> allEntries = sharedPreferences.getAll();

                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if(key.equals(strproduct_name +"name")){
                        strquantity = (Integer.parseInt(strquantity) + Integer.parseInt(sharedPreferences.getString(strproduct_name + "quantity", "0"))) + "";
                        strpurchase_price = decimalFormat.format((Double.parseDouble(strpurchase_price) + Double.parseDouble(sharedPreferences.getString(strproduct_name + "purchase price", "0")))) + "";
                        gross_calc = gross_calc + Double.parseDouble(sharedPreferences.getString(strproduct_name + "gross", "0"));
                    }
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(strproduct_name + "name", strproduct_name);
                editor.putString(strproduct_name + "quantity", strquantity);
                editor.putString(strproduct_name + "purchase price", strpurchase_price);
                editor.putString(strproduct_name + "sale price", strsale_price);
                editor.putString(strproduct_name + "gross", decimalFormat.format(gross_calc));
                editor.putString(strproduct_name + "profit", decimalFormat.format(profit_calc));
                editor.putString(strproduct_name + "over", "0");
                editor.apply();

                Intent intent = new Intent(add_products.this, manage_products.class);
                startActivity(intent);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strproduct_name = product_name.getText().toString();
                String strquantity = quantity.getText().toString();
                String strpurchase_price = purchase_price.getText().toString();
                String strsale_price = sale_price.getText().toString();

                try {
                    gross_calc = Integer.parseInt(strquantity) * Double.parseDouble(strsale_price);
                    profit_calc = gross_calc - Double.parseDouble(strpurchase_price);
                    gross_sale.setText(decimalFormat.format(gross_calc));
                    profit.setText(decimalFormat.format(profit_calc));
                } catch (NumberFormatException ignored) {
                }

                done_button.setEnabled(true);
            }
        });
    }

    private void autocomplete_name(){
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

        AutoCompleteTextView product_name = findViewById(R.id.product_name);

        product_name.setAdapter(adapter);
    }

}