package ru.vsu.csf.framework.persistence;

public class BaseDataSource {
    private final String Url;
    private final String user;
    private final String password;

    public BaseDataSource(String url, String user, String password) {
        Url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return Url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
