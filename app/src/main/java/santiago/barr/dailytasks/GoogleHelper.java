package santiago.barr.dailytasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Clase de ayuda para manejar la autenticación de Google Sign-In en la aplicación.
 */
public class GoogleHelper {
    private GoogleSignInClient mGoogleSignInClient;
    private Activity mActivity;
    private GoogleSignInResultCallback mCallback;

    /**
     * Interfaz para manejar los resultados del inicio de sesión de Google.
     */
    public interface GoogleSignInResultCallback {
        void onSignInSuccess();
        void onSignInFailure();
        void onSignOutComplete();
    }

    /**
     * Constructor para inicializar la configuración de Google Sign-In.
     *
     * @param activity La actividad actual.
     * @param callback El callback para manejar los resultados de inicio de sesión.
     */
    public GoogleHelper(Activity activity, GoogleSignInResultCallback callback) {
        this.mActivity = activity;
        this.mCallback = callback;

        // Configuración de las opciones de Google Sign-In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Inicialización del cliente de Google Sign-In.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    /**
     * Obtiene la cuenta de Google iniciada a partir del Intent.
     *
     * @param data Los datos del Intent.
     * @return Una tarea que representa la cuenta de Google.
     */
    public Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent data) {
        return GoogleSignIn.getSignedInAccountFromIntent(data);
    }

    /**
     * Obtiene el Intent para iniciar el flujo de inicio de sesión de Google.
     *
     * @return El Intent para Google Sign-In.
     */
    public Intent getSignInIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    /**
     * Maneja el resultado del intento de inicio de sesión de Google.
     *
     * @param completedTask La tarea completada del intento de inicio de sesión.
     */
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.e("Google Sign-In", "Error al iniciar sesión con Google: ", e);
            if (mCallback != null) {
                mCallback.onSignInFailure();
            }
        }
    }

    /**
     * Autentica el usuario en Firebase usando las credenciales de Google.
     *
     * @param acct La cuenta de Google.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mCallback != null) {
                                mCallback.onSignInSuccess();
                            }
                        } else {
                            if (mCallback != null) {
                                mCallback.onSignInFailure();
                            }
                        }
                    }
                });
    }

    /**
     * Cierra la sesión del usuario en Google y Firebase.
     */
    public void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();

                // Invoca el callback de sign out completado.
                if (mCallback != null) {
                    mCallback.onSignOutComplete();
                }
                // Redirige al usuario a la MainActivity después del cierre de sesión.
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
    }

    /**
     * Obtiene la URL de la imagen de perfil del usuario de Google.
     *
     * @return La URL de la imagen de perfil, o null si no está disponible.
     */
    public String getProfileImageUrl() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null && account.getPhotoUrl() != null) {
            return account.getPhotoUrl().toString();
        }
        return null; // Retorna null si no hay imagen de perfil o usuario no está conectado.
    }

    /**
     * Obtiene el nombre del perfil del usuario de Google.
     *
     * @return El nombre del perfil, o null si no está disponible.
     */
    public String getProfileName() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            return account.getDisplayName(); // Retorna el nombre del perfil.
        }
        return null; // Retorna null si el usuario no está conectado.
    }

    /**
     * Obtiene el correo electrónico del usuario de Google.
     *
     * @return El correo electrónico, o null si no está disponible.
     */
    public String getProfileEmail() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            return account.getEmail(); // Retorna el correo electrónico.
        }
        return null; // Retorna null si el usuario no está conectado.
    }
}
