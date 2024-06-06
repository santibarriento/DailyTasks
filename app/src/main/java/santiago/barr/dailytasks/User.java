package santiago.barr.dailytasks;

/**
 * Clase que representa un usuario en la aplicación.
 */
public class User {
    public String userName;      // Nombre de usuario
    public String email;         // Correo electrónico del usuario
    public String photoUrl;      // URL de la foto del usuario
    public String phone;         // Número de teléfono del usuario
    public boolean notifications; // Indicador de si el usuario quiere recibir notificaciones
    public boolean darkTheme;    // Indicador de si el usuario prefiere el tema oscuro

    /**
     * Constructor vacío necesario para llamadas a DataSnapshot.getValue(User.class)
     */
    public User() {
    }

    /**
     * Constructor con parámetros para inicializar los atributos del usuario.
     *
     * @param userName      Nombre de usuario.
     * @param email         Correo electrónico del usuario.
     * @param photoUrl      URL de la foto del usuario.
     * @param phone         Número de teléfono del usuario.
     * @param notifications Indicador de si el usuario quiere recibir notificaciones.
     * @param darkTheme     Indicador de si el usuario prefiere el tema oscuro.
     */
    public User(String userName, String email, String photoUrl, String phone, boolean notifications, boolean darkTheme) {
        this.userName = userName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phone = phone;
        this.notifications = notifications;
        this.darkTheme = darkTheme;
    }
}
