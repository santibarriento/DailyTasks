package santiago.barr.dailytasks;

public class Group {
    private String id;
    private String nombre;
    private String descripcion;
    private String admin;

    // Constructor vacío requerido para Firebase
    public Group() {}

    public Group(String id, String nombre, String descripcion, String admin) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
