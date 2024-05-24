package santiago.barr.dailytasks;

public class Chat {
    private String mensaje;
    private String mensajeId;
    private String timestamp;
    private String userId;

    public Chat() {}

    public Chat(String mensaje, String mensajeId, String timestamp, String userId) {
        this.mensaje = mensaje;
        this.mensajeId = mensajeId;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(String mensajeId) {
        this.mensajeId = mensajeId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
