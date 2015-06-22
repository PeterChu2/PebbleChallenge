package me.chu.peter.pebblecolors.models;

import android.graphics.Color;

/**
 * Created by peter on 01/06/15.
 */
public abstract class Command {
    public enum Type {
        ABSOLUTE, RELATIVE
    }

    /**
     * Listener for the {@link me.chu.peter.pebblecolors.util.ProcessCommandsTask}
     * to notify the UI Thread to update UI Elements
     */
    public interface OnReceiveListener {
        void onNewCommand(Command command);
    }

    private Type mType;
    protected int mRed;
    protected int mGreen;
    protected int mBlue;
    private boolean mIsSelected;

    public Command(int red, int green, int blue, Type type) {
        mRed = red;
        mGreen = green;
        mBlue = blue;
        mType = type;
        mIsSelected = false;
    }

    public Type getType() {
        return mType;
    }

    public int getRed() {
        return mRed;
    }

    public int getGreen() {
        return mGreen;
    }

    public int getBlue() {
        return mBlue;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isSelected() {
        return mIsSelected;
    }
}
