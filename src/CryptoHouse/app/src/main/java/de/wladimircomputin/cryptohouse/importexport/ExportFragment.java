package de.wladimircomputin.cryptohouse.importexport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.wladimircomputin.cryptohouse.MainActivity;
import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.profile.ProfileItem;
import de.wladimircomputin.libcryptoiot.v2.protocol.Crypter;

public class ExportFragment extends Fragment implements FocusListener {

    SharedPreferences sharedPrefs;
    List<ProfileItem> allProfiles;
    List<ProfileItem> profiles;

    Spinner exportProfileModeSpinner;
    EditText exportPasswordEditText;
    TextView exportStatusText;
    Button exportSaveAsButton;
    Button exportShareButton;

    ActivityResultLauncher<String> createDocumentLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.profiles", Context.MODE_PRIVATE);
        try {
            allProfiles = getProfiles(new JSONArray(sharedPrefs.getString("profiles", "[]")));
        } catch (Exception x){
            allProfiles = new ArrayList<>();
        }
        profiles = new ArrayList<>();

        createDocumentLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
            if (uri != null) {
                try {
                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                    String out = exportSettingsAsString(exportPasswordEditText.getText().toString());
                    outputStream.write(out.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                } catch (Exception x){}
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_export, container, false);
        exportProfileModeSpinner = view.findViewById(R.id.export_profile_mode_spinner);
        exportPasswordEditText = view.findViewById(R.id.export_password_edittext);
        exportStatusText = view.findViewById(R.id.export_status_text);
        exportSaveAsButton = view.findViewById(R.id.export_save_as_button);
        exportShareButton = view.findViewById(R.id.export_share_button);

        exportStatusText.setOnClickListener((v) -> {
            updateStatusText(2);
        });

        exportProfileModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateStatusText(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        exportShareButton.setOnClickListener((v) -> {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
                Date date = new Date();
                String filename = "cryptohouse_backup_" + formatter.format(date) + ".json";
                String out = exportSettingsAsString(exportPasswordEditText.getText().toString());
                File file = createTempFile(out, filename);
                shareFile(file);
            } catch (Exception x){}
        });

        exportSaveAsButton.setOnClickListener(v -> {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String filename = "cryptohouse_backup_" + formatter.format(date) + ".json";

            createDocumentLauncher.launch(filename);
        });

        return view;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnselected() {

    }

    private void updateStatusText(int position){
        if(exportProfileModeSpinner.getSelectedItemPosition() != position){
            exportProfileModeSpinner.setSelection(position);
            return;
        }

        if(position == 2){ //Custom profiles
            showMultiChoiceDialog(allProfiles, profiles, new MultipleSelectionDialogReceiver<ProfileItem>() {
                @Override
                public void onFinish(List<ProfileItem> selectedItems) {
                    profiles.clear();
                    profiles.addAll(selectedItems);
                    exportStatusText.setText(profilesToString(profiles));
                }

                @Override
                public void onAbort() {

                }
            });
        } else if (position == 1){ //Current profile
            profiles.clear();
            MainActivity mainActivity = (MainActivity) getActivity();
            profiles.add(mainActivity.getCurrentProfile());
            exportStatusText.setText(profilesToString(profiles));
        } else { //All profiles
            profiles.clear();
            profiles.addAll(allProfiles);
            exportStatusText.setText(profilesToString(profiles));
        }
    }

    private String exportSettingsAsString(String password) throws Exception{
        JSONObject exported_settings = exportSettings(profiles);
        String out;
        if (!password.isEmpty()) {
            out = encryptSettings(exported_settings, password).toString(4);
        } else {
            out = exported_settings.toString(4);
        }
        return out;
    }

    private void showMultiChoiceDialog(List<ProfileItem> profiles, List<ProfileItem> preselectedItems, MultipleSelectionDialogReceiver<ProfileItem> receiver) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Profiles to export");
        String[] items = new String[profiles.size()];
        List<ProfileItem> selectedItems = new ArrayList<>();
        for(int i = 0; i < profiles.size(); i++){
            items[i] = profiles.get(i).name;
        }
        boolean[] checkedItems = new boolean[profiles.size()];

        for(ProfileItem profileItem : preselectedItems) {
            checkedItems[profiles.indexOf(profileItem)] = true;
            selectedItems.add(profileItem);
        }


        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(profiles.get(indexSelected));
                } else {
                    selectedItems.remove(profiles.get(indexSelected));
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                receiver.onFinish(selectedItems);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                receiver.onAbort();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Function to write String to a file
    private File createTempFile(byte[] content, String fileName) {
        // Create a file in the cache directory
        File file = new File(getContext().getCacheDir(), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Function to write String to a file
    private File createTempFile(String content, String fileName) {
        return createTempFile(content.getBytes(StandardCharsets.UTF_8), fileName);
    }


    private void shareFile(File file) {
        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Grant permission to read the URI

        startActivity(Intent.createChooser(shareIntent, "Share File"));

        file.deleteOnExit();
    }

    private String profilesToString(List<ProfileItem> profiles){
        String profiles_selected = "";
        for(ProfileItem profileItem : profiles){
            profiles_selected += profileItem.name + ", ";
        }
        if(!profiles_selected.isEmpty()){
            profiles_selected = profiles_selected.substring(0, profiles_selected.length()-2);
        }
        return profiles_selected;
    }

    private List<ProfileItem> getProfiles(JSONArray arr){
        ArrayList<ProfileItem> profiles = new ArrayList<>();
        try {
            for(int i = 0; i < arr.length(); i++) {
                ProfileItem profileItem = new ProfileItem(arr.getJSONObject(i));
                profiles.add(profileItem);
            }

        }catch (Exception x){
            return null;
        }
        return profiles;
    }

    public JSONObject exportSettings(List<ProfileItem> profiles){
        JSONObject out = new JSONObject();
        try {
            out.put("encrypted", false);

            sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.main", Context.MODE_PRIVATE);
            JSONObject mainJsonObj = new JSONObject();
            mainJsonObj.put("version_code", sharedPrefs.getInt("version_code", 0));
            out.put("main", mainJsonObj);

            JSONArray profilesJsonArray = new JSONArray();
            for (ProfileItem profileItem : profiles){
                profilesJsonArray.put(profileItem.toJSON());
            }
            out.put("profiles", profilesJsonArray);

            sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.devices", Context.MODE_PRIVATE);
            JSONObject devicesJsonObj = new JSONObject();
            for(ProfileItem profileItem : profiles){
                String devicesJson = sharedPrefs.getString(profileItem.id, "{}");
                devicesJsonObj.put(profileItem.id, new JSONObject(devicesJson));
            }
            out.put("devices", devicesJsonObj);

            sharedPrefs = getContext().getSharedPreferences("de.wladimircomputin.cryptohouse.actions", Context.MODE_PRIVATE);

            JSONObject actionsJsonObj = new JSONObject();
            for(ProfileItem profileItem : profiles){
                String devicesJson = sharedPrefs.getString(profileItem.id, "{}");
                actionsJsonObj.put(profileItem.id, new JSONObject(devicesJson));
            }
            out.put("actions", actionsJsonObj);

        } catch (Exception x){

        }
        return out;
    }

    public JSONObject encryptSettings(JSONObject settings, String password) throws Exception{
        Crypter crypter = new Crypter(exportPasswordEditText.getText().toString());
        byte[] iv = crypter.getRandom(Crypter.AES_GCM_IV_LEN);
        byte[] encryptedMessageWithTag = crypter.encrypt(settings.toString().getBytes(StandardCharsets.UTF_8), iv);
        byte[] encrypted_message = new byte[encryptedMessageWithTag.length - Crypter.AES_GCM_TAG_LEN];
        byte[] tag = new byte[Crypter.AES_GCM_TAG_LEN];
        System.arraycopy(encryptedMessageWithTag, 0, encrypted_message, 0, encrypted_message.length);
        System.arraycopy(encryptedMessageWithTag, encrypted_message.length, tag, 0, tag.length);
        JSONObject out = new JSONObject();
        out.put("encrypted", true);
        out.put("iv", new String(Base64.encode(iv, Base64.NO_WRAP)));
        out.put("tag", new String(Base64.encode(tag, Base64.NO_WRAP)));
        out.put("data", new String(Base64.encode(encrypted_message, Base64.NO_WRAP)));

        return out;
    }
}
