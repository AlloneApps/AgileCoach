package com.task.agilecoach.views.myTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTasksAdapter extends RecyclerView.Adapter<MyTasksAdapter.PickUpRequestsViewHolder> {

    private static final String TAG = MyTasksAdapter.class.getSimpleName();
    // region Variable declarations

    /**
     * ArrayList of type TaskMaster
     */
    private List<TaskMaster> myTaskaMainList;
//    private TaskMaster deliveryMasterMain;

    /**
     * Context
     */
    private Context context;

    private MyTasksItemClickListener listener;

    private String tripStatus;

    // endregion

    public MyTasksAdapter(Context context, List<TaskMaster> myTaskaMainList, MyTasksItemClickListener listener) {
        this.context = context;
        this.myTaskaMainList = myTaskaMainList;
        this.listener = listener;
    }

    @Override
    public MyTasksAdapter.PickUpRequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_tasks, parent, false);
        return new MyTasksAdapter.PickUpRequestsViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyTasksAdapter.PickUpRequestsViewHolder holder, int position) {
        try {
            holder.setItem(myTaskaMainList.get(position));

            TaskMaster taskMaster = myTaskaMainList.get(position);
            if (taskMaster != null) {

                User loginUser = Utils.getLoginUserDetails(context);

                if(loginUser != null){
                    if(loginUser.getGender().equals(AppConstants.MALE_GENDER)){
                        holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_male_avatar));
                    }else if(loginUser.getGender().equals(AppConstants.FEMALE_GENDER)){
                        holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_female_avatar));
                    }else{
                        holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_male_avatar));
                    }
                }

                String taskNumber = taskMaster.getTaskMasterId();
                holder.textTaskNumber.setText(taskNumber);

                holder.textTaskHeader.setText(taskMaster.getTaskHeader());

                holder.textTaskType.setText(taskMaster.getTaskType());

                if(taskMaster.getTaskType().equals(AppConstants.BUG_TYPE)){
                    holder.textTaskType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_red, 0, 0, 0);
                    holder.textTaskType.setCompoundDrawablePadding((int)context.getResources().getDimension(R.dimen.std_10_dp));
                    holder.textTaskType.setTextColor(context.getResources().getColor(R.color.error_color,null));
                }else{
                    holder.textTaskType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_green, 0, 0, 0);
                    holder.textTaskType.setCompoundDrawablePadding((int)context.getResources().getDimension(R.dimen.std_10_dp));
                    holder.textTaskType.setTextColor(context.getResources().getColor(R.color.success_color,null));
                }

                int lastPosition = (taskMaster.getTasksSubDetailsList().size() - 1);

                Log.d(TAG, "onBindViewHolder: lastPosition: "+lastPosition);

                if(lastPosition >= 0){
                    String lastStatus = taskMaster.getTasksSubDetailsList().get(lastPosition).getTaskStatus();
                    Log.d(TAG, "onBindViewHolder: lastStatus: "+lastStatus);
                    holder.textTaskStatus.setText(lastStatus);
                    holder.textTaskStatus.setTextColor(context.getResources().getColor(R.color.success_color,null));
                }

                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.viewTasks(position,taskMaster);
                    }
                });

                holder.textUpdateStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.changeStatus(position,taskMaster);
                    }
                });
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
        return myTaskaMainList.size();
    }

    public interface MyTasksItemClickListener {
        void viewTasks(int position, TaskMaster taskMaster);

        void changeStatus(int position, TaskMaster taskMaster);
    }

    static class PickUpRequestsViewHolder extends RecyclerView.ViewHolder {
        TaskMaster deliveryMasterMain;
        TextView textTaskNumber, textTaskHeader, textTaskStatus, textView, textUpdateStatus,textTaskType;
        CircleImageView imageUserAvatar;


        PickUpRequestsViewHolder(View itemView) {
            super(itemView);
            textTaskNumber = itemView.findViewById(R.id.text_task_number);
            textTaskHeader = itemView.findViewById(R.id.text_task_header);
            textTaskStatus = itemView.findViewById(R.id.text_task_status);

            imageUserAvatar = itemView.findViewById(R.id.image_user);

            textView = itemView.findViewById(R.id.text_view);
            textUpdateStatus = itemView.findViewById(R.id.text_update_status);
            textTaskType = itemView.findViewById(R.id.text_task_type);
        }

        public void setItem(TaskMaster item) {
            deliveryMasterMain = item;
        }
    }
}
