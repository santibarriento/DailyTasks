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

    private EditText emailEditText;
    private Button sendInviteButton, backButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        emailEditText = findViewById(R.id.emailEditText);
        sendInviteButton = findViewById(R.id.sendInviteButton);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        sendInviteButton.setOnClickListener(v -> sendInvite());
    }

    private void sendInvite() {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String inviteId = databaseReference.child("invitaciones").push().getKey();
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("email", email);
        inviteData.put("enviadoPor", currentUser.getUid());
        inviteData.put("estado", "pendiente");

        if (inviteId != null) {
            databaseReference.child("invitaciones").child(inviteId).setValue(inviteData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendEmailNotification(email, inviteId);
                            Toast.makeText(this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error al enviar la invitación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendEmailNotification(String email, String inviteId) {
        // Aquí debes implementar la lógica para enviar el correo electrónico
        // Puedes usar un servicio como SendGrid, Amazon SES, etc.
        // Ejemplo:
        // EmailService.send(email, "Invitación a Daily Tasks", "Has sido invitado a unirte a un grupo en Daily Tasks. Código de invitación: " + inviteId);

        System.out.println("Correo enviado a: " + email);
        System.out.println("ID de Invitación: " + inviteId);
    }
}
