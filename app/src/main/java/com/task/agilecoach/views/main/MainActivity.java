package com.task.agilecoach.views.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.agilecoach.BuildConfig;
import com.task.agilecoach.R;
import com.task.agilecoach.helpers.AppConstants;
import com.task.agilecoach.helpers.FireBaseDatabaseConstants;
import com.task.agilecoach.helpers.NetworkUtil;
import com.task.agilecoach.helpers.Utils;
import com.task.agilecoach.helpers.myTaskToast.MyTasksToast;
import com.task.agilecoach.model.User;
import com.task.agilecoach.views.allTasks.AllTasks;
import com.task.agilecoach.views.allUsers.AllUsers;
import com.task.agilecoach.views.createTasks.CreateTasks;
import com.task.agilecoach.views.dashboard.AdminDashboard;
import com.task.agilecoach.views.dashboard.UserDashboard;
import com.task.agilecoach.views.login.LoginActivity;
import com.task.agilecoach.views.myTasks.MyTasks;
import com.task.agilecoach.views.profile.Profile;
import com.task.agilecoach.views.updateMpin.UpdateMPin;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private ImageView menuIcon;
    private TextView navUserName, navUserId;
    private TextView textTitle;
    private CircleImageView navImageUser;
    private View navHeader;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);

            menuIcon = findViewById(R.id.menu_icon);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            TextView title = appBarLayout.findViewById(R.id.title);
            textTitle = findViewById(R.id.title_header);
            textTitle.setVisibility(View.VISIBLE);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                    hideKeyboard();
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                    hideKeyboard();
                    super.onDrawerOpened(drawerView);
                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });


            User loginUser = Utils.getLoginUserDetails(MainActivity.this);
            Log.d(TAG, "onCreate: loginUser: " + loginUser);

            NavigationView navigationView = findViewById(R.id.nav_view);
            if (loginUser != null) {
                if (loginUser.getRole().equals(AppConstants.ADMIN_ROLE)) {
                    navigationView.inflateMenu(R.menu.activity_main_drawer_admin);
                } else {
                    navigationView.inflateMenu(R.menu.activity_main_drawer_user);
                }
            } else {
                navigationView.inflateMenu(R.menu.activity_main_drawer_user);
            }

            navigationView.setNavigationItemSelectedListener(this);

            navHeader = navigationView.getHeaderView(0);
            navUserName = navHeader.findViewById(R.id.text_user_name);
            navUserId = navHeader.findViewById(R.id.text_user_id);
            navImageUser = navHeader.findViewById(R.id.image_user_avatar);


            if (loginUser != null) {
                String userName = "";
                if (loginUser.getRole().equals(AppConstants.ADMIN_ROLE)) {
//                    userName = loginUser.getFirstName() + " (" + loginUser.getRole() + ")";
                    userName = loginUser.getFirstName() + " " + loginUser.getLastName() + " (" + loginUser.getRole() + ")";
                } else {
                    userName = loginUser.getFirstName() + " " + loginUser.getLastName() + " (" + loginUser.getRole() + ")";
                }
                navUserName.setText(userName);
                navUserId.setText(loginUser.getEmailId());

                setProfileAvatar(loginUser);

                verifyUserActiveStatus(loginUser);
            }

            TextView textVersion = findViewById(R.id.internal_version);

            String version = "v" + BuildConfig.VERSION_NAME;
            textVersion.setText(version);
            LinearLayout layoutFooter = findViewById(R.id.layout_logout);

            layoutFooter.setOnClickListener(view -> {
                if (NetworkUtil.getConnectivityStatus(MainActivity.this)) {
                    if (drawer != null) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    logout();
                } else {
                    MyTasksToast.showErrorToastWithBottom(MainActivity.this, "No Internet Connection.", MyTasksToast.MYTASKS_TOAST_LENGTH_SHORT);
                }
            });

            if (loginUser != null) {
                if (loginUser.getRole().equals(AppConstants.ADMIN_ROLE)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, new AdminDashboard()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, new UserDashboard()).commit();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyUserActiveStatus(User user) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            databaseReference.child(user.getMobileNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String isActiveStatus = snapshot.child(FireBaseDatabaseConstants.IS_ACTIVE).getValue(String.class);
                        Log.d(TAG, "onDataChange: isActiveStatus: " + isActiveStatus);
                        if (isActiveStatus != null) {
                            if (isActiveStatus.equals("false")) {
                                MyTasksToast.showErrorToastWithBottom(MainActivity.this, "You are DeActivated now, Please contact your admin", MyTasksToast.MYTASKS_TOAST_LENGTH_LONG);
                                logout();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String titleName) {
        try {
            Log.d(TAG, "setTitle: titleName: " + titleName);
            if (textTitle != null) {
                textTitle.setVisibility(View.VISIBLE);
                Log.d(TAG, "setTitle: title setted");
                textTitle.setText(titleName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProfileAvatar(User user) {
        try {
            if (user != null && navImageUser != null) {
                if (user.getGender().equals(AppConstants.FEMALE_GENDER)) {
                    navImageUser.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_female_avatar));
                } else {
                    navImageUser.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_male_avatar));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            Utils.removeAllDataWhenLogout(MainActivity.this);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.findViewById(R.id.toolbar).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        try {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            fragmentManager = getSupportFragmentManager();
            if (id == R.id.nav_create_task) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new CreateTasks(), Integer.toString(getFragmentCount())).commit();
            } else if (id == R.id.nav_all_tasks) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new AllTasks(), Integer.toString(getFragmentCount())).commit();
            } else if (id == R.id.nav_my_tasks) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new MyTasks(), Integer.toString(getFragmentCount())).commit();
            } else if (id == R.id.nav_all_users) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new AllUsers(), Integer.toString(getFragmentCount())).commit();
            } else if (id == R.id.nav_profile) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new Profile(), Integer.toString(getFragmentCount())).commit();
            } else if (id == R.id.nav_update_mpin) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new UpdateMPin(), Integer.toString(getFragmentCount())).commit();
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            Log.d(TAG, "onBackPressed: ");
            fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout_main);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (fragment instanceof UserDashboard) {
                moveTaskToBack(true);
            } else if (fragment instanceof AdminDashboard) {
                moveTaskToBack(true);
            } else if (fragment instanceof CreateTasks) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new AdminDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else if (fragment instanceof AllTasks) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new AdminDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else if (fragment instanceof AllUsers) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new AdminDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else if (fragment instanceof MyTasks) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new UserDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else if (fragment instanceof Profile) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new UserDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else if (fragment instanceof UpdateMPin) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_main, new UserDashboard(),
                        Integer.toString(getFragmentCount())).commit();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getFragmentCount() {
        try {
            return getSupportFragmentManager().getBackStackEntryCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}