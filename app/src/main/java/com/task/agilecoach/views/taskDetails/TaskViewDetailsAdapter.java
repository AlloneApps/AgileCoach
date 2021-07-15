package com.task.agilecoach.views.taskDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.task.agilecoach.R;
import com.task.agilecoach.model.TasksSubDetails;

import java.util.List;

public class TaskViewDetailsAdapter extends RecyclerView.Adapter<TaskViewDetailsAdapter.TaskSubDetailsViewAdapterViewHolder> {

    private static final String TAG = TaskViewDetailsAdapter.class.getSimpleName();
    /**
     * ArrayList of type PlaceItem
     */
    private List<TasksSubDetails> tasksSubDetailsList;
    private Context context;

    // endregion

    public TaskViewDetailsAdapter(Context context, List<TasksSubDetails> tasksSubDetailsList) {
        this.context = context;
        this.tasksSubDetailsList = tasksSubDetailsList;
    }

    @Override
    public TaskSubDetailsViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_details_sub, parent, false);
        return new TaskSubDetailsViewAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TaskSubDetailsViewAdapterViewHolder holder, int position) {
        try {
            if (tasksSubDetailsList.size() > 0) {
                TasksSubDetails tasksSubDetails = tasksSubDetailsList.get(position);
                holder.setItem(tasksSubDetails);

                int sequenceNumber = position + 1;
                String seqNumberString = String.valueOf(sequenceNumber);
                if (sequenceNumber < 10) {
                    seqNumberString = "0" + sequenceNumber;
                }

                holder.textSequenceNumber.setText(seqNumberString);

                holder.textAssignedDate.setText(tasksSubDetails.getModifiedOn());
                holder.textAssignedStatus.setText(tasksSubDetails.getTaskStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return tasksSubDetailsList.size();
    }

    static class TaskSubDetailsViewAdapterViewHolder extends RecyclerView.ViewHolder {

        TasksSubDetails taskSubDetails;

        TextView textSequenceNumber, textAssignedDate, textAssignedStatus;

        TaskSubDetailsViewAdapterViewHolder(View itemView) {
            super(itemView);
            // Text View
            textSequenceNumber = itemView.findViewById(R.id.sequence_number);
            textAssignedDate = itemView.findViewById(R.id.text_assigned_date);
            textAssignedStatus = itemView.findViewById(R.id.text_assign_status);
        }

        public void setItem(TasksSubDetails item) {
            try {
                taskSubDetails = item;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
