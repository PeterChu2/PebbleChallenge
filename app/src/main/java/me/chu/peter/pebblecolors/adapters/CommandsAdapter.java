package me.chu.peter.pebblecolors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.chu.peter.pebblecolors.R;
import me.chu.peter.pebblecolors.models.Command;

/**
 * Created by peter on 01/06/15.
 */
public class CommandsAdapter extends ArrayAdapter<Command>{
    private LayoutInflater mLayoutInflater;
    private List<Command> mCommandsList;
    private List<Command> mSelectedCommands;
    private Command mCurrentAbsoluteCommand;

    public CommandsAdapter(Context context, int resource, List<Command> commandsList) {
        super(context, resource, commandsList);
        mCommandsList = commandsList;
        mSelectedCommands = new ArrayList<Command>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null)
        {
            row = mLayoutInflater.inflate(R.layout.command_list_item, parent, false);
        }
        TextView commandTypeTextView = (TextView) row.findViewById(R.id.tv_command_type);
        TextView commandTextView = (TextView) row.findViewById(R.id.tv_command);

        Command currentCommand = mCommandsList.get(position);
        commandTypeTextView.setText(currentCommand.getType().name());
        commandTextView.setText(String.format("R: %d, G: %d, B: %d",
                currentCommand.getRed(), currentCommand.getGreen(),
                currentCommand.getBlue()));

        // change color of list item depending on if it is selected or not
        if(mCurrentAbsoluteCommand == currentCommand) {
            row.setBackgroundColor(getContext().getResources().getColor(R.color.current_absolute));
        }
        else if(currentCommand.isSelected()) {
            row.setBackgroundColor(getContext().getResources().getColor(R.color.selected));
        }
        else {
            row.setBackgroundColor(getContext().getResources().getColor(R.color.unselected));
        }
        return row;
    }

    @Override
    public void insert(Command command, int index) {
        super.insert(command, index);
        if(command.getType() == Command.Type.ABSOLUTE) {
            // new Absolute command
            mCurrentAbsoluteCommand = command;
            // deselect all currently selected commands
            deselectAll();
        }
        command.setSelected(true);
        mSelectedCommands.add(command);
    }

    /**
     * Toggles the {@link Command} color in the ListView.
     * An {@link me.chu.peter.pebblecolors.models.AbsoluteCommand} cannot be toggled.
     * @param position The position of the {@link Command} in the list.
     */
    public void toggle(int position) {
        Command command  = getItem(position);
        if(command.isSelected()) {
            if(command.getType() == Command.Type.ABSOLUTE) {
                // do not allow users to deselect absolute commands - there should always be one
                return;
            }
            mSelectedCommands.remove(command);
        }
        else {
            if(command.getType() == Command.Type.ABSOLUTE) {
                // Deselect all selected commands because an absolute command was deselected
                deselectAll();
                mCurrentAbsoluteCommand = command;
            }
            mSelectedCommands.add(command);
        }
        // toggle selection
        command.setSelected(!command.isSelected());
    }

    /**
     * Deselects all selected commands so that they appear unselected in the list
     */
    private void deselectAll() {
        for(Command c : mSelectedCommands) {
            c.setSelected(false);
        }
        mSelectedCommands.clear();
    }
}
