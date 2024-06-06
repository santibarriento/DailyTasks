package santiago.barr.dailytasks;

/**
 * Clase que representa una invitación en la aplicación.
 */
public class Invite {
    private String inviteId;  // Identificador único de la invitación
    private String email;  // Correo electrónico del destinatario de la invitación
    private String message;  // Mensaje de la invitación
    private String groupId;  // ID del grupo al que se invita al usuario
    private String senderId;  // ID del usuario que envía la invitación
    private String status;  // Estado de la invitación (pendiente, aceptada, rechazada)

    /**
     * Constructor por defecto.
     */
    public Invite() {
    }

    /**
     * @return El identificador de la invitación.
     */
    public String getInviteId() {
        return inviteId;
    }

    /**
     * Establece el identificador de la invitación.
     *
     * @param inviteId Identificador de la invitación.
     */
    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    /**
     * @return El correo electrónico del destinatario de la invitación.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del destinatario de la invitación.
     *
     * @param email Correo electrónico del destinatario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return El mensaje de la invitación.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje de la invitación.
     *
     * @param message Mensaje de la invitación.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return El ID del grupo al que se invita al usuario.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Establece el ID del grupo al que se invita al usuario.
     *
     * @param groupId ID del grupo.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return El ID del usuario que envía la invitación.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Establece el ID del usuario que envía la invitación.
     *
     * @param senderId ID del usuario que envía la invitación.
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return El estado de la invitación.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Establece el estado de la invitación.
     *
     * @param status Estado de la invitación (pendiente, aceptada, rechazada).
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return El identificador de la invitación.
     */
    public String getId() {
        return inviteId;
    }
}
