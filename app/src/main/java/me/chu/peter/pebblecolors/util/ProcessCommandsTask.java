package me.chu.peter.pebblecolors.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import me.chu.peter.pebblecolors.models.AbsoluteCommand;
import me.chu.peter.pebblecolors.models.Command;
import me.chu.peter.pebblecolors.models.RelativeCommand;

/**
 * AsyncTask to process all {@link Command}s created from python server
 */
public class ProcessCommandsTask extends AsyncTask<Void, Command, Void> {
    // each command has a max length of only 7 bytes - which is the size of a relative command
    // absolute commands only have a length of 4 bytes
    private static final int MAX_BYTES = 7;
    private String mIpAddress;
    private int mPort;
    private Context mContext;
    private Command.OnReceiveListener mOnReceiveListener;
    private OnConnectListener mOnConnectListener;

    /**
     * Listener to be called when a connection is made
     */
    public interface OnConnectListener {
        void onConnect();
    }

    public ProcessCommandsTask(Context context, String ipAddress, int port) {
        mContext = context;
        mIpAddress = ipAddress;
        mPort = port;
        mOnReceiveListener = (Command.OnReceiveListener) context;
        mOnConnectListener = (OnConnectListener) context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Socket socket = new Socket(mIpAddress, mPort);
            InputStream inputStream = socket.getInputStream();
            // notify the UI Thread of connection
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOnConnectListener.onConnect();
                }
            });

            // each command has a max length of only 7 bytes - which is the size of a relative command
            // absolute commands only have a length of 4 bytes
            byte[] commandBuffer = new byte[MAX_BYTES];
            Command.Type commandType = null;

            while ((inputStream.read(commandBuffer) != -1) && !isCancelled()){
                if (commandType == null) {
                    // first byte determines type of command
                    if (commandBuffer[0] == 0x01) {
                        commandType = Command.Type.RELATIVE;
                    } else if (commandBuffer[0] == 0x02) {
                        commandType = Command.Type.ABSOLUTE;
                    }
                }
                if (commandType == Command.Type.ABSOLUTE) {
                    // full absolute command received
                    AbsoluteCommand absoluteCommand = new AbsoluteCommand(commandBuffer[1],
                            commandBuffer[2], commandBuffer[3]);
                    publishProgress(absoluteCommand);
                    commandType = null;
                } else if (commandType == Command.Type.RELATIVE) {
                    // full relative command received - use only the least significant byte of the 16bits
                    // allocated for storing 8-bit color data
                    RelativeCommand relativeCommand = new RelativeCommand(commandBuffer[2], commandBuffer[4], commandBuffer[6]);
                    publishProgress(relativeCommand);
                    commandType = null;
                }
            }
            socket.close();
        } catch (UnknownHostException e)
        {
            Toast.makeText(mContext, "Unknown Host!", Toast.LENGTH_LONG).show();
        } catch (IOException e)
        {
            // NOOP
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Command... values) {
        mOnReceiveListener.onNewCommand(values[0]);
        super.onProgressUpdate(values);
    }
}
