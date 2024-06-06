package santiago.barr.dailytasks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para ver y aceptar invitaciones a grupos.
 */
public class AcceptInviteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewInvites; // RecyclerView para mostrar las invitaciones
    private InviteAdapter inviteAdapter; // Adaptador para manejar los elementos de invitación
    private List<Invite> inviteList; // Lista para almacenar las invitaciones
    private DatabaseReference db; // Referencia a la base de datos de Firebase
    private String userEmail; // Email del usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_invite);

        recyclerViewInvites = findViewById(R.id.recyclerViewInvites); // Inicializa el RecyclerView
        recyclerViewInvites.setLayoutManager(new LinearLayoutManager(this)); // Establece el layout manager para el RecyclerView
        inviteList = new ArrayList<>(); // Inicializa la lista de invitaciones
        inviteAdapter = new InviteAdapter(inviteList, this); // Inicializa el adaptador con la lista de invitaciones
        recyclerViewInvites.setAdapter(inviteAdapter); // Establece el adaptador para el RecyclerView

        db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference(); // Obtiene la referencia de la base de datos de Firebase
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Obtiene el email del usuario actual

        loadInvites(); // Carga las invitaciones desde la base de datos

        Button backButton = findViewById(R.id.back_button); // Inicializa el botón de retroceso
        backButton.setOnClickListener(v -> finish()); // Establece el listener de clic para el botón de retroceso
    }

    /**
     * Carga las invitaciones desde la base de datos de Firebase donde el email coincide con el email del usuario actual.
     */
    private void loadInvites() {
        db.child("invitaciones").orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        inviteList.clear(); // Limpia la lista existente de invitaciones
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Invite invite = snapshot.getValue(Invite.class); // Obtiene el objeto Invite desde el snapshot
                            if (invite != null) {
                                inviteList.add(invite); // Añade la invitación a la lista
                            }
                        }
                        inviteAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AcceptInviteActivity.this, "Error al cargar invitaciones", Toast.LENGTH_SHORT).show(); // Muestra un mensaje de error
                    }
                });
    }
}
