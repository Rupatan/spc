package ru.pm52.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new AuthFragment())
                    .commit();

    }

    @Override
    public void showDetails(TaskModel task) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TaskFragment())
                .addToBackStack(null)
                .commit();
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