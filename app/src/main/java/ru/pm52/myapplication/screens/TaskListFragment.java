package ru.pm52.myapplication.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ru.pm52.myapplication.App;
import ru.pm52.myapplication.FragmentBase;
import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.IRecycleViewItemClick;
import ru.pm52.myapplication.MainActivity;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Navigator;
import ru.pm52.myapplication.R;
import ru.pm52.myapplication.RecycleViewAdapter;
import ru.pm52.myapplication.ViewModel.TaskListViewModel;
import ru.pm52.myapplication.databinding.FragmentTaskListBinding;

public class TaskListFragment extends FragmentBase implements IRecycleViewItemClick {

    private FragmentTaskListBinding binding;
    private TaskListViewModel viewModel;

    private boolean isNew = false;
    private boolean isRefresh = false;
    private boolean isCallbackPress = false;

    public TaskListFragment(@Nullable List<TaskModel> list) {
        isNew = true;
    }

    public TaskListFragment() {
        this(null);

        setHasOptionsMenu(App.getUser().IsProgrammist);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).addListenerCallbackPress(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        binding = FragmentTaskListBinding.inflate(inflater, container, false);

        if (!isCallbackPress) {
            binding.swipeContainer.setRefreshing(true);

            viewModel.refresh();
        }
//        else if (savedInstanceState == null) {
//            viewModel.setListTasks(listTask);
//        } else listTask = viewModel.getTaskModelList();

        viewModel.ListTasks.observe(getViewLifecycleOwner(), new Observer<List<TaskModel>>() {
            @Override
            public void onChanged(List<TaskModel> taskModels) {
                RecycleViewAdapter adapter = (RecycleViewAdapter) binding.listTask.getAdapter();
                if (adapter == null)
                    binding.listTask.setAdapter(new RecycleViewAdapter(taskModels, TaskListFragment.this));
                else
                    adapter.setList(taskModels);
            }
        });

        viewModel.IsRefresh.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean)
                    binding.swipeContainer.setRefreshing(false);
            }
        });

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (App.getUser().IsProgrammist)
            inflater.inflate(R.menu.menu_list_task_programmist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemComplete: {

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isNew = false;

    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).removeListenerCallbackPress(this);
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onItemClick(TaskModel model, View view) {
        ((Navigator) getActivity()).showDetails(model, this);
        isCallbackPress = false;
    }

    @Override
    public void CallBackPress() {
        isCallbackPress = true;
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        //if ()
    }
}
