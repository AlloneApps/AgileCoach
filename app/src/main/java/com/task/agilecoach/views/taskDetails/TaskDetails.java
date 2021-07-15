package com.task.agilecoach.views.taskDetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.model.TaskMaster;
import com.task.agilecoach.model.TasksSubDetails;
import com.task.agilecoach.model.User;

import java.util.ArrayList;
import java.util.List;

public class TaskDetails extends AppCompatActivity {

    private static final String TAG = "TaskDetails";
    public static final String TASK_MASTER_DETAILS = "Task Master Details";

    private RecyclerView recyclerTaskDetails;
    private Button btnBack;
    private TaskMaster taskMaster = null;
    private List<TasksSubDetails> tasksSubDetailsList = new ArrayList<>();
    private TaskViewDetailsAdapter taskViewDetailsAdapter;

    TextView textTaskNumber, textTaskHeader, textTaskDesc,textTaskStatus, textView, textUpdateStatus, textTaskType, textAssignTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_task_details);

            if (getIntent() != null) {
                taskMaster = getIntent().getParcelableExtra(TASK_MASTER_DETAILS);
            }

            setUpViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            recyclerTaskDetails = findViewById(R.id.recycler_loan_details);
            btnBack = findViewById(R.id.btn_back);

            textTaskNumber = findViewById(R.id.text_task_number);
            textTaskHeader = findViewById(R.id.text_task_header);
            textTaskDesc = findViewById(R.id.text_task_description);
            textTaskStatus = findViewById(R.id.text_task_status);

            textView = findViewById(R.id.text_view);
            textUpdateStatus = findViewById(R.id.text_update_status);
            textTaskType = findViewById(R.id.text_task_type);
            textAssignTo = findViewById(R.id.text_assign_to);

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });

            if (taskMaster != null) {

                User loginUser = Utils.getLoginUserDetails(TaskDetails.this);

                String taskNumber = taskMaster.getTaskMasterId();
                textTaskNumber.setText(taskNumber);

                textTaskHeader.setText(taskMaster.getTaskHeader());
                textTaskDesc.setText(taskMaster.getTaskDescription());

                textTaskType.setText(taskMaster.getTaskType());

                if (taskMaster.getTaskType().equals(AppConstants.BUG_TYPE)) {
                    textTaskType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_red, 0, 0, 0);
                    textTaskType.setCompoundDrawablePadding((int) TaskDetails.this.getResources().getDimension(R.dimen.std_10_dp));
                    textTaskType.setTextColor(TaskDetails.this.getResources().getColor(R.color.error_color, null));
                } else {
                    textTaskType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task_or_bug_green, 0, 0, 0);
                    textTaskType.setCompoundDrawablePadding((int) TaskDetails.this.getResources().getDimension(R.dimen.std_10_dp));
                    textTaskType.setTextColor(TaskDetails.this.getResources().getColor(R.color.success_color, null));
                }

                tasksSubDetailsList.clear();
                tasksSubDetailsList = taskMaster.getTasksSubDetailsList();
                if (tasksSubDetailsList.size() > 0) {
                    int lastPosition = (tasksSubDetailsList.size() - 1);

                    Log.d(TAG, "onBindViewHolder: lastPosition: " + lastPosition);

                    String lastStatus = tasksSubDetailsList.get(lastPosition).getTaskStatus();
                    Log.d(TAG, "onBindViewHolder: lastStatus: " + lastStatus);
                    textTaskStatus.setText(lastStatus);
                    textTaskStatus.setTextColor(TaskDetails.this.getResources().getColor(R.color.success_color, null));

                    String assignedTo = tasksSubDetailsList.get(lastPosition).getTaskUserAssigned();
                    textAssignTo.setText(assignedTo);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TaskDetails.this);
                    recyclerTaskDetails.setLayoutManager(linearLayoutManager);
                    taskViewDetailsAdapter = new TaskViewDetailsAdapter(TaskDetails.this, tasksSubDetailsList);
                    recyclerTaskDetails.setAdapter(taskViewDetailsAdapter);
                    taskViewDetailsAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}