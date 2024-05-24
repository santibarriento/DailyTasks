package santiago.barr.dailytasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GoogleAccount extends AppCompatActivity implements View.OnClickListener{

    private GoogleHelper googleHelper;
    private ImageView imageView;
    public TextView textViewNombre;
    public TextView textViewEmail;
    private Button buttonRanking;
    private Button buttonSignOff;
    private Button buttonData;
    private Spinner spinnerAge;
    private Spinner spinnerGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_acc);

        imageView = findViewById(R.id.imageView);
        textViewNombre = findViewById(R.id.textViewNombre);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonSignOff = findViewById(R.id.buttonSignOff);
        buttonSignOff.setOnClickListener(this);
        spinnerAge = findViewById(R.id.spinnerAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        buttonData = findViewById(R.id.buttonData);
        buttonData.setOnClickListener(this);

        // Genera las edades
        List<String> ageOptions = new ArrayList<>();
        for (int i = 10; i <= 40; i++) {
            ageOptions.add(Integer.toString(i));
        }

        // Crea un ArrayAdapter con estilo personalizado para el spinner de edades
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

        // Convierte el array de recursos en una lista
        String[] genderArray = getResources().getStringArray(R.array.gender_options);
        List<String> genderList = Arrays.asList(genderArray);

        // Crea un ArrayAdapter con estilo personalizado para el spinner de género
        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList) {
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
        spinnerGender.setAdapter(adapterGender);
        adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(adapterAge);


        // Inicializa GoogleHelper con un callback
        googleHelper = new GoogleHelper(this, new GoogleHelper.GoogleSignInResultCallback() {
            @Override
            public void onSignInSuccess() {
                // Asegúrate de que tienes una URL para la imagen del perfil
                String imageUrl = googleHelper.getProfileImageUrl();
                if (imageUrl != null) {
                    // Carga la imagen con Glide y aplica una transformación para redondearla
                    Glide.with(GoogleAccount.this)
                            .load(imageUrl)
                            .apply(RequestOptions.circleCropTransform()) // Esta línea redondea la imagen
                            .into(imageView);
                }

                // Actualiza los TextView con el nombre y correo electrónico
                String nombre = googleHelper.getProfileName();
                String email = googleHelper.getProfileEmail();
                textViewNombre.setText(nombre);
                textViewEmail.setText(email);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("usuario").document(email).get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    spinnerGender.setSelection(documentSnapshot.getString("genero").equals("Male") || documentSnapshot.getString("genero").equals("Masculino") ? 0 : 1);
                                    spinnerAge.setSelection(Integer.parseInt(documentSnapshot.getString("edad")) - 10);
                                }
                            }
                        });

            }

            @Override
            public void onSignInFailure() {

            }

            @Override
            public void onSignOutComplete() {

            }
        });

        // Ejemplo de cómo iniciar el inicio de sesión
        // Esto probablemente lo llamarás en respuesta a un evento, como un clic de botón
        Intent signInIntent = googleHelper.getSignInIntent();
        startActivityForResult(signInIntent, 9001); // 9001 es un código de solicitud arbitrario
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado devuelto al lanzar el Intent desde GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            googleHelper.handleSignInResult(googleHelper.getSignedInAccountFromIntent(data));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSignOff){
            googleHelper.signOut();
            Toast.makeText(GoogleAccount.this, "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show();

        }
        if (v.getId() == R.id.buttonData){
            // Llamada al método uploadUserData de la clase UploadData
            UploadData.uploadUserData(textViewNombre, textViewEmail, spinnerAge, spinnerGender);
            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
        }

    }

}
