package santiago.barr.dailytasks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteDetailActivity extends AppCompatActivity {

    private TextView inviteMessageDetail;
    private Button acceptInviteButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String inviteId, groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);

        inviteMessageDetail = findViewById(R.id.inviteMessageDetail);
        acceptInviteButton = findViewById(R.id.acceptInviteButton);

        inviteId = getIntent().getStringExtra("inviteId");
        groupId = getIntent().getStringExtra("groupId");
        String mensaje = getIntent().getStringExtra("mensaje");

        inviteMessageDetail.setText(mensaje);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        acceptInviteButton.setOnClickListener(v -> acceptInvite());
    }

    private void acceptInvite() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("invitaciones").child(inviteId).child("estado").setValue("aceptada")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReference.child("grupos").child(groupId).child("miembros")
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
