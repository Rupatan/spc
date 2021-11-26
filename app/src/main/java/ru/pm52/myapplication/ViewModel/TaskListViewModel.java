package ru.pm52.myapplication.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;

public class TaskListViewModel extends ViewModelBase{

    private final MutableLiveData<List<TaskModel>> listTasks = new MutableLiveData<>();
    public final LiveData<List<TaskModel>> ListTasks = listTasks;

    private final MutableLiveData<Boolean> isRefresh = new MutableLiveData<>();
    public final LiveData<Boolean> IsRefresh = isRefresh;

    public TaskListViewModel(){
        
    }

    public void setListTasks(List<TaskModel> list){
        listTasks.postValue(list);
    }

    public List<TaskModel> getTaskModelList(){
        return listTasks.getValue();
    }

    public void refresh(){
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString().replace('-', '_');
        AuthRepository authRepository = AuthRepository.getInstance();

        HTTPClient client = new HTTPClient.Builder(ModelContext.URLBase)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT")
                .addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                .addHeader("Cache-Control", "post-check=0, pre-check=0")
                .addHeader("Pragma", "no-cache")
                .authentication(authRepository.getUsername(), authRepository.getPassword())
                .pathURL("mobile/tasks/getlist?uid=" + uuidAsString)
                .method(HTTPClient.METHOD_SEND.POST)
                .callback(this)
                .build();

        client.setNameEvent("getlist").sendAsync();
        isRefresh.postValue(true);
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        if (eventString.equals("getlist")){
            String body = params[0].toString();
            int code = (int)params[1];
            if (code == 200){
                String stringJson = params[0].toString();
                try {
                    Type listType = new TypeToken<ArrayList<TaskModel>>(){}.getType();
                    JSONObject json = new JSONObject(stringJson);
                    if (json.getInt("status") == 1) {
                        String stringTasks = json.getJSONObject("data").getJSONArray("Задачи").toString();

                        List<TaskModel> lst = new GsonBuilder()
                                .setPrettyPrinting()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create()
                                .fromJson(stringTasks, listType);

                        listTasks.postValue(lst);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        isRefresh.postValue(false);
    }
}
