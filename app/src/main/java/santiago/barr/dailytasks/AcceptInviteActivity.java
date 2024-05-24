package santiago.barr.dailytasks;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptInviteActivity extends AppCompatActivity {

    private Button acceptInviteButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_invite);

        acceptInviteButton = findViewById(R.id.acceptInviteButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        String inviteId = getIntent().getStringExtra("inviteId");

        acceptInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptInvite(inviteId);
            }
        });
    }

    private void acceptInvite(String inviteId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("invitaciones").child(inviteId).child("estado").setValue("aceptada")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReference.child("grupos").child(inviteId).child("miembros")
                                .child(currentUser.getUid()).setValue(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "Invitación aceptada", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Error al aceptar la invitación", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Error al aceptar la invitación", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
