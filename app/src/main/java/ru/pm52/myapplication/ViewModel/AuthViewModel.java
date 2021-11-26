package ru.pm52.myapplication.ViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.pm52.myapplication.Model.AuthModel;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.TaskModel;

public class AuthViewModel extends ViewModelBase {

    private final AuthRepository repository;

    private final MutableLiveData<AuthModel> _authModel = new MutableLiveData<>();
    public LiveData<AuthModel> authModel = _authModel;

    private final MutableLiveData<Boolean> _isLogin = new MutableLiveData<>();
    public final LiveData<Boolean> isLogin = _isLogin;

    private final MutableLiveData<List<TaskModel>> listTasks = new MutableLiveData<>();
    public final LiveData<List<TaskModel>> ListTasks = listTasks;

    public LiveData<Boolean> getIsLogin() {
        return isLogin;
    }

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public void login(String username, String password, @Nullable String nameEvent) {
        repository.login(username, password, nameEvent, this);
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        if (eventString.equals("login")) {
            int code = (int) params[1];
            if (code == 200) {
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

                        _isLogin.postValue(true);
                        listTasks.postValue(lst);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    _isLogin.postValue(false);
                }
            }else{
                _isLogin.postValue(false);
            }
        }
    }
}
