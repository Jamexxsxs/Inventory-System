package com.example.triplesagent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triplesagent.R;

public class add_products extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product); // Set the layout for the AddProductActivity


        change_tab();
    }

    private void change_tab(){
        TextView add_product_button = findViewById(R.id.manage_product);

        add_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_products.this, manage_products.class);
                startActivity(intent);
            }
        });
    }
}