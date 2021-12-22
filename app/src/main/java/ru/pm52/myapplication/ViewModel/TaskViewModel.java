package ru.pm52.myapplication.ViewModel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Model.TaskRepository;

public class TaskViewModel extends ViewModelBase {

    public static final String EVENT_UPDATE = "update";

    private final TaskRepository<TaskModel> taskRepository;
    private final MutableLiveData<Boolean> isDone = new MutableLiveData<>();
    public LiveData<Boolean> IsDone = isDone;

    private final MutableLiveData<Boolean> isSend = new MutableLiveData<>();
    public LiveData<Boolean> IsSend = isSend;

    public TaskViewModel(TaskRepository<TaskModel> repository) {
        this.taskRepository = repository;
        taskRepository.addListener(this);
        isSend.postValue(false);
    }

    public TaskModel getTaskModel() {
        return taskRepository.getModel();
    }

    public void addImage(Integer id, Uri uri) {
        taskRepository.addImage(id, uri);
    }

    public boolean deleteImage(int id) {
        Uri uri = taskRepository.getImage(id);
        boolean fileRemoved = deleteImageForUri(uri);
        if (fileRemoved)
            taskRepository.deleteImage(id);
        return fileRemoved;
    }

    public void sendComplete() {
        isSend.postValue(true);
        taskRepository.sendComplete(EVENT_UPDATE);
    }

    public boolean deleteImageForUri(Uri uri) {
        File fileUri = new File(uri.getPath());
        String nameFile = fileUri.getName();
        File file = new File(FILE_PATH_DCIM, nameFile);
        return file.exists() && file.delete();
    }

    public HashMap<Integer, Uri> getAddedImages() {
        return null;
    }

    public void deleteImages() {
//        for (Map.Entry<Integer, Uri> i : addedImages.entrySet()) {
//            boolean fileDeleted = deleteImageForUri(i.getValue());
//            if (fileDeleted)
//                addedImages.remove(i.getKey());
//        }
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        if (eventString.equals(EVENT_UPDATE)) {
            int code = (int) params[1];
            if (code == 200) {
                String content = String.valueOf(params[0]);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int status = jsonObject.getInt("status");
                    isDone.postValue(status == 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            isSend.postValue(false);
        }
    }

}
