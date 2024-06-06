package santiago.barr.dailytasks;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Clase de ayuda para interactuar con la base de datos en tiempo real de Firebase.
 */
public class RealtimeDatabaseHelper {

    private Context context; // Contexto de la aplicación

    /**
     * Constructor que inicializa el contexto.
     * @param context El contexto de la aplicación.
     */
    public RealtimeDatabaseHelper(Context context) {
        this.context = context;
    }

    /**
     * Método para guardar los datos del usuario en la base de datos de Firebase.
     * @param userName Nombre del usuario.
     * @param email Email del usuario.
     * @param photoUrl URL de la foto del usuario.
     * @param phone Teléfono del usuario.
     * @param notifications Preferencias de notificaciones del usuario.
     * @param darkTheme Preferencias de tema oscuro del usuario.
     */
    public void saveUserData(String userName, String email, String photoUrl, String phone, boolean notifications, boolean darkTheme) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        User user = new User(userName, email, photoUrl, phone, notifications, darkTheme);
        databaseReference.child("usuarios").child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método para guardar datos adicionales del perfil del usuario en la base de datos de Firebase.
     * @param userId ID del usuario.
     * @param age Edad del usuario.
     * @param gender Género del usuario.
     */
    public void saveUserProfileData(String userId, String age, String gender) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("usuarios").child(userId).child("edad").setValue(age);
        databaseReference.child("usuarios").child(userId).child("genero").setValue(gender);
    }
}
