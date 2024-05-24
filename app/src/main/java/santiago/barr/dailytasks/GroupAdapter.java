package santiago.barr.dailytasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter<Group> {
    private Context context;
    private ArrayList<Group> groupList;

    public GroupAdapter(@NonNull Context context, ArrayList<Group> groupList) {
        super(context, 0, groupList);
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        }

        Group group = groupList.get(position);

        TextView groupNameTextView = convertView.findViewById(R.id.group_name);
        TextView groupDescriptionTextView = convertView.findViewById(R.id.group_description);

        groupNameTextView.setText(group.getNombre());
        groupDescriptionTextView.setText(group.getDescripcion());

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupDetailActivity.class);
            intent.putExtra("groupId", group.getId());
            context.startActivity(intent);
        });

        return convertView;
    }
}
