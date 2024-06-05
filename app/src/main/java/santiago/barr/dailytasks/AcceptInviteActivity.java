package santiago.barr.dailytasks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AcceptInviteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewInvites;
    private InviteAdapter inviteAdapter;
    private List<Invite> inviteList;
    private DatabaseReference db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_invite);

        recyclerViewInvites = findViewById(R.id.recyclerViewInvites);
        recyclerViewInvites.setLayoutManager(new LinearLayoutManager(this));
        inviteList = new ArrayList<>();
        inviteAdapter = new InviteAdapter(inviteList, this);
        recyclerViewInvites.setAdapter(inviteAdapter);

        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        loadInvites();

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadInvites() {
        db.child("invitaciones").orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        inviteList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Invite invite = snapshot.getValue(Invite.class);
                            if (invite != null) {
                                inviteList.add(invite);
                            }
                        }
                        inviteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AcceptInviteActivity.this, "Error al cargar invitaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
