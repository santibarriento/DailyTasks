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

/**
 * Actividad para enviar una invitación a un usuario.
 */
public class InviteActivity extends AppCompatActivity {

    private EditText emailEditText, messageEditText; // Campos para ingresar el correo y mensaje de la invitación
    private Button sendInviteButton, backButton; // Botones para enviar la invitación y regresar
    private FirebaseAuth mAuth; // Autenticación de Firebase
    private DatabaseReference databaseReference; // Referencia a la base de datos de Firebase
    private String groupId; // ID del grupo al que se enviará la invitación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        // Inicializar vistas
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);
        sendInviteButton = findViewById(R.id.sendInviteButton);
        backButton = findViewById(R.id.back_button);

        // Inicializar FirebaseAuth y DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        // Obtener el ID del grupo desde el Intent
        groupId = getIntent().getStringExtra("groupId");

        // Configurar el botón de retroceso
        backButton.setOnClickListener(v -> finish());

        // Configurar el botón para enviar la invitación
        sendInviteButton.setOnClickListener(v -> sendInvite());
    }

    /**
     * Envía una invitación al correo electrónico proporcionado.
     */
    private void sendInvite() {
        String email = emailEditText.getText().toString().trim(); // Obtener el correo electrónico
        String message = messageEditText.getText().toString().trim(); // Obtener el mensaje

        // Verificar que el correo y el mensaje no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Por favor, ingresa un email y un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Verificar que el usuario esté autenticado
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un ID único para la invitación y preparar los datos de la invitación
        String inviteId = databaseReference.child("invitaciones").push().getKey();
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("inviteId", inviteId);
        inviteData.put("email", email);
        inviteData.put("message", message);
        inviteData.put("groupId", groupId);
        inviteData.put("senderId", currentUser.getUid());
        inviteData.put("status", "pending");

        // Verificar que el ID de la invitación no sea nulo y guardar la invitación en la base de datos
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
