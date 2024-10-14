package de.wladimircomputin.cryptohouse.importexport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.libcryptoiot.v2.protocol.Crypter;

public class ImportDialog {

    public interface OnSuccessCallback{
        public void onSuccess(String plain);
        public void onCancel();
    }

    public static final int IMPORT_BROWSE_INTENT = 13;

    Uri filepath = null;

    public final AlertDialog dialog;
    public final EditText passwordText;
    public final Button browseButton;

    public ImportDialog(@NonNull Context context, Fragment parent, String devices, OnSuccessCallback callback) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_importsettings, null);
        browseButton = dialogView.findViewById(R.id.import_browse_button);
        passwordText = dialogView.findViewById(R.id.import_password_edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.import_settings));
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.import_settings, (dialog1, which) -> {
            try {
                if (filepath != null) {
                    InputStream input = context.getContentResolver().openInputStream(filepath);
                    StringBuilder stringBuilder = new StringBuilder();
                    int ch;
                    while ((ch = input.read()) != -1) {
                        stringBuilder.append((char) ch);
                    }
                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    input.close();

                    Crypter crypter = new Crypter(passwordText.getText().toString());

                    byte iv[] = Base64.decode(jsonObject.getString("iv"), Base64.NO_WRAP);
                    byte tag[] = Base64.decode(jsonObject.getString("tag"), Base64.NO_WRAP);
                    byte data[] = Base64.decode(jsonObject.getString("data"), Base64.NO_WRAP);

                    String plain = new String(crypter.decrypt(data, iv, tag), StandardCharsets.UTF_8);
                    callback.onSuccess(plain);
                }

            } catch (Exception x) {
                callback.onCancel();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog1, which) ->{
            dialog1.cancel();
        });

        browseButton.setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            intent.putExtra(Intent.EXTRA_TITLE, "ch_backup_" + formatter.format(date) +".json");
            parent.startActivityForResult(intent, IMPORT_BROWSE_INTENT);
        });

        dialog = builder.create();
    }

    public void setUri(Uri uri){
        filepath = uri;
    }

    public Dialog getDialog(){
        return dialog;
    }


}
