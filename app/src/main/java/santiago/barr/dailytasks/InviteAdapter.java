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

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteViewHolder> {

    private List<Invite> inviteList;
    private Context context;

    public InviteAdapter(List<Invite> inviteList, Context context) {
        this.inviteList = inviteList;
        this.context = context;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_item, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        Invite invite = inviteList.get(position);
        holder.emailTextView.setText(invite.getEmail());
        holder.messageTextView.setText(invite.getMessage());

        holder.acceptButton.setOnClickListener(v -> acceptInvite(invite));
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    private void acceptInvite(Invite invite) {
        DatabaseReference db = FirebaseDatabase.getInstance("https://daily-tasks-eea7e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String inviteId = invite.getId();
        String groupId = invite.getGroupId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.child("invitaciones").child(inviteId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                db.child("grupos").child(groupId).child("usuarios").child(userId).setValue(userEmail);
                db.child("usuarios").child(userId).child("grupos").child(groupId).setValue(groupId);
                Toast.makeText(context, "Invitación aceptada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al aceptar la invitación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class InviteViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView, messageTextView;
        Button acceptButton;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}
