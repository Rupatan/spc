package ru.pm52.myapplication.Model;

import androidx.annotation.Nullable;

import java.util.UUID;

import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.ICallbackResponse;
import ru.pm52.myapplication.INotify;

public class AuthRepository {

    @Nullable private static AuthRepository instance;

    private AuthModel model = new AuthModel();

    private AuthRepository() {

    }

    public static AuthRepository getInstance(){
        if (instance == null)
            instance = new AuthRepository();

        return instance;
    }

    public void login(String username, String password, @Nullable String nameEvent, @Nullable INotify callback) {
        model = new AuthModel(username, password);

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString().replace('-', '_');

        HTTPClient client = new HTTPClient.Builder(ModelContext.URLBase)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT")
                .addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                .addHeader("Cache-Control", "post-check=0, pre-check=0")
                .addHeader("Pragma", "no-cache")
                .authentication(username, password)
                .pathURL("mobile/tasks/getlist?uid=" + uuidAsString)
                .method(HTTPClient.METHOD_SEND.POST)
                .callback(callback)
                .build();

        client.setNameEvent(nameEvent).sendAsync();
    }

    public String getUsername(){
        return model.getLogin();
    }

    public String getPassword(){
        return model.getPassword();
    }

}
