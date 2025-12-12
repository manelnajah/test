package com.example.securecontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_PHONE = "extra_phone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        EditText etName = findViewById(R.id.etName);
        EditText etPhone = findViewById(R.id.etPhone);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                // simple validation
                if (name.isEmpty()) etName.setError("Nom requis");
                if (phone.isEmpty()) etPhone.setError("Téléphone requis");
                return;
            }
            Intent res = new Intent();
            res.putExtra(EXTRA_NAME, name);
            res.putExtra(EXTRA_PHONE, phone);
            setResult(Activity.RESULT_OK, res);
            finish();
        });
    }
}
