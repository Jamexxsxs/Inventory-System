package com.example.triplesagent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class sale_record extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_record);

        change_tab();
        table();
    }

    private void change_tab(){
        TextView add_product_button = findViewById(R.id.add_product);
        TextView manage_product_button = findViewById(R.id.manage_product);
        TextView add_sale_button = findViewById(R.id.add_sale);

        add_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sale_record.this, add_products.class);
                startActivity(intent);
            }
        });

        manage_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sale_record.this, manage_products.class);
                startActivity(intent);
            }
        });

        add_sale_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sale_record.this, add_sale.class);
                startActivity(intent);
            }
        });
    }

    private void table(){
        TableLayout table = findViewById(R.id.table_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.contains("name")) {
                TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.record_table, null);

                TextView strname = tableRow.findViewById(R.id.name);
                strname.setText(value.toString());
                table.addView(tableRow);

                String id = key.split("_")[1];
                System.out.println(id);

                for (Map.Entry<String, ?> entry1 : allEntries.entrySet()) {
                    String key1 = entry1.getKey();
                    Object value1 = entry1.getValue();

                    if(key1.contains(id)){
                        if(key1.contains("quantity")){
                            TextView strquantity = tableRow.findViewById(R.id.quantity);
                            strquantity.setText(value1.toString());
                        }
                        else if(key1.contains("product price")){
                            TextView strquantity = tableRow.findViewById(R.id.product_price);
                            strquantity.setText("₱" + value1.toString());
                        }
                        else if(key1.contains("subtotal")){
                            TextView strgross = tableRow.findViewById(R.id.subtotal);
                            strgross.setText("₱" + value1.toString());
                        }
                        else if (key1.contains("date")) {
                            TextView strdate = tableRow.findViewById(R.id.date);
                            strdate.setText(value1.toString());
                        }
                    }
                }
            }
        }
    }
}
