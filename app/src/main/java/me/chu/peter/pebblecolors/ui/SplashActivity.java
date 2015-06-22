package me.chu.peter.pebblecolors.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import me.chu.peter.pebblecolors.R;

/**
 * Created by peter on 01/06/15.
 */
public class SplashActivity extends Activity {
    public static final String IP_ADDRESS_KEY = "me.chu.peter.pebblecolors.ip_address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        final EditText ipAddressEditText = (EditText) findViewById(R.id.et_ip_address);
        Button startButton = (Button) findViewById(R.id.button_start);
        final Activity activity = this;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = ipAddressEditText.getText().toString();
                Intent intent = new Intent(activity, PebbleColorsActivity.class);
                intent.putExtra(IP_ADDRESS_KEY, ipAddress);
                startActivity(intent);
            }
        });
    }
}
