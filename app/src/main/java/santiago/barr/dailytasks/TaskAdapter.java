package santiago.barr.dailytasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getNombre());
        holder.taskDescription.setText(task.getDescripcion());
        holder.taskDueDate.setText(task.getFechaVencimiento());
        holder.taskStatus.setText(task.getEstado());
        holder.taskAssignedTo.setText(task.getAsignadoA());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("groupId", task.getGroupId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskTitle, taskDescription, taskDueDate, taskStatus, taskAssignedTo;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskDueDate = itemView.findViewById(R.id.task_due_date);
            taskStatus = itemView.findViewById(R.id.task_status);
            taskAssignedTo = itemView.findViewById(R.id.task_assigned_to);
        }
    }
}
