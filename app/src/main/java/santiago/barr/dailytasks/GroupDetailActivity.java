package santiago.barr.dailytasks;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupDetailActivity extends AppCompatActivity {

    private TextView groupNameDetail, groupDescriptionDetail;
    private Button addMemberButton;
    private DatabaseReference db;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupNameDetail = findViewById(R.id.group_name_detail);
        groupDescriptionDetail = findViewById(R.id.group_description_detail);
        addMemberButton = findViewById(R.id.add_member_button);

        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        groupId = getIntent().getStringExtra("groupId");

        loadGroupDetails(groupId);

        addMemberButton.setOnClickListener(v -> {
            Toast.makeText(GroupDetailActivity.this, "falta añadir miembros", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadGroupDetails(String groupId) {
        db.child("grupos").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group != null) {
                        groupNameDetail.setText(group.getNombre());
                        groupDescriptionDetail.setText(group.getDescripcion());
                    }
                } else {
                    Toast.makeText(GroupDetailActivity.this, "Detalles del grupo no encontrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailActivity.this, "Error al cargar detalles del grupo: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
