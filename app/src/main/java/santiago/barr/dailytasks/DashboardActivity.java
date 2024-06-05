package santiago.barr.dailytasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class DashboardActivity extends AppCompatActivity {

    private TextView noGroupsMessage;
    private ListView groupsListView;
    private Button createGroupButton, viewGroupsButton, invitationsButton;
    private ImageButton userProfileButton; // Agregar referencia para el botón de perfil de usuario
    private FirebaseAuth mAuth;
    private DatabaseReference userGroupsReference, groupsReference;
    private ArrayList<Group> groupsList;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        noGroupsMessage = findViewById(R.id.no_groups_message);
        groupsListView = findViewById(R.id.groups_list);
        createGroupButton = findViewById(R.id.create_group_button);
        viewGroupsButton = findViewById(R.id.view_groups_button);
        invitationsButton = findViewById(R.id.invitations_button);
        userProfileButton = findViewById(R.id.user_profile_button); // Inicializar el botón de perfil de usuario

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userGroupsReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("usuarios").child(currentUser.getUid()).child("grupos");
        groupsReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("grupos");

        groupsList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupsList);
        groupsListView.setAdapter(groupAdapter);

        loadUserGroups();

        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        viewGroupsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ViewGroupsActivity.class);
            startActivity(intent);
        });

        invitationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AcceptInviteActivity.class);
            startActivity(intent);
        });

        userProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
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
                Toast.makeText(DashboardActivity.this, "Error al cargar los grupos: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(DashboardActivity.this, "Error al cargar los detalles del grupo: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
