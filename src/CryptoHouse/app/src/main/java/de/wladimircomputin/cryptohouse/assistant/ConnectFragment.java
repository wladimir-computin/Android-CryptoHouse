package de.wladimircomputin.cryptohouse.assistant;

import static de.wladimircomputin.libcryptoiot.v2.Constants.wifipass_factory_default;
import static de.wladimircomputin.libcryptoiot.v2.Constants.wifissid_factory_default;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import de.wladimircomputin.cryptohouse.databinding.FragmentAssistantConnectBinding;

public class ConnectFragment extends Fragment implements FocusListener {

    FragmentAssistantConnectBinding binding;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    connect();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentAssistantConnectBinding.inflate(inflater, container, false);
        binding.assistantConnectButton.setOnClickListener((v) -> {
            connect();
        });

        return binding.getRoot();
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onUnselected() {
    }

    private void connect(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            binding.assistantConnectProgress.setVisibility(View.VISIBLE);
            WifiUtils.withContext(getContext()).connectWith(wifissid_factory_default, wifipass_factory_default)
                    .setTimeout(10000)
                    .onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
                            getActivity().runOnUiThread(() -> {
                                binding.assistantConnectProgress.setVisibility(View.GONE);
                                ((AssistantActivity)getActivity()).nextPage();
                            });
                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            binding.assistantConnectProgress.setVisibility(View.GONE);
                            Toast.makeText(getContext(), errorCode.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }).start();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}
