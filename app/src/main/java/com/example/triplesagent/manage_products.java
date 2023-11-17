package com.example.triplesagent;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class manage_products extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        change_tab();
        table();
    }

    private void change_tab(){
        TextView add_product_button = findViewById(R.id.add_product);
        TextView add_sale_button = findViewById(R.id.add_sale);

        add_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_products.this, add_products.class);
                startActivity(intent);
            }
        });

        add_sale_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_products.this, add_sale.class);
                startActivity(intent);
            }
        });

        TextView sale_report_button = findViewById(R.id.sale_report);

        sale_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_products.this, sale_record.class);
                startActivity(intent);
            }
        });
    }

    private void table(){
        TableLayout table = findViewById(R.id.table_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println(key);

            // Check if the key matches your criteria
            if (key.contains("name")) {
                TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row, null);

                TextView strname = tableRow.findViewById(R.id.name);
                strname.setText(value.toString());
                table.addView(tableRow);

                String product = key.split("name")[0];
                System.out.println(product);

                for (Map.Entry<String, ?> entry1 : allEntries.entrySet()) {
                    String key1 = entry1.getKey();
                    Object value1 = entry1.getValue();

                    if(key1.contains(product)){
                        if(key1.contains("quantity")){
                            TextView strquantity = tableRow.findViewById(R.id.quantity);
                            strquantity.setText(value1.toString());
                        }
                        else if(key1.contains("purchase price")){
                            TextView strquantity = tableRow.findViewById(R.id.purchase_price);
                            strquantity.setText("₱" + value1.toString());
                        }
                        else if(key1.contains("gross")){
                            TextView strgross = tableRow.findViewById(R.id.gross_sale);
                            strgross.setText("₱" + sharedPreferences.getString(product + "over", "0") +  "/" + value1.toString());
                        }
                        else if(key1.contains("sale price")){
                            TextView strgross = tableRow.findViewById(R.id.sale_price);
                            strgross.setText("₱ " + value1.toString());
                        }
                    }

                    TextView strstock = tableRow.findViewById(R.id.stock);

                    strstock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(manage_products.this, add_products.class);
                            intent.putExtra("name", product);
                            startActivity(intent);
                            System.out.println("clickedd");
                        }
                    });

                    TextView delete = tableRow.findViewById(R.id.delete);

                    delete.setOnClickListener(view -> {
                        shared_preference.removeKey(this, "products", product + "name");
                        shared_preference.removeKey(this, "products", product + "quantity");
                        shared_preference.removeKey(this, "products", product + "purchase price");
                        shared_preference.removeKey(this, "products", product + "sale price");
                        shared_preference.removeKey(this, "products", product + "gross");
                        shared_preference.removeKey(this, "products", product + "profit");
                        shared_preference.removeKey(this, "products", product + "over");
                        recreate();
                    });
                }
            }
        }
    }
}
