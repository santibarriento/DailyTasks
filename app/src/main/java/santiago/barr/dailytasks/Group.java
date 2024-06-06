package santiago.barr.dailytasks;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un grupo en la aplicación.
 * Incluye detalles del grupo como ID, administrador, descripción, nombre, tareas y usuarios.
 */
public class Group {
    private String id;
    private String admin;
    private String descripcion;
    private String nombre;
    private Map<String, Task> tareas;
    private Map<String, String> usuarios;

    /**
     * Constructor por defecto necesario para llamadas a DataSnapshot.getValue(Group.class).
     */
    public Group() {
        // Inicializa las listas de tareas y usuarios como HashMaps vacíos.
        this.tareas = new HashMap<>();
        this.usuarios = new HashMap<>();
    }

    /**
     * Constructor con parámetros para inicializar un grupo.
     *
     * @param id          Identificador único del grupo.
     * @param admin       ID del usuario que es administrador del grupo.
     * @param descripcion Descripción del grupo.
     * @param nombre      Nombre del grupo.
     */
    public Group(String id, String admin, String descripcion, String nombre) {
        this.id = id;
        this.admin = admin;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.tareas = new HashMap<>();
        this.usuarios = new HashMap<>();
    }

    // Métodos getter y setter para cada atributo de la clase.

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
