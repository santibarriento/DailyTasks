package santiago.barr.dailytasks;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity {

    private EditText emailEditText, messageEditText;
    private Button sendInviteButton, backButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);
        sendInviteButton = findViewById(R.id.sendInviteButton);
        backButton = findViewById(R.id.back_button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        groupId = getIntent().getStringExtra("groupId");

        backButton.setOnClickListener(v -> finish());

        sendInviteButton.setOnClickListener(v -> sendInvite());
    }

    private void sendInvite() {
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Por favor, ingresa un email y un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String inviteId = databaseReference.child("invitaciones").push().getKey();
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("inviteId", inviteId);
        inviteData.put("email", email);
        inviteData.put("message", message);
        inviteData.put("groupId", groupId);
        inviteData.put("senderId", currentUser.getUid());
        inviteData.put("status", "pending");

        if (inviteId != null) {
            databaseReference.child("invitaciones").child(inviteId).setValue(inviteData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error al enviar la invitación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
