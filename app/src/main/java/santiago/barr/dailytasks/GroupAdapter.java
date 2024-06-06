package santiago.barr.dailytasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Adaptador personalizado para mostrar la lista de grupos en un ListView.
 */
public class GroupAdapter extends ArrayAdapter<Group> {
    private Context context;
    private ArrayList<Group> groupList;

    /**
     * Constructor del adaptador.
     *
     * @param context   Contexto de la actividad.
     * @param groupList Lista de grupos a mostrar.
     */
    public GroupAdapter(@NonNull Context context, ArrayList<Group> groupList) {
        super(context, 0, groupList);
        this.context = context;
        this.groupList = groupList;
    }

    /**
     * Método para obtener y configurar la vista de cada elemento en la lista.
     *
     * @param position    Posición del elemento en la lista.
     * @param convertView Vista a reutilizar.
     * @param parent      Padre al que se adjunta la vista.
     * @return La vista configurada para el elemento.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Inflar la vista de grupo si no existe.
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        }

        // Obtener el grupo en la posición actual.
        Group group = groupList.get(position);

        // Obtener las vistas de nombre y descripción del grupo.
        TextView groupNameTextView = convertView.findViewById(R.id.group_name);
        TextView groupDescriptionTextView = convertView.findViewById(R.id.group_description);

        // Establecer el nombre y la descripción del grupo.
        groupNameTextView.setText(group.getNombre());
        groupDescriptionTextView.setText(group.getDescripcion());

        // Configurar el clic en el elemento para abrir los detalles del grupo.
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupDetailActivity.class);
            intent.putExtra("groupId", group.getId());
            context.startActivity(intent);
        });

        return convertView;
    }
}
