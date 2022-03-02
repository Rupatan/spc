package ru.pm52.myapplication.Model;

import static ru.pm52.myapplication.ViewModel.TaskViewModel.EVENT_UPDATE;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.pm52.myapplication.App;
import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.HttpFile;
import ru.pm52.myapplication.INotify;

public class TaskRepository <T> extends RepositoryBase implements INotify {

    public static String TYPE_WORKS = "uploadtypeworks";

    private HashMap<Integer, Uri> addedImages = new HashMap<>();
    private final T taskModel;

    public TaskRepository(T taskModel) {
        this.taskModel = taskModel;
    }

    public void getListTypeWorks() {
        String uidString = HTTPClient.HTTPProcess.getUID();
        String pathURL = String.format("mobile/taskworks/getlist?uid=%1$s", uidString);
        HTTPClient.Builder builder = HTTPClient.HTTPProcess.getCustomBuilder(pathURL).callback(this);
        HTTPClient client = builder.build();
        client.setNameEvent(TYPE_WORKS).sendAsync();
    }

    public T getModel(){
        return taskModel;
    }

    public void sendComplete(String nameEvent) {
        sendComplete(nameEvent, this.taskModel);
    }

    public void sendComplete(String nameEvent, @NonNull T taskModel){
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString().replace('-', '_');
        AuthRepository authRepository = AuthRepository.getInstance();

        HTTPClient.Builder client = new HTTPClient.Builder(ModelContext.URLBase)
                .addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT")
                .addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                .addHeader("Cache-Control", "post-check=0, pre-check=0")
                .addHeader("Pragma", "no-cache")
                .authentication(authRepository.getUsername(), authRepository.getPassword())
                .pathURL("mobile/tasks/update?uid=" + uuidAsString)
                .method(HTTPClient.METHOD_SEND.POST)
                .callback(this);

        ContentResolver contentResolver = App.getContext().getContentResolver();
        for (Map.Entry<Integer, Uri> i : addedImages.entrySet()) {
            try {
                Uri uri = i.getValue();
                File file = new File(i.getValue().getPath());
                String fileName = file.getName();
                InputStream inputStream = contentResolver.openInputStream(uri);
                byte[] data = HTTPClient.HTTPProcess.getBytes(inputStream);
                HttpFile httpFile = new HttpFile();

                String contentDisposition = String.format("Content-Disposition: " +
                        "name=\"%1$s\"; filename=\"%2$s\"", httpFile.Name, httpFile.FileName);

                httpFile.FileName = fileName;
                httpFile.Name = fileName.substring(0, fileName.lastIndexOf((int) '.'));
                httpFile.Data = data;
                httpFile.ContentDesposition = contentDisposition;
                httpFile.ContentType = "Content-Type: image/jpeg";
                client.addFile(httpFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String stringJson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create()
                .toJson(taskModel);

        HttpFile objectJson = new HttpFile();
        objectJson.ContentDesposition = "Content-Disposition: form-data; name=\"task\"";
        objectJson.ContentType = "Content-Type: application/json; charset=UTF-8";
        objectJson.Data = stringJson.getBytes(StandardCharsets.UTF_8);
        client.addFile(objectJson);

        client.build().setNameEvent(nameEvent).sendAsync();
    }

    public void addImage(int counter, Uri uri) {
        addedImages.put(counter, uri);
    }

    public Uri getImage(int id){
        return addedImages.get(id);
    }

    public void deleteImage(int id){
        addedImages.remove(id);
    }

//    @Override
//    public void NotifyResponse(String eventString, Object... params) {
////        if (eventString.equals(TYPE_WORKS)) {
////            int code = (int) params[0];
////            if (code == 200) {
////
////            }
////        }else if(eventString.equals(EVENT_UPDATE)){
////            notifyAll();
////        }
//
//        super.NotifyResponse(eventString, params);
//    }
}
