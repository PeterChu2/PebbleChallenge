package me.chu.peter.pebblecolors.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import me.chu.peter.pebblecolors.R;
import me.chu.peter.pebblecolors.adapters.CommandsAdapter;
import me.chu.peter.pebblecolors.models.AbsoluteCommand;
import me.chu.peter.pebblecolors.models.Command;
import me.chu.peter.pebblecolors.models.RelativeCommand;
import me.chu.peter.pebblecolors.util.ProcessCommandsTask;

/**
 * Created by peter on 01/06/15.
 */
public class PebbleColorsActivity extends Activity implements Command.OnReceiveListener,
        ProcessCommandsTask.OnConnectListener {
    public static final int DEFAULT_PORT = 1234;
    public static final int DEFAULT_COLOR = Color.rgb(127, 127, 127);
    private CommandsAdapter mCommandsAdapter;
    private ProcessCommandsTask mProcessCommandsTask;
    private TextView mColorValue;
    private View mColorDisplay;
    private int mCurrentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get IP Address from the extra inserted from the SplashActivity
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra(SplashActivity.IP_ADDRESS_KEY);

        TextView iPAddressTextView = (TextView) findViewById(R.id.tv_ip_address);
        iPAddressTextView.setText(ipAddress);

        ListView commandsListView = (ListView) findViewById(R.id.commands_list_view);
        commandsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Command command = mCommandsAdapter.getItem(position);
                if (command.getType() == Command.Type.RELATIVE) {
                    if (command.isSelected()) {
                        mCurrentColor = ((RelativeCommand) command).revert(mCurrentColor);
                    } else {
                        mCurrentColor = ((RelativeCommand) command).apply(mCurrentColor);
                    }
                } else if (!command.isSelected() && command.getType() == Command.Type.ABSOLUTE) {
                    mCurrentColor = ((AbsoluteCommand) command).getColor();
                }
                mCommandsAdapter.toggle(position);
                mCommandsAdapter.notifyDataSetChanged();
                updateUI();
            }
        });

        mColorValue = (TextView) findViewById(R.id.rgb_value);
        mColorDisplay = findViewById(R.id.color_display);

        TextView portTextView = (TextView) findViewById(R.id.tv_port);
        portTextView.setText(String.valueOf(DEFAULT_PORT));

        ArrayList<Command> commandsList = new ArrayList<Command>();
        mCommandsAdapter = new CommandsAdapter(this,
                R.layout.command_list_item, commandsList);
        commandsListView.setAdapter(mCommandsAdapter);

        // start the asynctask to process commands
        mProcessCommandsTask = new ProcessCommandsTask(this, ipAddress, DEFAULT_PORT);
        mProcessCommandsTask.execute();
    }

    @Override
    protected void onDestroy() {
        mProcessCommandsTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onNewCommand(Command command) {
        // always insert new commands at the top of the list
        mCommandsAdapter.insert(command, 0);
        if (command.getType() == Command.Type.RELATIVE) {
            mCurrentColor = ((RelativeCommand) command).apply(mCurrentColor);
        } else {
            mCurrentColor = ((AbsoluteCommand) command).getColor();
        }
        updateUI();
    }

    @Override
    public void onConnect() {
        // set the color to the default color when the client connects
        mCurrentColor = DEFAULT_COLOR;
        updateUI();
    }

    // refreshes ui components with new color data
    private void updateUI() {
        mColorValue.setText(formatColor(mCurrentColor));
        mColorDisplay.setBackgroundColor(mCurrentColor);
    }

    private static String formatColor(int rgb) {
        return String.format("Red: %d, Green: %d, Blue: %d",
                Color.red(rgb), Color.green(rgb), Color.blue(rgb));
    }
}
