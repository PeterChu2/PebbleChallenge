package me.chu.peter.pebblecolors.models;

import android.graphics.Color;

/**
 * A {@link Command} that sets the current color to the absolute RGB value provided.
 * All RGB values can be from 0-255.
 */
public class AbsoluteCommand extends Command {
    public AbsoluteCommand(byte red, byte green, byte blue) {
        super(getUnsignedValue(red), getUnsignedValue(green), getUnsignedValue(blue), Type.ABSOLUTE);
    }

    public int getColor() {
        return Color.rgb(mRed, mGreen, mBlue);
    }

    private static int getUnsignedValue(byte signedByte) {
        // must AND with 0xFF as a mask to get the unsigned value of the byte
        return signedByte & 0xFF;
    }
}
