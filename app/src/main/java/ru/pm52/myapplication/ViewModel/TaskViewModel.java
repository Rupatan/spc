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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.pm52.myapplication.Model.TaskModel;

public class TaskViewModel extends ViewModelBase {

    private MutableLiveData<TaskModel> task = new MutableLiveData<>();
    public LiveData<TaskModel> Task = task;

    private HashMap<Integer, Uri> addedImages = new HashMap<>();
    private Context context;

    public TaskViewModel(Context context) {
        this.context = context;
    }

    public void setTask(TaskModel taskModel) {
        task.postValue(taskModel);
    }

    public TaskModel getTaskModel() {
        return task.getValue();
    }

    public void setAddedImages(Integer id, Uri uri) {
        this.addedImages.put(id, uri);
    }

    public boolean deleteImage(int id) {
        Uri uri = addedImages.get(id);
        boolean fileRemoved = deleteImageForUri(uri);
        if (fileRemoved)
            addedImages.remove(id);
        return fileRemoved;
    }

    public boolean deleteImage(Map.Entry<Integer, Uri> i) {
        return deleteImage(i.getKey());
    }

    public boolean deleteImageForUri(Uri uri) {
        File fileUri = new File(uri.getPath());
        String nameFile = fileUri.getName();
        File file = new File(FILE_PATH_DCIM, nameFile);
        return file.exists() && file.delete();
    }

    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                result = "";
            }
            cursor.close();
        }
        return result;
    }

    public void deleteImages() {
        for (Map.Entry<Integer, Uri> i : addedImages.entrySet()) {
            boolean fileDeleted = deleteImageForUri(i.getValue());
            if (fileDeleted)
                addedImages.remove(i.getKey());
        }
    }
}
