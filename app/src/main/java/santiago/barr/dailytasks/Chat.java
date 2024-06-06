package santiago.barr.dailytasks;

/**
 * Clase que representa un mensaje de chat en la aplicación.
 */
public class Chat {
    private String mensaje; // Contenido del mensaje
    private String mensajeId; // Identificador único del mensaje
    private String timestamp; // Marca de tiempo del mensaje
    private String userId; // ID del usuario que envió el mensaje

    /**
     * Constructor vacío requerido por Firebase.
     */
    public Chat() {}

    /**
     * Constructor para inicializar un mensaje de chat con todos los atributos.
     *
     * @param mensaje Contenido del mensaje.
     * @param mensajeId Identificador único del mensaje.
     * @param timestamp Marca de tiempo del mensaje.
     * @param userId ID del usuario que envió el mensaje.
     */
    public Chat(String mensaje, String mensajeId, String timestamp, String userId) {
        this.mensaje = mensaje;
        this.mensajeId = mensajeId;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    /**
     * Obtiene el contenido del mensaje.
     *
     * @return El contenido del mensaje.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el contenido del mensaje.
     *
     * @param mensaje El contenido del mensaje.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el identificador único del mensaje.
     *
     * @return El identificador único del mensaje.
     */
    public String getMensajeId() {
        return mensajeId;
    }

    /**
     * Establece el identificador único del mensaje.
     *
     * @param mensajeId El identificador único del mensaje.
     */
    public void setMensajeId(String mensajeId) {
        this.mensajeId = mensajeId;
    }

    /**
     * Obtiene la marca de tiempo del mensaje.
     *
     * @return La marca de tiempo del mensaje.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Establece la marca de tiempo del mensaje.
     *
     * @param timestamp La marca de tiempo del mensaje.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Obtiene el ID del usuario que envió el mensaje.
     *
     * @return El ID del usuario que envió el mensaje.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Establece el ID del usuario que envió el mensaje.
     *
     * @param userId El ID del usuario que envió el mensaje.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
