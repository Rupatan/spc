package ru.pm52.myapplication.ViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.pm52.myapplication.Model.AuthModel;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Model.TypeWork;

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
                    Type listType = new TypeToken<ArrayList<TaskModel>>() {
                    }.getType();
                    JSONObject json = new JSONObject(stringJson);
                    if (json.getInt("status") == 1) {
                        JSONObject dataObject = json.getJSONObject("data");
                        String stringTasks = dataObject.getJSONArray("Задачи").toString();

                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create();

                        Object objectListTypeWork = dataObject.opt("ВидыРабот");
                        if (objectListTypeWork instanceof JSONArray) {
                            JSONArray listTypeWork = (JSONArray) objectListTypeWork;
                            for (int i = 0; i < listTypeWork.length(); i++) {
                                JSONObject jObject = listTypeWork.getJSONObject(i);
                                ModelContext.typeWorkList.add(new TypeWork(jObject.getString("Ссылка"), jObject.getString("Наименование")));
                            }
                        }
                        //getJSONArray("ВидыРабот").toString();
//                        if (!stringTypeWorks.isEmpty()) {
//                            ModelContext.typeWorkList.addAll(gson.fromJson(stringTypeWorks, new TypeToken<List<TypeWork>>() {
//                            }.getType()));
//                        }

                        List<TaskModel> lst = gson.fromJson(stringTasks, listType);

                        _isLogin.postValue(true);
                        listTasks.postValue(lst);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    _isLogin.postValue(false);
                }
            } else {
                _isLogin.postValue(false);
            }
        }
    }
}
