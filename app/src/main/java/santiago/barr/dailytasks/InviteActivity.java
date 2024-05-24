package santiago.barr.dailytasks;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

    private EditText usernameEditText;
    private Button sendInviteButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        usernameEditText = findViewById(R.id.usernameEditText);
        sendInviteButton = findViewById(R.id.sendInviteButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        sendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvite();
            }
        });
    }

    private void sendInvite() {
        String username = usernameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Por favor, ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String inviteId = databaseReference.child("invitaciones").push().getKey();
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("username", username);
        inviteData.put("enviadoPor", currentUser.getUid());
        inviteData.put("estado", "pendiente");

        if (inviteId != null) {
            databaseReference.child("invitaciones").child(inviteId).setValue(inviteData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendNotification(username, inviteId);
                            Toast.makeText(this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error al enviar la invitación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendNotification(String username, String inviteId) {
        // Lógica para enviar una notificación en la aplicación
        // Puedes usar Firebase Cloud Messaging (FCM) para enviar notificaciones push
        System.out.println("Notificación enviada a: " + username);
        System.out.println("ID de Invitación: " + inviteId);
    }
}
