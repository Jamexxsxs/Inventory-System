package com.example.triplesagent;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class main extends AppCompatActivity {

    private int content = 0;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private String product = "";
    private String quantity = "";
    private String price = "";
    private String subtotal = "";
    private double overall = 0;
    private double gross_calc = 0;
    private double profit_calc = 0;
    private ArrayList<String[]> data = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);

        TextView add_product_button = findViewById(R.id.add_product);
        TextView manage_product_button = findViewById(R.id.manage_product);
        TextView add_sale_button = findViewById(R.id.add_sale);
        TextView sale_report_button = findViewById(R.id.sale_report);
        TextView[] tab_array = {add_sale_button, manage_product_button, add_product_button, sale_report_button};
        LinearLayout sale_report_content = findViewById(R.id.sale_report_view);
        LinearLayout add_product_content = findViewById(R.id.add_product_view);
        LinearLayout manage_product_content = findViewById(R.id.manage_product_view);
        LinearLayout add_sale_content = findViewById(R.id.add_sale_view);
        LinearLayout[] content_array = {add_sale_content, manage_product_content, add_product_content, sale_report_content};

        change_tab(content_array, tab_array, add_product_button, manage_product_button, add_sale_button, sale_report_button);


        shared_preference.printSharedPreferences(this, "products");
        shared_preference.printSharedPreferences(this, "record");
        sale_add();
        sale_clearAll();
        sale_done();
        record_table();
        manage_table(tab_array, content_array);
        autocomplete_name();
        add_done(tab_array, content_array);
    }

    private void change_tab(LinearLayout[] content_array, TextView[] tab_array, TextView add_product_button, TextView manage_product_button, TextView add_sale_button, TextView sale_report_button){

        add_product_button.setOnClickListener(view ->  {
            content_array[0].setVisibility(View.GONE);
            content_array[1].setVisibility(View.GONE);
            content_array[3].setVisibility(View.GONE);
            content_array[2].setVisibility(View.VISIBLE);
            content = 2;
            change_nav(tab_array);
        });

        manage_product_button.setOnClickListener(view ->  {
            content_array[0].setVisibility(View.GONE);
            content_array[2].setVisibility(View.GONE);
            content_array[3].setVisibility(View.GONE);
            content_array[1].setVisibility(View.VISIBLE);
            content = 1;
            change_nav(tab_array);
        });

        add_sale_button.setOnClickListener(view ->  {
            content_array[2].setVisibility(View.GONE);
            content_array[1].setVisibility(View.GONE);
            content_array[3].setVisibility(View.GONE);
            content_array[0].setVisibility(View.VISIBLE);
            content = 0;
            change_nav(tab_array);
        });

        sale_report_button.setOnClickListener(view ->  {
            content_array[0].setVisibility(View.GONE);
            content_array[1].setVisibility(View.GONE);
            content_array[2].setVisibility(View.GONE);
            content_array[3].setVisibility(View.VISIBLE);
            content = 3;
            change_nav(tab_array);
        });
    }

    private void change_nav(TextView[] tab_array){
        for(int i = 0; i < tab_array.length; i++){
            if(i!=content){
                tab_array[i].setTextColor(Color.BLACK);
                tab_array[i].setBackgroundColor(Color.TRANSPARENT);
            }
            else{
                tab_array[i].setTextColor(Color.WHITE);
                tab_array[i].setBackgroundColor(Color.parseColor("#8EBBFF"));
            }
        }
    }

    private void sale_add(){
        TextView add_button = findViewById(R.id.add);

        add_button.setOnClickListener(view -> {
            Dialog customDialog = new Dialog(main.this);
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

                    add_sale_table();
                }
            });

            customDialog.show();
        });
    }

    private void sale_clearAll(){
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
    private void sale_done(){
        SharedPreferences record_sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);
        SharedPreferences.Editor record_editor = record_sharedPreferences.edit();

        SharedPreferences product_sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);
        SharedPreferences.Editor product_editor = product_sharedPreferences.edit();

        TextView done = findViewById(R.id.sale_done);

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

            TableLayout table1 = findViewById(R.id.sale_table_layout);

            for (int i = table1.getChildCount() - 1; i > 0; i--) {
                View childView = table1.getChildAt(i);
                if (childView instanceof TableRow) {
                    table1.removeView(childView);
                }
            }

            record_table();
        });
    }

    private void add_sale_table(){
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

    private void record_table(){
        TableLayout table = findViewById(R.id.sale_table_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);

        Map<String, Integer> filteredMap = new HashMap<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.contains("date")){
                filteredMap.put(key, Integer.parseInt(value.toString().replaceAll("/", "")));
            }
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(filteredMap.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                // Sorting in descending order
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // Creating a new LinkedHashMap to store the sorted entries
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            String key = entry.getKey();

            TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.record_table, null);

            TextView strdate = tableRow.findViewById(R.id.date);
            strdate.setText(sharedPreferences.getString(key, ""));
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
                    else if (key1.contains("name")) {
                        TextView strname = tableRow.findViewById(R.id.name);
                        strname.setText(value1.toString());
                    }
                }
            }
        }
    }

    private void manage_table(TextView[] tab_array, LinearLayout[] content_array){
        TableLayout table = findViewById(R.id.manage_table_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("products", MODE_PRIVATE);

        Map<String, Integer> filteredMap = new HashMap<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.contains("quantity")){
                filteredMap.put(key, Integer.parseInt(value.toString()));
            }
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(filteredMap.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry1.getValue().compareTo(entry2.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            String key = entry.getKey();

            TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row, null);

            TextView strname = tableRow.findViewById(R.id.quantity);
            strname.setText(sharedPreferences.getString(key, ""));
            table.addView(tableRow);

            String product = key.split("quantity")[0];
            System.out.println(product);

            for (Map.Entry<String, ?> entry1 : allEntries.entrySet()) {
                String key1 = entry1.getKey();
                Object value1 = entry1.getValue();

                if(key1.contains(product)){
                    if(key1.contains("name")){
                        TextView strquantity = tableRow.findViewById(R.id.name);
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
                        content_array[0].setVisibility(View.GONE);
                        content_array[1].setVisibility(View.GONE);
                        content_array[3].setVisibility(View.GONE);
                        content_array[2].setVisibility(View.VISIBLE);
                        content = 2;
                        change_nav(tab_array);
                        EditText textView = findViewById(R.id.product_name);
                        textView.setText(product);
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

                    int childCount = table.getChildCount();

                    for (int i = childCount - 1; i > 0; i--) {
                        View childView = table.getChildAt(i);
                        if (childView instanceof TableRow) {
                            table.removeView(childView);
                        }
                    }
                    manage_table(tab_array, content_array);
                });
            }
        }
    }

    private void add_done(TextView[] tab_array, LinearLayout[] content_array){
        TextView done_button = findViewById(R.id.done);
        TextView cancel_button = findViewById(R.id.cancel);
        AutoCompleteTextView product_name = findViewById(R.id.product_name);
        EditText quantity = findViewById(R.id.quantity_edit);
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

                product_name.setText("");
                quantity.setText("");
                purchase_price.setText("");
                sale_price.setText("");
                gross_sale.setText("");
                profit.setText("");

                TableLayout table = findViewById(R.id.manage_table_layout);
                int childCount = table.getChildCount();

                for (int i = childCount - 1; i > 0; i--) {
                    View childView = table.getChildAt(i);
                    if (childView instanceof TableRow) {
                        table.removeView(childView);
                    }
                }

                manage_table(tab_array, content_array);
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

