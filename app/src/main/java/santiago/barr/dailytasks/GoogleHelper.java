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

public class GoogleHelper {
    private GoogleSignInClient mGoogleSignInClient;
    private Activity mActivity;
    private GoogleSignInResultCallback mCallback;

    public interface GoogleSignInResultCallback {
        void onSignInSuccess();
        void onSignInFailure();
        void onSignOutComplete();
    }

    public GoogleHelper(Activity activity, GoogleSignInResultCallback callback) {
        this.mActivity = activity;
        this.mCallback = callback;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }
    public Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent data) {
        return GoogleSignIn.getSignedInAccountFromIntent(data);
    }

    public Intent getSignInIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

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
    public void signOut(){

        mGoogleSignInClient.signOut().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Opcional: Cerrar sesión de Firebase si estás utilizando Firebase Auth
                FirebaseAuth.getInstance().signOut();

                // Invocar el callback de sign out completado
                if (mCallback != null) {
                    mCallback.onSignOutComplete();
                }
                // Redirige al usuario a la MainActivity después del cierre de sesión
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish(); // Opcional: Llama a finish() si quieres sacar de la pila de actividades la actividad actual
            }
        });
    }

    public String getProfileImageUrl() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null && account.getPhotoUrl() != null) {
            return account.getPhotoUrl().toString();
        }
        return null; // Retorna null si no hay imagen de perfil o usuario no está conectado
    }

    // Método para obtener el nombre del perfil
    public String getProfileName() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            return account.getDisplayName(); // Retorna el nombre del perfil
        }
        return null; // Retorna null si el usuario no está conectado
    }

    // Método para obtener el correo electrónico
    public String getProfileEmail() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            return account.getEmail(); // Retorna el correo electrónico
        }
        return null; // Retorna null si el usuario no está conectado
    }

}
