package santiago.barr.dailytasks;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewGroupsActivity extends AppCompatActivity {

    private TextView noGroupsMessage;
    private ListView groupsListView;
    private FirebaseAuth mAuth;
    private DatabaseReference userGroupsReference, groupsReference;
    private ArrayList<Group> groupsList;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        noGroupsMessage = findViewById(R.id.no_groups_message);
        groupsListView = findViewById(R.id.groups_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userGroupsReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("usuarios").child(currentUser.getUid()).child("grupos");
        groupsReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("grupos");

        groupsList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupsList);
        groupsListView.setAdapter(groupAdapter);

        loadUserGroups();
    }

    private void loadUserGroups() {
        userGroupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupsList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        String groupId = groupSnapshot.getValue(String.class);
                        if (groupId != null) {
                            loadGroupDetails(groupId);
                        }
                    }
                } else {
                    noGroupsMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewGroupsActivity.this, "Error al cargar los grupos: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadGroupDetails(String groupId) {
        groupsReference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot groupSnapshot) {
                Group group = groupSnapshot.getValue(Group.class);
                if (group != null) {
                    group.setId(groupId);
                    groupsList.add(group);
                    groupAdapter.notifyDataSetChanged();
                    noGroupsMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewGroupsActivity.this, "Error al cargar los detalles del grupo: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
