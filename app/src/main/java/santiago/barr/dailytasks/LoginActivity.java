package santiago.barr.dailytasks;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Actividad para gestionar el inicio de sesión con Google utilizando One Tap.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQ_ONE_TAP = 2;  // Código de solicitud único para One Tap
    private SignInClient oneTapClient;  // Cliente de One Tap para gestionar el inicio de sesión
    private FirebaseAuth mAuth;  // Autenticación de Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();  // Inicializar Firebase Auth
        oneTapClient = Identity.getSignInClient(this);  // Inicializar el cliente de One Tap
    }

    /**
     * Método llamado cuando se hace clic en el botón de inicio de sesión.
     * @param view La vista que fue clicada.
     */
    public void ABRIMEELGOOGLE(View view) {
        Log.d(TAG, "Sign in button clicked");
        prepareOneTapSignIn();
    }

    /**
     * Prepara la solicitud de inicio de sesión con One Tap.
     */
    private void prepareOneTapSignIn() {
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)  // Permitir crear cuenta si no existe
                        .build())
                .build();

        displayOneTapUI(signInRequest);
    }

    /**
     * Muestra la interfaz de usuario de One Tap.
     * @param request La solicitud de inicio de sesión.
     */
    private void displayOneTapUI(BeginSignInRequest request) {
        oneTapClient.beginSignIn(request)
                .addOnSuccessListener(this, result -> {
                    try {
                        Log.d(TAG, "Starting One Tap UI.");
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(),
                                REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> Log.e(TAG, "One Tap UI failed: " + e.getLocalizedMessage()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            handleSignInResult(data);
        }
    }

    /**
     * Maneja el resultado del intento de inicio de sesión.
     * @param data Los datos del intento.
     */
    private void handleSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken);
            } else {
                Log.e(TAG, "No ID token received");
            }
        } catch (ApiException e) {
            Log.e(TAG, "Sign in result failed", e);
        }
    }

    /**
     * Autentica el usuario con Firebase utilizando el ID token de Google.
     * @param idToken El ID token de Google.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    /**
     * Actualiza la interfaz de usuario en función del estado de autenticación del usuario.
     * @param user El usuario autenticado.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "User is signed in");
            Toast.makeText(this, "User is signed in", Toast.LENGTH_SHORT).show();
            // Navega a DashboardActivity
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "Authentication Failed.");
            Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
