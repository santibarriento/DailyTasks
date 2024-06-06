package santiago.barr.dailytasks;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Actividad para editar una tarea existente. Permite cambiar el título, descripción, fecha de vencimiento, usuario asignado y estado de la tarea.
 */
public class EditTaskActivity extends AppCompatActivity {

    private EditText taskTitle, taskDescription, taskDueDate;
    private Spinner userSpinner, taskStatus;
    private Button updateTaskButton, deleteTaskButton;
    private DatabaseReference db;
    private String taskId, groupId;

    private List<String> groupUsers;
    private ArrayAdapter<String> userAdapter;
    private ArrayAdapter<CharSequence> statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Inicializar vistas
        taskTitle = findViewById(R.id.edit_task_title);
        taskDescription = findViewById(R.id.edit_task_description);
        taskDueDate = findViewById(R.id.edit_task_due_date);
        userSpinner = findViewById(R.id.edit_user_spinner);
        taskStatus = findViewById(R.id.edit_task_status);
        updateTaskButton = findViewById(R.id.update_task_button);
        deleteTaskButton = findViewById(R.id.delete_task_button);

        // Obtener extras del Intent
        taskId = getIntent().getStringExtra("taskId");
        groupId = getIntent().getStringExtra("groupId");

        // Verificar que taskId y groupId no son nulos
        if (taskId == null || groupId == null) {
            Toast.makeText(this, "Error: falta taskId o groupId", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si los valores son nulos
            return;
        }

        // Inicializar la referencia a la base de datos
        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        // Inicializar listas y adaptadores
        groupUsers = new ArrayList<>();
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupUsers);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);

        statusAdapter = ArrayAdapter.createFromResource(this, R.array.task_status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskStatus.setAdapter(statusAdapter);

        // Cargar detalles de la tarea y usuarios del grupo
        loadTaskDetails(taskId, groupId);
        loadGroupUsers(groupId);

        // Configurar el selector de fecha
        taskDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Configurar botones de actualización y eliminación de tarea
        updateTaskButton.setOnClickListener(v -> updateTask());
        deleteTaskButton.setOnClickListener(v -> deleteTask());
    }

    /**
     * Carga los detalles de una tarea desde la base de datos y los muestra en la interfaz.
     * @param taskId ID de la tarea a cargar.
     * @param groupId ID del grupo al que pertenece la tarea.
     */
    private void loadTaskDetails(String taskId, String groupId) {
        db.child("grupos").child(groupId).child("tareas").child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskTitle.setText(task.getNombre());
                        taskDescription.setText(task.getDescripcion());
                        taskDueDate.setText(task.getFechaVencimiento());
                        userSpinner.setSelection(userAdapter.getPosition(task.getAsignadoA()));
                        taskStatus.setSelection(statusAdapter.getPosition(task.getEstado()));
                    }
                } else {
                    Toast.makeText(EditTaskActivity.this, "Detalles de la tarea no encontrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditTaskActivity.this, "Error al cargar los detalles de la tarea: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carga los usuarios de un grupo desde la base de datos y los muestra en el spinner de usuarios.
     * @param groupId ID del grupo cuyos usuarios se van a cargar.
     */
    private void loadGroupUsers(String groupId) {
        db.child("grupos").child(groupId).child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupUsers.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userEmail = userSnapshot.getValue(String.class);
                        if (userEmail != null) {
                            groupUsers.add(userEmail);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditTaskActivity.this, "Error al cargar los usuarios: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Actualiza los detalles de la tarea en la base de datos.
     */
    private void updateTask() {
        String selectedUser = userSpinner.getSelectedItem().toString();
        String title = taskTitle.getText().toString();
        String description = taskDescription.getText().toString();
        String dueDate = taskDueDate.getText().toString();
        String status = taskStatus.getSelectedItem().toString();

        Task updatedTask = new Task(taskId, title, description, status, dueDate, selectedUser, groupId);
        db.child("grupos").child(groupId).child("tareas").child(taskId).setValue(updatedTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditTaskActivity.this, "Tarea actualizada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditTaskActivity.this, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Elimina la tarea de la base de datos.
     */
    private void deleteTask() {
        db.child("grupos").child(groupId).child("tareas").child(taskId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditTaskActivity.this, "Tarea eliminada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditTaskActivity.this, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Muestra un cuadro de diálogo de selector de fecha y establece la fecha seleccionada en el campo de fecha de vencimiento.
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            taskDueDate.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);
        datePickerDialog.show();
    }
}
