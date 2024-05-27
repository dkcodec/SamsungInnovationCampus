package model;

// поля для работы с бд из FireBase RealTimeDataBase
public class ProfileLS {
    private String profile_name;
    private String profile_avatar;
    private String profile_mobile;
    private String profile_address;
    private String profile_email;
    private String profile_postalCode;

    public ProfileLS() {
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_avatar() {
        return profile_avatar;
    }

    public void setProfile_avatar(String profile_avatar) {
        this.profile_avatar = profile_avatar;
    }

    public String getProfile_mobile() {
        return profile_mobile;
    }

    public void setProfile_mobile(String profile_mobile) {
        this.profile_mobile = profile_mobile;
    }

    public String getProfile_address() {
        return profile_address;
    }

    public void setProfile_address(String profile_address) {
        this.profile_address = profile_address;
    }

    public String getProfile_email() {
        return profile_email;
    }

    public void setProfile_email(String profile_email) {
        this.profile_email = profile_email;
    }

    public String getProfile_postalCode() {
        return profile_postalCode;
    }

    public void setProfile_postalCode(String profile_postalCode) {
        this.profile_postalCode = profile_postalCode;
    }
}
