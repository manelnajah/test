package com.example.securecontacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private ContactStorage storage;
    private List<Contact> contacts;
    private ContactsAdapter adapter;

    /** Launcher pour ajouter un contact */
    private final ActivityResultLauncher<Intent> addContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String name = result.getData().getStringExtra(AddContactActivity.EXTRA_NAME);
                    String phone = result.getData().getStringExtra(AddContactActivity.EXTRA_PHONE);

                    contacts.add(new Contact(name, phone));
                    adapter.notifyItemInserted(contacts.size() - 1);
                    storage.saveContacts(contacts);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE
        );


        storage = new ContactStorage(this);

        // Lancement immédiat de la reconnaissance faciale
        startBiometricAuth();
    }

    /** Initialisation une fois l'utilisateur authentifié */
    private void initUi() {
        RecyclerView rv = findViewById(R.id.rvContacts);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        contacts = storage.loadContacts();

        adapter = new ContactsAdapter(
                contacts,
                new ContactsAdapter.OnItemInteraction() {

                    @Override
                    public void onDelete(int position) {
                        contacts.remove(position);
                        adapter.notifyItemRemoved(position);
                        storage.saveContacts(contacts);
                    }

                    @Override
                    public void onCall(int position) {
                        showCallDialog(contacts.get(position));
                    }

                    @Override
                    public void onSms(int position) {
                        sendSms(contacts.get(position));
                    }
                }
        );

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddContactActivity.class);
            addContactLauncher.launch(i);
        });
    }

    /** 📞 Dialogue avant d'appeler */
    private void showCallDialog(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Appeler " + contact.getName())
                .setMessage("Voulez-vous appeler ce contact sécurisé ?")
                .setPositiveButton("Appeler", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                    startActivity(callIntent);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    /** 📩 Envoi SMS */
    private void sendSms(Contact contact) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + contact.getPhone())); // Aucun besoin de permission !
        smsIntent.putExtra("sms_body", ""); // Message vide

        startActivity(smsIntent);
    }

    /** 🔐 Authentification biométrique */
    private void startBiometricAuth() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt.AuthenticationCallback callback =
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Authentification réussie", Toast.LENGTH_SHORT).show();
                            initUi();
                        });
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Erreur: " + errString, Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Échec de l’authentification", Toast.LENGTH_SHORT).show());
                    }
                };

        BiometricPrompt prompt = new BiometricPrompt(this, executor, callback);

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Déverrouiller Secure Contacts")
                .setSubtitle("Authentifiez-vous pour accéder à vos contacts")
                .setNegativeButtonText("Annuler")
                .build();

        prompt.authenticate(promptInfo);
    }
}
