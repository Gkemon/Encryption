package com.gk.emon.encryption;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etEncrypt, etDecrypt;
    TextView tvEncrypt, tvDecrypt;
    Button btnCopyEncrypt, btnCopyDecrypt, btnDoEncrypt, btnDoDecrypt;
    List<Integer> randoms ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEncrypt = findViewById(R.id.et_encrypt);
        etDecrypt = findViewById(R.id.et_decrypt);

        tvEncrypt = findViewById(R.id.tv_encrypted_message);
        tvDecrypt = findViewById(R.id.tv_decrypted_message);

        btnCopyEncrypt = findViewById(R.id.btn_copy_encrypted_message);
        btnCopyDecrypt = findViewById(R.id.btn_copy_decrypted_message);
        btnDoDecrypt = findViewById(R.id.btn_decrypted_message);
        btnDoEncrypt = findViewById(R.id.btn_encrypted_message);


        btnDoEncrypt.setOnClickListener(v -> {
            randoms = Encryptor.RandomGenerator.getRandoms(etEncrypt.getText().toString().length());
            Encryptor.Encryption encryption = new Encryptor.Encryption("AIB2", "END");
            tvEncrypt.setText(encryption.encryption(etEncrypt.getText().toString(), randoms));
        });


        btnCopyEncrypt.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Your encrypted message", tvEncrypt.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this,"Copy done",Toast.LENGTH_SHORT).show();
            tvEncrypt.setText("");
        });


        btnDoDecrypt.setOnClickListener(v -> {
            Encryptor.Encryption encryption = new Encryptor.Encryption("AIB2", "END");
            tvDecrypt.setText(encryption.decryption(etDecrypt.getText().toString(),randoms));
        });


        btnCopyDecrypt.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Your decrypted message", tvDecrypt.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this,"Copy done",Toast.LENGTH_SHORT).show();
            tvDecrypt.setText("");
        });





    }

    /**
     * @param context use this if you want to get string from a file something named like "originalText.txt"
     * @return the whole string from the specific file
     */
    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("originalText.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    /**
     * @param data    If you willing to save a string into file
     * @param context
     */
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(context.openFileOutput("originalText.txt",
                            Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}