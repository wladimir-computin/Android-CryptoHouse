package de.wladimircomputin.cryptohouse.devicesettings;

import static de.wladimircomputin.libcryptoiot.v2.Constants.command_reboot;
import static de.wladimircomputin.libcryptoiot.v2.Constants.command_reset;
import static de.wladimircomputin.libcryptoiot.v2.Constants.command_update;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.assistant.FocusListener;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;
import de.wladimircomputin.cryptohouse.devicesettings.Terminal.TerminalActivity_V1;
import de.wladimircomputin.cryptohouse.ui.PagerAdapterTitleProvider;
import de.wladimircomputin.libcryptoiot.v2.protocol.Content;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptCon;
import de.wladimircomputin.libcryptoiot.v2.protocol.CryptConReceiver;

public class DeviceSettingsActivity extends AppCompatActivity {
    public SharedPreferences sharedPref;
    public DeviceManagerDevice device;
    public CryptCon cc;
    private DeviceSettingsPagerAdapter deviceSettingsPagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.device_settings_pager);
        tabLayout = findViewById(R.id.device_settings_tab_layout);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String ip = intent.getStringExtra("ip");
        String pass = intent.getStringExtra("pass");
        String type = intent.getStringExtra("type");
        device = new DeviceManagerDevice(id, name, type, name, ip, "", pass, true, "");

        setTitle(device.name + " " + getString(R.string.settings));
        this.sharedPref = getSharedPreferences("de.wladimircomputin.cryptohouse.device." + device.id, Context.MODE_PRIVATE);
        this.cc = new CryptCon(pass, device.ip);

        // Create adapter for ViewPager2
        deviceSettingsPagerAdapter = new DeviceSettingsPagerAdapter(this);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(deviceSettingsPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(((PagerAdapterTitleProvider) deviceSettingsPagerAdapter).getTitle(position));
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((FocusListener)deviceSettingsPagerAdapter.createFragment(position)).onSelected();
                for(int page = 0; page < deviceSettingsPagerAdapter.getItemCount(); page++){
                    if(page != position) {
                        ((FocusListener) deviceSettingsPagerAdapter.createFragment(position)).onUnselected();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_terminal_v1:
                terminal_v1();
                break;

            case R.id.menu_reboot:
                reboot();
                break;

            case R.id.menu_update:
                update_mode();
                break;

            case R.id.menu_reset:
                reset();
                break;

            case R.id.menu_apply:
                return false;

            case R.id.menu_add:
                return false;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void reset(){
        cc.sendMessageEncrypted(command_reset, CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {
                //update();
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onProgress(String sprogress, int iprogress) {

            }
        });
    }

    private void update_mode(){
        cc.sendMessageEncrypted(command_update, CryptCon.Mode.UDP, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {
                runOnUiThread(() -> {
                    Toast.makeText(DeviceSettingsActivity.this, getString(R.string.update_server_enabled) + "\n" + response.data, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onProgress(String sprogress, int iprogress) {

            }
        });
    }

    public void reboot(){
        cc.sendMessageEncrypted(command_reboot, CryptCon.Mode.UDP, 1, new CryptConReceiver() {
            @Override
            public void onSuccess(Content response) {}

            @Override
            public void onFail() {}

            @Override
            public void onFinished() {
                runOnUiThread(() -> {
                    Toast.makeText(DeviceSettingsActivity.this, getString(R.string.rebooting), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onProgress(String sprogress, int iprogress) {}
        });
    }

    public void terminal_v1(){
        Intent intent = new Intent(this, TerminalActivity_V1.class);
        intent.putExtra("id", device.id);
        intent.putExtra("name", device.name);
        intent.putExtra("ip", device.ip);
        intent.putExtra("pass", device.pass);
        intent.putExtra("type", device.type);
        this.startActivity(intent);
    }
}