package santiago.barr.dailytasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * Actividad principal de la aplicación, maneja el inicio de sesión con Google.
 */
public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Código de solicitud para el inicio de sesión
    private static final String TAG = "MainActivity"; // Tag para logs
    private FirebaseAuth mAuth; // Autenticación de Firebase
    private GoogleSignInClient mGoogleSignInClient; // Cliente de inicio de sesión de Google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Inicializar Firebase
        setContentView(R.layout.activity_login); // Establecer el contenido de la vista

        mAuth = FirebaseAuth.getInstance(); // Obtener instancia de FirebaseAuth

        // Configuración de opciones de inicio de sesión de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); // Inicializar cliente de inicio de sesión de Google

        // Configurar el botón de inicio de sesión
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(); // Iniciar el proceso de inicio de sesión
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya ha iniciado sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser); // Actualizar la interfaz de usuario si el usuario está autenticado
        }
    }

    /**
     * Inicia el flujo de inicio de sesión con Google.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Manejar el resultado del intento de inicio de sesión
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); // Autenticar con Firebase utilizando la cuenta de Google
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null); // Actualizar la interfaz de usuario en caso de fallo
            }
        }
    }

    /**
     * Autenticar con Firebase utilizando la cuenta de Google.
     * @param acct La cuenta de Google.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserData(user); // Guardar datos del usuario en Firestore
                            updateUI(user); // Actualizar la interfaz de usuario
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null); // Actualizar la interfaz de usuario en caso de fallo
                        }
                    }
                });
    }

    /**
     * Guardar datos del usuario en Firebase Firestore.
     * @param user El usuario autenticado.
     */
    private void saveUserData(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("fotoURL", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
        userData.put("telefono", user.getPhoneNumber());
        userData.put("preferencias", new HashMap<String, Object>() {{
            put("notificaciones", true);
            put("temaOscuro", false);
        }});
        userData.put("puntos", 0);

        db.collection("usuarios").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Datos guardados correctamente
                })
                .addOnFailureListener(e -> {
                    // Error al guardar datos
                });
    }

    /**
     * Actualiza la interfaz de usuario en función del estado de autenticación del usuario.
     * @param user El usuario autenticado.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish(); // Finalizar la actividad actual
        } else {
            // Manejar el caso en que el usuario no esté autenticado
        }
    }
}
