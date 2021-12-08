package ru.pm52.myapplication.Model;

import java.util.List;
import java.util.UUID;

import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.INotify;

public class TaskRepository implements INotify {

    public static String TYPE_WORKS = "uploadtypeworks";

    public void getListTypeWorks() {
        String uidString = HTTPClient.HTTPProcess.getUID();
        String pathURL = String.format("mobile/taskworks/getlist?uid=%1$s", uidString);
        HTTPClient.Builder builder = HTTPClient.HTTPProcess.getCustomBuilder(pathURL).callback(this);
        HTTPClient client = builder.build();
        client.setNameEvent(TYPE_WORKS).sendAsync();
    }


    @Override
    public void NotifyResponse(String eventString, Object... params)  {
        if (eventString.equals(TYPE_WORKS)){
            int code = (int)params[0];
            if (code == 200){
//                params[1];
            }
        }
    }
}
