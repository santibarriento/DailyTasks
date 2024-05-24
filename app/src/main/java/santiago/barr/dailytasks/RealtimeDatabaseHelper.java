package santiago.barr.dailytasks;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDatabaseHelper {

    private Context context;

    public RealtimeDatabaseHelper(Context context) {
        this.context = context;
    }

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

    public void saveUserProfileData(String userId, String age, String gender) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("usuarios").child(userId).child("edad").setValue(age);
        databaseReference.child("usuarios").child(userId).child("genero").setValue(gender);
    }
}
