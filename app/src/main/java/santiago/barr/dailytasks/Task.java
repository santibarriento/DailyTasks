package santiago.barr.dailytasks;

/**
 * Clase que representa una tarea en la aplicación.
 */
public class Task {

    private String id; // Identificador único de la tarea
    private String nombre; // Nombre de la tarea
    private String descripcion; // Descripción de la tarea
    private String estado; // Estado de la tarea (pendiente, en progreso, completada)
    private String fechaVencimiento; // Fecha de vencimiento de la tarea
    private String asignadoA; // Usuario al que está asignada la tarea
    private String groupId; // Identificador del grupo al que pertenece la tarea

    /**
     * Constructor vacío requerido por Firebase
     */
    public Task() {
    }

    /**
     * Constructor con parámetros para inicializar una tarea
     * @param id Identificador único de la tarea
     * @param nombre Nombre de la tarea
     * @param descripcion Descripción de la tarea
     * @param estado Estado de la tarea
     * @param fechaVencimiento Fecha de vencimiento de la tarea
     * @param asignadoA Usuario asignado a la tarea
     * @param groupId Identificador del grupo al que pertenece la tarea
     */
    public Task(String id, String nombre, String descripcion, String estado, String fechaVencimiento, String asignadoA, String groupId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
        this.asignadoA = asignadoA;
        this.groupId = groupId;
    }

    /**
     * Obtiene el identificador único de la tarea
     * @return id de la tarea
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador único de la tarea
     * @param id nuevo identificador de la tarea
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la tarea
     * @return nombre de la tarea
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la tarea
     * @param nombre nuevo nombre de la tarea
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la tarea
     * @return descripción de la tarea
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la tarea
     * @param descripcion nueva descripción de la tarea
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el estado de la tarea
     * @return estado de la tarea
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la tarea
     * @param estado nuevo estado de la tarea
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la fecha de vencimiento de la tarea
     * @return fecha de vencimiento de la tarea
     */
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Establece la fecha de vencimiento de la tarea
     * @param fechaVencimiento nueva fecha de vencimiento de la tarea
     */
    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Obtiene el usuario asignado a la tarea
     * @return usuario asignado a la tarea
     */
    public String getAsignadoA() {
        return asignadoA;
    }

    /**
     * Establece el usuario asignado a la tarea
     * @param asignadoA nuevo usuario asignado a la tarea
     */
    public void setAsignadoA(String asignadoA) {
        this.asignadoA = asignadoA;
    }

    /**
     * Obtiene el identificador del grupo al que pertenece la tarea
     * @return identificador del grupo
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Establece el identificador del grupo al que pertenece la tarea
     * @param groupId nuevo identificador del grupo
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
