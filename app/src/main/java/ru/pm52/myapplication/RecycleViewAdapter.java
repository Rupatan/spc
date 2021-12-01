package ru.pm52.myapplication;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.databinding.ActivityTaskRecycleViewItemBinding;


public class RecycleViewAdapter
        extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolderBinding>
        implements View.OnClickListener {

    private List<TaskModel> list;
    @Nullable
    private IRecycleViewItemClick objectItemClick;

    public RecycleViewAdapter(List<TaskModel> listTask) {
        this(listTask, null);
    }

    public RecycleViewAdapter(List<TaskModel> listTask, @Nullable IRecycleViewItemClick objectItemClick) {
        this.objectItemClick = objectItemClick;
        setList(listTask);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<TaskModel> listTask) {
        this.list = listTask;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderBinding onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityTaskRecycleViewItemBinding view = ActivityTaskRecycleViewItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        view.getRoot().setOnClickListener(this);
        return new ViewHolderBinding(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBinding holder, int position) {
        final TaskModel taskModel = list.get(position);
        if (taskModel != null) {
            ActivityTaskRecycleViewItemBinding b = holder.binding;
            b.getRoot().setTag(taskModel);
            b.Name.setText(taskModel.Name);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            b.DatePerfomance.setText(formatter.format(taskModel.DateTime));
            b.Number.setText(taskModel.Number);
            b.Contact.setText(taskModel.Contact);
            b.Contragent.setText(taskModel.Contragent);
            b.Description.setText(taskModel.Descritpion);

            Date date = new Date();
            if (date.getTime() >= taskModel.DatePerfomance.getTime()) {
                b.DatePerfomance.setTextColor(Color.RED);
            }

            b.imagePlusMinus.setTag(false);
            b.imagePlusMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView txt = b.imagePlusMinus;
                    boolean isVisible = ((boolean) txt.getTag());
                    b.Description.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                    b.imagePlusMinus.setTag(!isVisible);

                    txt.setText(isVisible ? "+" : "-");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        if (objectItemClick != null) {
            TaskModel model = (TaskModel) view.getTag();
            objectItemClick.onItemClick(model, view);

        } else if (view.getId() == R.id.imagePlusMinus) {

        }
    }


    public class ViewHolderBinding extends RecyclerView.ViewHolder {

        protected ActivityTaskRecycleViewItemBinding binding;

        public ViewHolderBinding(@NonNull ActivityTaskRecycleViewItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }
    }

}

