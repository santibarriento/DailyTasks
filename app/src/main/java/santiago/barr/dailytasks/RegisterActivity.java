package santiago.barr.dailytasks;

import android.os.Bundle;
import android.text.TextUtils;
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
 * Actividad para el registro de nuevos usuarios en la aplicación.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton, backButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializa los campos de texto y botones
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.back_button);

        // Inicializa Firebase Auth y DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Configura el botón de regreso
        backButton.setOnClickListener(v -> finish());

        // Configura el botón de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    /**
     * Método para registrar un nuevo usuario.
     */
    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Verifica que los campos no estén vacíos
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Por favor, ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, ingresa una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea un nuevo usuario con correo electrónico y contraseña
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("email", email);

                    // Guarda la información del usuario en la base de datos
                    databaseReference.child("usuarios").child(userId).setValue(userData)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
