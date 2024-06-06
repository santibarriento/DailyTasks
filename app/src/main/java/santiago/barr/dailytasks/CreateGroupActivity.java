package santiago.barr.dailytasks;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa la actividad de crear un grupo en la aplicación.
 */
public class CreateGroupActivity extends AppCompatActivity {

    // Elementos de la interfaz de usuario
    private EditText groupNameEditText, groupDescriptionEditText;
    private Button createGroupButton, backButton;

    // Referencia a la base de datos de Firebase y a la autenticación
    private DatabaseReference db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Inicializar elementos de la interfaz de usuario
        groupNameEditText = findViewById(R.id.group_name);
        groupDescriptionEditText = findViewById(R.id.group_description);
        createGroupButton = findViewById(R.id.create_group_button);
        backButton = findViewById(R.id.back_button);

        // Configurar el botón de retroceso
        backButton.setOnClickListener(v -> finish());

        // Inicializar Firebase Database y Firebase Auth
        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth = FirebaseAuth.getInstance();

        // Configurar el botón de creación de grupo
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
    }

    /**
     * Método para crear un grupo nuevo.
     */
    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String groupDescription = groupDescriptionEditText.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        // Validar que los campos no estén vacíos
        if (groupName.isEmpty() || groupDescription.isEmpty()) {
            Toast.makeText(CreateGroupActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si el usuario está autenticado
        if (user != null) {
            String groupId = db.child("grupos").push().getKey();
            Map<String, Object> groupData = new HashMap<>();
            groupData.put("nombre", groupName);
            groupData.put("descripcion", groupDescription);
            groupData.put("admin", user.getUid());

            // Guardar los datos del grupo en Firebase
            db.child("grupos").child(groupId).setValue(groupData).addOnSuccessListener(aVoid -> {
                // Añadir el grupo al usuario actual
                db.child("usuarios").child(user.getUid()).child("grupos").child(groupId).setValue(groupId).addOnSuccessListener(aVoid1 -> {
                    Toast.makeText(CreateGroupActivity.this, "Grupo creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(CreateGroupActivity.this, "Error al añadir el grupo al usuario", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(CreateGroupActivity.this, "Error al crear el grupo", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
