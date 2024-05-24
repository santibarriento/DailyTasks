package santiago.barr.dailytasks;

public class Member {
    private String rol;
    private String userId;

    public Member() {}

    public Member(String rol, String userId) {
        this.rol = rol;
        this.userId = userId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
