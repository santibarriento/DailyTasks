package santiago.barr.dailytasks;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userProfileImage;
    private TextView userName, userEmail;
    private Spinner userAge, userGender;
    private EditText userPhone; // Nuevo campo de número de teléfono
    private Button logoutButton, updateDataButton, backButton;
    private FirebaseAuth mAuth;
    private RealtimeDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_acc);

        userProfileImage = findViewById(R.id.imageView);
        userName = findViewById(R.id.textViewNombre);
        userEmail = findViewById(R.id.textViewEmail);
        userAge = findViewById(R.id.spinnerAge);
        userGender = findViewById(R.id.spinnerGender);
        userPhone = findViewById(R.id.editTextPhone); // Inicializar el campo de número de teléfono
        logoutButton = findViewById(R.id.buttonSignOff);
        updateDataButton = findViewById(R.id.buttonData);
        backButton = findViewById(R.id.back_button);

        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new RealtimeDatabaseHelper(this);
        FirebaseUser user = mAuth.getCurrentUser();

        // Inicializar opciones de edad en el spinner
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

        // Inicializar opciones de género en el spinner
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
                        userPhone.setText(dataSnapshot.child("telefono").getValue(String.class)); // Mostrar el número de teléfono si existe en la base de datos
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this, "Error al cargar datos del perfil: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> finish());

        updateDataButton.setOnClickListener(v -> {
            updateUserProfile();
        });
    }

    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String age = userAge.getSelectedItem().toString();
            String gender = userGender.getSelectedItem().toString();
            String phone = userPhone.getText().toString(); // Obtener el número de teléfono

            databaseHelper.saveUserData(userName.getText().toString(), userEmail.getText().toString(), user.getPhotoUrl().toString(), phone, true, false);
            databaseHelper.saveUserProfileData(user.getUid(), age, gender);
        }
    }
}
