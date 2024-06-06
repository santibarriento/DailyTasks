package santiago.barr.dailytasks;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja la actividad del perfil de usuario.
 * Permite ver y actualizar la información del usuario.
 */
public class UserProfileActivity extends AppCompatActivity {

    // Declaración de variables para los elementos de la UI
    private ImageView userProfileImage;
    private TextView userName, userEmail;
    private Spinner userAge, userGender;
    private EditText userPhone, editTextNewUserName; // Nuevo campo para el nombre de usuario
    private Button logoutButton, updateDataButton, updateUserNameButton, backButton;
    private FirebaseAuth mAuth;
    private RealtimeDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Inicialización de las vistas
        userProfileImage = findViewById(R.id.imageView);
        userName = findViewById(R.id.textViewNombre);
        userEmail = findViewById(R.id.textViewEmail);
        userAge = findViewById(R.id.spinnerAge);
        userGender = findViewById(R.id.spinnerGender);
        userPhone = findViewById(R.id.editTextPhone);
        editTextNewUserName = findViewById(R.id.editTextNewUserName);
        logoutButton = findViewById(R.id.buttonSignOff);
        updateDataButton = findViewById(R.id.buttonData);
        updateUserNameButton = findViewById(R.id.buttonUpdateUserName);
        backButton = findViewById(R.id.back_button);

        // Inicialización de Firebase Auth y Database Helper
        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new RealtimeDatabaseHelper(this);
        FirebaseUser user = mAuth.getCurrentUser();

        // Configuración de las opciones del spinner de edad
        List<String> ageOptions = new ArrayList<>();
        for (int i = 18; i <= 99; i++) {
            ageOptions.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapterAge = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ageOptions) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                return view;
            }
        };
        adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userAge.setAdapter(adapterAge);

        // Configuración de las opciones del spinner de género
        String[] genderArray = getResources().getStringArray(R.array.gender_options);
        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                return view;
            }
        };
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGender.setAdapter(adapterGender);

        // Si el usuario está autenticado, cargar la información del perfil
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(userProfileImage);

            DatabaseReference db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("usuarios").child(user.getUid());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userAge.setSelection(adapterAge.getPosition(dataSnapshot.child("edad").getValue(String.class)));
                        userGender.setSelection(adapterGender.getPosition(dataSnapshot.child("genero").getValue(String.class)));
                        userPhone.setText(dataSnapshot.child("phone").getValue(String.class)); // Mostrar el número de teléfono si existe en la base de datos
                        userName.setText(dataSnapshot.child("userName").getValue(String.class)); // Mostrar el nombre de usuario
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this, "Error al cargar datos del perfil: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        // Acción de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Acción de regresar
        backButton.setOnClickListener(v -> finish());

        // Acción de actualizar el perfil del usuario
        updateDataButton.setOnClickListener(v -> {
            updateUserProfile();
        });

        // Acción de actualizar el nombre de usuario
        updateUserNameButton.setOnClickListener(v -> {
            updateUserName();
        });
    }

    /**
     * Método para actualizar el perfil del usuario.
     */
    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String age = userAge.getSelectedItem().toString();
            String gender = userGender.getSelectedItem().toString();
            String phone = userPhone.getText().toString();

            databaseHelper.saveUserData(userName.getText().toString(), userEmail.getText().toString(), user.getPhotoUrl().toString(), phone, true, false);
            databaseHelper.saveUserProfileData(user.getUid(), age, gender);
        }
    }

    /**
     * Método para actualizar el nombre de usuario.
     */
    private void updateUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String newUserName = editTextNewUserName.getText().toString().trim();
            if (TextUtils.isEmpty(newUserName)) {
                Toast.makeText(this, "Por favor, ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("usuarios").child(user.getUid());
            db.child("userName").setValue(newUserName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userName.setText(newUserName);
                            Toast.makeText(UserProfileActivity.this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
