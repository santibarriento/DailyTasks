package santiago.barr.dailytasks;

import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class UploadData {
    public interface DatosUsuarioCallback {
        void onCallback(String nombre, String email, String genero, String edad);
    }

    public static void uploadUserData(@NonNull TextView textViewNombre, TextView textViewEmail, Spinner spinnerAge, Spinner spinnerGender) {
        // instancia de firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crea un nuevo objeto usuario con los datos
        HashMap<String, Object> user = new HashMap<>();
        user.put("nombre", textViewNombre.getText().toString());
        user.put("email", textViewEmail.getText().toString());
        user.put("edad", spinnerAge.getSelectedItem().toString());
        user.put("genero", spinnerGender.getSelectedItem().toString());

        // Usa el correo electrónico como identificador único para el documento
        String emailId = textViewEmail.getText().toString();

        // Sube los datos al documento con ID basado en el correo electrónico
        db.collection("usuario").document(emailId).set(user)
                .addOnSuccessListener(aVoid -> {
                    // Manejar éxito
                    System.out.println("Datos subidos con éxito");
                })
                .addOnFailureListener(e -> {
                    // Manejar fallo
                    System.err.println("Error al subir datos: " + e.toString());
                });
    }

    public static void incrementLocationCounter(String selectedLocation) {
        // Obtener la instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener la fecha y hora actual
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setMinimalDaysInFirstWeek(1);

        // Obtener el año y el mes actual
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Enero es 0 en Calendar, por lo que se suma 1

        // Calcular el número de semana del mes actual
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        // Crear un ID de documento que incluya la ubicación, el mes y la semana del mes
        String documentId = selectedLocation + ": " + year + " mes " + month + " semana " + weekOfMonth;

        // Referencia al documento de la localización, mes y semana seleccionada
        DocumentReference locationDocRef = db.collection("estudioLocalizacion").document(documentId);

        // Lógica para incrementar el contador o crear el documento si no existe
        locationDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Si el documento existe, incrementar el contador
                    long currentCount = document.getLong("contador");
                    locationDocRef.update("contador", currentCount + 1)
                            .addOnSuccessListener(aVoid -> System.out.println("Contador incrementado con éxito"))
                            .addOnFailureListener(e -> System.err.println("Error al incrementar contador: " + e.toString()));
                } else {
                    // Si el documento no existe, crear uno nuevo con contador = 1
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("contador", 1);
                    locationDocRef.set(newData)
                            .addOnSuccessListener(aVoid -> System.out.println("Documento creado con éxito y contador inicializado"))
                            .addOnFailureListener(e -> System.err.println("Error al crear documento: " + e.toString()));
                }
            } else {
                System.err.println("Error al obtener documento: " + task.getException());
            }
        });
    }

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void obtenerDatosUsuarioActual(String email, DatosUsuarioCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuario").document(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String nombre = document.getString("nombre");
                            String emailUsuario = document.getString("email");
                            String genero = document.getString("genero");
                            String edad = document.getString("edad");
                            callback.onCallback(nombre, emailUsuario, genero, edad);
                        } else {
                            Log.d("UploadData", "No se encontró el documento del usuario actual");
                        }
                    } else {
                        Log.d("UploadData", "Error obteniendo documentos: ", task.getException());
                    }
                });
    }
    public static void subirDatosUsuarioConSeries(String nombre, String email, String genero, String edad, int numberOfSeries) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("email", email);
        datosUsuario.put("genero", genero);
        datosUsuario.put("edad", edad);
        datosUsuario.put("numeroDeSeries", numberOfSeries);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ranking").document(email)
                .set(datosUsuario)
                .addOnSuccessListener(documentReference -> Log.d("UploadData", "DocumentSnapshot written"))
                .addOnFailureListener(e -> Log.w("UploadData", "Error adding document", e));
    }
}
