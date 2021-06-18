package com.task.agilecoach.views.allUsers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllTasksViewHolder> {

    private static final String TAG = AllUsersAdapter.class.getSimpleName();
    // region Variable declarations

    /**
     * ArrayList of type TaskMaster
     */
    private List<User> allUsersMainList;
//    private TaskMaster deliveryMasterMain;

    /**
     * Context
     */
    private Context context;

    private AllUsersItemClickListener listener;

    private String tripStatus;

    // endregion

    public AllUsersAdapter(Context context, List<User> allUsersMainList, AllUsersItemClickListener listener) {
        this.context = context;
        this.allUsersMainList = allUsersMainList;
        this.listener = listener;
    }

    @Override
    public AllUsersAdapter.AllTasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_users, parent, false);
        return new AllUsersAdapter.AllTasksViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final AllUsersAdapter.AllTasksViewHolder holder, int position) {
        try {
            holder.setItem(allUsersMainList.get(position));

            User userMain = allUsersMainList.get(position);
            if (userMain != null) {
                if (userMain.getGender().equals(AppConstants.MALE_GENDER)) {
                    holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_male_avatar));
                } else if (userMain.getGender().equals(AppConstants.FEMALE_GENDER)) {
                    holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_female_avatar));
                } else {
                    holder.imageUserAvatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_male_avatar));
                }

                String userName = userMain.getFirstName() + " " + userMain.getLastName();
                holder.textUserName.setText(userName);

                String mobileNumber = userMain.getMobileNumber();
                holder.textMobileNumber.setText(mobileNumber);

                String userActiveStatus = "";

                boolean isActive = true;
                if (userMain.getIsActive().equals("false")) {
                    isActive = false;
                }

                if (isActive) {
                    userActiveStatus = AppConstants.ACTIVE_USER;
                    holder.textMakeInActive.setText("InActive");
                } else {
                    userActiveStatus = AppConstants.IN_ACTIVE_USER;
                    holder.textMakeInActive.setText("Active");
                }
                holder.textUserActiveStatus.setText(userActiveStatus);

                if (userActiveStatus.equals(AppConstants.ACTIVE_USER)) {
                    holder.textUserActiveStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active_user, 0, 0, 0);
                    holder.textUserActiveStatus.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.std_10_dp));
                    holder.textUserActiveStatus.setTextColor(context.getResources().getColor(R.color.success_color, null));
                } else {
                    holder.textUserActiveStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unactive_user, 0, 0, 0);
                    holder.textUserActiveStatus.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.std_10_dp));
                    holder.textUserActiveStatus.setTextColor(context.getResources().getColor(R.color.error_color, null));
                }

                holder.textUserDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.userDetails(position, userMain);
                    }
                });

                holder.textMakeInActive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.makeInActiveUser(position, userMain);
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
        return allUsersMainList.size();
    }

    public interface AllUsersItemClickListener {
        void userDetails(int position, User userDetails);

        void makeInActiveUser(int position, User userDetails);
    }

    static class AllTasksViewHolder extends RecyclerView.ViewHolder{
        User user;
        TextView textUserName, textMobileNumber, textUserActiveStatus, textUserDetails, textMakeInActive;
        CircleImageView imageUserAvatar;


        AllTasksViewHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textMobileNumber = itemView.findViewById(R.id.text_mobile_number);
            textUserActiveStatus = itemView.findViewById(R.id.text_active);

            imageUserAvatar = itemView.findViewById(R.id.image_user);

            textUserDetails = itemView.findViewById(R.id.text_user_details);
            textMakeInActive = itemView.findViewById(R.id.text_in_active_user);
        }

        public void setItem(User item) {
            user = item;
        }
    }
}
