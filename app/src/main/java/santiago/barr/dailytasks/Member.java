package santiago.barr.dailytasks;

/**
 * Clase que representa un miembro en la aplicación.
 */
public class Member {
    private String rol; // El rol del miembro en el grupo (por ejemplo, administrador, miembro)
    private String userId; // El ID del usuario en la base de datos

    /**
     * Constructor por defecto necesario para llamadas a DataSnapshot.getValue(Member.class).
     */
    public Member() {}

    /**
     * Constructor que inicializa el rol y el ID del usuario.
     * @param rol El rol del miembro en el grupo.
     * @param userId El ID del usuario en la base de datos.
     */
    public Member(String rol, String userId) {
        this.rol = rol;
        this.userId = userId;
    }

    /**
     * Obtiene el rol del miembro.
     * @return El rol del miembro.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del miembro.
     * @param rol El rol del miembro.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el ID del usuario.
     * @return El ID del usuario.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Establece el ID del usuario.
     * @param userId El ID del usuario.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
