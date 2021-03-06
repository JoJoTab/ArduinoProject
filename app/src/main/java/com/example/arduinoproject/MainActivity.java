package com.example.arduinoproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.arduinoproject.fragment.BoardFragment;
import com.example.arduinoproject.fragment.NoticeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BoardFragment boardFragment;
    private NoticeFragment noticeFragment;

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardFragment = new BoardFragment();
        noticeFragment = new NoticeFragment();

        bottomNavigation = findViewById(R.id.navigationView);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.page_home :
                        fragmentManager.beginTransaction().replace(R.id.fragmentLayout,  boardFragment).commit();
                        break;
                    case R.id.page_notice :
                        fragmentManager.beginTransaction().replace(R.id.fragmentLayout,  noticeFragment).commit();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        passPushTokenToServer();
    }
    void passPushTokenToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                Map<String, Object> map = new HashMap<>();
                map.put("pushToken", token);

                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
            }
        });
    }



    // ???????????? ?????? ??????????????? ?????? long ??????
    private long pressedTime = 0;

    // ????????? ??????
    public interface OnBackPressedListener {
        public void onBack();
    }

    // ????????? ?????? ??????
    private OnBackPressedListener mBackListener;

    // ????????? ?????? ?????????
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    // ???????????? ????????? ????????? ?????? ??????????????? ?????????
    @Override
    public void onBackPressed() {

        // ?????? Fragment ?????? ???????????? ???????????? ??? ???????????????.
        if(mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // ???????????? ???????????? ?????? ??????(???????????? ??????Fragment)??????
            // ???????????? ????????? ??????????????? ?????? ????????? ??? ?????? ???????????????.
        } else {
            Log.e("!!!", "Listener is null");
            if ( pressedTime == 0 ) {
                Toast.makeText(MainActivity.this," ??? ??? ??? ????????? ???????????????." ,Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            }
            else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if ( seconds > 2000 ) {
                    Toast.makeText(MainActivity.this," ??? ??? ??? ????????? ???????????????." ,Toast.LENGTH_SHORT).show();
                    pressedTime = 0 ;
                }
                else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess");
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }
}