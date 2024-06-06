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

/**
 * Actividad que muestra los detalles de una invitación y permite al usuario aceptarla.
 */
public class InviteDetailActivity extends AppCompatActivity {

    private TextView inviteMessageDetail; // Texto para mostrar el mensaje de invitación
    private Button acceptInviteButton; // Botón para aceptar la invitación
    private DatabaseReference databaseReference; // Referencia a la base de datos de Firebase
    private FirebaseAuth mAuth; // Autenticación de Firebase
    private String inviteId, groupId; // IDs de la invitación y del grupo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);

        inviteMessageDetail = findViewById(R.id.inviteMessageDetail); // Inicializar el TextView del mensaje de invitación
        acceptInviteButton = findViewById(R.id.acceptInviteButton); // Inicializar el botón de aceptar invitación

        // Obtener los extras del Intent
        inviteId = getIntent().getStringExtra("inviteId");
        groupId = getIntent().getStringExtra("groupId");
        String mensaje = getIntent().getStringExtra("mensaje");

        inviteMessageDetail.setText(mensaje); // Establecer el mensaje de la invitación

        mAuth = FirebaseAuth.getInstance(); // Inicializar la autenticación de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference(); // Obtener referencia a la base de datos

        // Configurar el botón de aceptar invitación
        acceptInviteButton.setOnClickListener(v -> acceptInvite());
    }

    /**
     * Método para aceptar la invitación.
     */
    private void acceptInvite() {
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Obtener el usuario actual
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el estado de la invitación a "aceptada"
        databaseReference.child("invitaciones").child(inviteId).child("estado").setValue("aceptada")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Añadir el usuario al grupo en la base de datos
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
