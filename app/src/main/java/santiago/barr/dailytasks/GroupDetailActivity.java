package santiago.barr.dailytasks;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * Actividad que muestra los detalles de un grupo seleccionado, incluyendo las tareas y los usuarios.
 */
public class GroupDetailActivity extends AppCompatActivity {

    private TextView groupNameDetail, groupDescriptionDetail;
    private Button backButton, inviteMemberButton, addTaskButton;
    private RecyclerView tasksRecyclerView;
    private TaskAdapter tasksAdapter;
    private List<Task> tasksList;
    private DatabaseReference db;
    private String groupId;

    private Spinner userSpinner;
    private EditText taskTitle, taskDescription, taskDueDate;

    private List<String> groupUsers;
    private ArrayAdapter<String> userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // Inicialización de vistas
        groupNameDetail = findViewById(R.id.group_name_detail);
        groupDescriptionDetail = findViewById(R.id.group_description_detail);
        backButton = findViewById(R.id.back_button);
        inviteMemberButton = findViewById(R.id.invite_member_button);
        addTaskButton = findViewById(R.id.add_task_button);
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        userSpinner = findViewById(R.id.user_spinner);
        taskTitle = findViewById(R.id.task_title);
        taskDescription = findViewById(R.id.task_description);
        taskDueDate = findViewById(R.id.task_due_date);

        tasksList = new ArrayList<>();
        tasksAdapter = new TaskAdapter(tasksList, this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(tasksAdapter);

        groupUsers = new ArrayList<>();
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupUsers);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);

        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        groupId = getIntent().getStringExtra("groupId");

        loadGroupDetails(groupId);
        loadGroupTasks(groupId);
        loadGroupUsers(groupId);

        // Configuración de botones
        backButton.setOnClickListener(v -> finish());

        inviteMemberButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailActivity.this, InviteActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        addTaskButton.setOnClickListener(v -> createTask());

        taskDueDate.setOnClickListener(v -> showDatePickerDialog());
    }

    /**
     * Crea una nueva tarea y la añade al grupo.
     */
    private void createTask() {
        String selectedUser = userSpinner.getSelectedItem().toString();
        String title = taskTitle.getText().toString();
        String description = taskDescription.getText().toString();
        String dueDate = taskDueDate.getText().toString();
        String taskId = db.child("grupos").child(groupId).child("tareas").push().getKey();

        if (taskId != null) {
            Task task = new Task(taskId, title, description, "pendiente", dueDate, selectedUser, groupId);
            db.child("grupos").child(groupId).child("tareas").child(taskId).setValue(task)
                    .addOnCompleteListener(taskCompletion -> {
                        if (taskCompletion.isSuccessful()) {
                            Toast.makeText(this, "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                            loadGroupTasks(groupId);
                        } else {
                            Toast.makeText(this, "Error al crear la tarea", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Carga los detalles del grupo desde la base de datos.
     *
     * @param groupId ID del grupo a cargar.
     */
    private void loadGroupDetails(String groupId) {
        db.child("grupos").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group != null) {
                        groupNameDetail.setText(group.getNombre());
                        groupDescriptionDetail.setText(group.getDescripcion());
                    }
                } else {
                    Toast.makeText(GroupDetailActivity.this, "Detalles del grupo no encontrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailActivity.this, "Error al cargar detalles del grupo: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carga las tareas del grupo desde la base de datos.
     *
     * @param groupId ID del grupo.
     */
    private void loadGroupTasks(String groupId) {
        DatabaseReference groupTasksRef = db.child("grupos").child(groupId).child("tareas");
        groupTasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasksList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        Task task = taskSnapshot.getValue(Task.class);
                        if (task != null) {
                            tasksList.add(task);
                        }
                    }
                    tasksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailActivity.this, "Error al cargar las tareas: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carga los usuarios del grupo desde la base de datos.
     *
     * @param groupId ID del grupo.
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
                Toast.makeText(GroupDetailActivity.this, "Error al cargar los usuarios: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra un cuadro de diálogo para seleccionar la fecha.
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
