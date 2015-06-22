package me.chu.peter.pebblecolors.models;

import android.graphics.Color;

/**
 * A {@link Command} to change the current color by an offset provided.
 * The RGB values for the offset must be within -127 to 127
 */
public class RelativeCommand extends Command {
    public RelativeCommand(byte red, byte green, byte blue) {
        // leave the bytes signed because these values range from -127 to 127
        super(red, green, blue, Type.RELATIVE);
    }

    /**
     * @param color The current color
     * @return The current color offset by the amount specified by the {@link RelativeCommand}
     */
    public int apply(int color) {
        return Color.rgb(Color.red(color) + mRed,
                Color.green(color) + mGreen, Color.blue(color) + mBlue);
    }

    /**
     * @param color The current color
     * @return Returns the reverted color if the command was not applied
     */
    public int revert(int color) {
        return Color.rgb(Color.red(color) - mRed,
                Color.green(color) - mGreen, Color.blue(color) - mBlue);
    }
}
