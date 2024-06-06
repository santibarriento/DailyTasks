package santiago.barr.dailytasks;

import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Adaptador personalizado para mostrar la lista de invitaciones en un RecyclerView.
 */
public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteViewHolder> {

    private List<Invite> inviteList; // Lista de invitaciones
    private Context context; // Contexto de la actividad

    /**
     * Constructor del adaptador de invitaciones.
     *
     * @param inviteList Lista de invitaciones.
     * @param context    Contexto de la actividad.
     */
    public InviteAdapter(List<Invite> inviteList, Context context) {
        this.inviteList = inviteList;
        this.context = context;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño del elemento de invitación
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_item, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        // Obtener la invitación actual
        Invite invite = inviteList.get(position);
        holder.emailTextView.setText(invite.getEmail()); // Establecer el correo electrónico
        holder.messageTextView.setText(invite.getMessage()); // Establecer el mensaje

        // Configurar el botón de aceptar invitación
        holder.acceptButton.setOnClickListener(v -> acceptInvite(invite));
    }

    @Override
    public int getItemCount() {
        // Devolver el tamaño de la lista de invitaciones
        return inviteList.size();
    }

    /**
     * Método para aceptar una invitación.
     *
     * @param invite Invitación a aceptar.
     */
    private void acceptInvite(Invite invite) {
        DatabaseReference db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String inviteId = invite.getId(); // Obtener el ID de la invitación
        String groupId = invite.getGroupId(); // Obtener el ID del grupo
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtener el ID del usuario actual
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Obtener el correo electrónico del usuario actual

        // Eliminar la invitación de la base de datos
        db.child("invitaciones").child(inviteId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Añadir el usuario al grupo en la base de datos
                db.child("grupos").child(groupId).child("usuarios").child(userId).setValue(userEmail);
                db.child("usuarios").child(userId).child("grupos").child(groupId).setValue(groupId);
                Toast.makeText(context, "Invitación aceptada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al aceptar la invitación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ViewHolder para el adaptador de invitaciones.
     */
    public static class InviteViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView, messageTextView;
        Button acceptButton;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las vistas del elemento de invitación
            emailTextView = itemView.findViewById(R.id.emailTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}
