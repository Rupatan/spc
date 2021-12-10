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

import ru.pm52.myapplication.App;
import ru.pm52.myapplication.Model.AuthModel;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Model.TypeWork;
import ru.pm52.myapplication.Model.UserModel;

public class AuthViewModel extends ViewModelBase {

    public static final String NAME_EVENT_LOGIN = "login";

    private final AuthRepository repository;

    private final MutableLiveData<AuthModel> _authModel = new MutableLiveData<>();
    public LiveData<AuthModel> authModel = _authModel;

    private final MutableLiveData<Boolean> _isLogin = new MutableLiveData<>();
    public final LiveData<Boolean> isLogin = _isLogin;

    private final MutableLiveData<List<TaskModel>> listTasks = new MutableLiveData<>();
    public final LiveData<List<TaskModel>> ListTasks = listTasks;

    private final MutableLiveData<String> messageLogin = new MutableLiveData<>();
    public final LiveData<String> MessageLogin = messageLogin;

    public LiveData<Boolean> getIsLogin() {
        return isLogin;
    }

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
        this.repository.addListener(this, NAME_EVENT_LOGIN);
    }

    public String getDatabase(){
        return repository.getDataBase();
    }

    public String getServer(){
        return repository.getServer();
    }

    public String getLogin(){
        return repository.getUsername();
    }

    public void login(String username, String password, String _database, String _server) throws Exception {
        repository.login(username, password, _database, _server, NAME_EVENT_LOGIN);
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        if (eventString.equals(NAME_EVENT_LOGIN)) {
            int code = (int) params[1];
            if (code == 200) {
                String stringJson = params[0].toString();
                try {
                    Type listType = new TypeToken<ArrayList<TaskModel>>() {
                    }.getType();
                    JSONObject json = new JSONObject(stringJson);
                    if (json.getInt("status") == 1) {
                        JSONObject dataObject = json.getJSONObject("data");
//                        String stringTasks = dataObject.getJSONArray("Задачи").toString();

                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create();

                        UserModel user = gson.fromJson(dataObject.getJSONObject("Пользователь").toString(),
                                UserModel.class);

                        App.setUser(user);

                        Object objectListTypeWork = dataObject.opt("ВидыРабот");
                        if (objectListTypeWork instanceof JSONArray) {
                            JSONArray listTypeWork = (JSONArray) objectListTypeWork;
                            for (int i = 0; i < listTypeWork.length(); i++) {
                                JSONObject jObject = listTypeWork.getJSONObject(i);
                                ModelContext.typeWorkList.add(new TypeWork(jObject.getString("Ссылка"), jObject.getString("Наименование")));
                            }
                        }
//                        List<TaskModel> lst = gson.fromJson(stringTasks, listType);

                        _isLogin.postValue(true);
//                        listTasks.postValue(lst);
                    }else{
                        JSONObject jsonObject = new JSONObject((String) params[0]);
                        String msg = jsonObject.getString("info");

                        messageLogin.postValue(msg);
                        _isLogin.postValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    _isLogin.postValue(false);
                }
            } else {
                if (code == -1)
                    messageLogin.postValue((String) params[0]);

                _isLogin.postValue(false);

            }

        }
    }
}
