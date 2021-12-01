package ru.pm52.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.databinding.ActivityMainBinding;
import ru.pm52.myapplication.databinding.ActivityTaskRecycleViewItemBinding;
import ru.pm52.myapplication.screens.AuthFragment;
import ru.pm52.myapplication.screens.TaskFragment;

public class MainActivity extends AppCompatActivity implements Navigator {

    interface ICallBackPress{
        public void CallBackPress();
    }

    private List<ICallBackPress> callBackPresses = new ArrayList<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
//        setSupportActionBar(toolbar);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new AuthFragment())
                    .commit();

    }

    @Override
    public void showDetails(TaskModel task, @Nullable INotify callback) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TaskFragment(task))
                .addToBackStack(null)
                .commit();
    }

    public void addListenerCallbackPress(ICallBackPress object){
        callBackPresses.add(object);
    }

    public void removeListenerCallbackPress(ICallBackPress object){
        callBackPresses.remove(object);
    }

    @Override
    public void onBackPressed() {
        for (ICallBackPress v : callBackPresses)
            v.CallBackPress();

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callBackPresses.clear();
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void toast(int messageRes) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

}