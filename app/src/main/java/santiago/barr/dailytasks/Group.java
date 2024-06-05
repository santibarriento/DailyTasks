package santiago.barr.dailytasks;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String id;
    private String admin;
    private String descripcion;
    private String nombre;
    private Map<String, Task> tareas;
    private Map<String, String> usuarios;

    public Group() {
        // Constructor por defecto necesario para llamadas a DataSnapshot.getValue(Group.class)
    }

    public Group(String id, String admin, String descripcion, String nombre) {
        this.id = id;
        this.admin = admin;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.tareas = new HashMap<>();
        this.usuarios = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, Task> getTareas() {
        return tareas;
    }

    public void setTareas(Map<String, Task> tareas) {
        this.tareas = tareas;
    }

    public Map<String, String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Map<String, String> usuarios) {
        this.usuarios = usuarios;
    }
}
