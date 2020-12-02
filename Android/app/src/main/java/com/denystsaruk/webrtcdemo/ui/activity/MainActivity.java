package com.denystsaruk.webrtcdemo.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.denystsaruk.webrtcdemo.R;
import com.denystsaruk.webrtcdemo.utility.MessageUtil;
import com.denystsaruk.webrtcdemo.webrtc.SignallingClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SignallingClient.SignalingInterface{
    public static MainActivity instance;
    public static final int REQUEST_PERMISSION = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };
    public static boolean isFirst = true;

    @BindView(R.id.txt_socket_url)
    EditText txt_socket_url;

    @BindView(R.id.txt_my_id)
    EditText txt_my_id;

    @BindView(R.id.txt_friend_id)
    EditText txt_friend_id;

    private boolean isRegistered =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;
/////////////////   TEST VALUE   ///////////////////////////////////////////////////////
//        txt_socket_url.setText("http://10.70.3.112:8080");
        txt_socket_url.setText("https://webrtcsignaling.herokuapp.com/");
//        txt_my_id.setText("aaa");
//        txt_friend_id.setText("bbb");

//        txt_my_id.setText("bbb");
//        txt_friend_id.setText("aaa");

//        onRegister();
////////////////    TEST VALUE   //////////////////////////////////////////////////////////
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            verifyStoragePermissions(this);
        }

        SignallingClient.getInstance().callback = this;
    }
    @OnClick(R.id.btn_register)
    public void onRegister(){
        if(txt_socket_url.getText().toString().trim().isEmpty()){
            MessageUtil.showError(this, "Please input socket server url.");
            return;
        }

        if(txt_my_id.getText().toString().trim().isEmpty()){
            MessageUtil.showError(this, "Please input your id.");
            return;
        }


        SignallingClient.getInstance().init(txt_socket_url.getText().toString().trim(), txt_my_id.getText().toString().trim());

        isRegistered = true;
    }
    @OnClick(R.id.btn_call)
    public void onCall(){
        if(!isRegistered){
            MessageUtil.showError(this, "Please register your account.");
            return;
        }
        if(txt_friend_id.getText().toString().trim().isEmpty()){
            MessageUtil.showError(this, "Please input friend's id.");
            return;
        }
        if(!SignallingClient.getInstance().isInitiator){
            MessageUtil.showError(this, "Internet connection error. Please register again.");
            return;
        }

        String friendUserId = txt_friend_id.getText().toString().trim();
        String myId = txt_my_id.getText().toString().trim();

        SignallingClient.getInstance().emitCreateRoom("Some room", friendUserId, myId);

        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra("isCaller", true);
        intent.putExtra("myId", myId);
        intent.putExtra("friendId", friendUserId);
        startActivity(intent);
    }
    public void receiveCall(String roomName, String fromUserId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, VideoCallActivity.class);
                intent.putExtra("isCaller", false);
                intent.putExtra("myId", txt_my_id.getText().toString().trim());
                intent.putExtra("friendId", fromUserId);
                startActivity(intent);
            }
        });
    }
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission0 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (permission0 != PackageManager.PERMISSION_GRANTED
                || permission1 != PackageManager.PERMISSION_GRANTED
                || permission2 != PackageManager.PERMISSION_GRANTED
                || permission3 != PackageManager.PERMISSION_GRANTED
                || permission4 != PackageManager.PERMISSION_GRANTED
                || permission5 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        }
    }

    @Override
    public void onCreatedRoom(JSONObject data) {
        try {
            String fromUserID = data.getString("fromUserID");
            String roomName = data.getString("roomName");
            receiveCall(roomName, fromUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRemoteHangUp() { }
    @Override
    public void onOfferReceived(JSONObject data) { }
    @Override
    public void onAnswerReceived(JSONObject data) { }
    @Override
    public void onIceCandidateReceived(JSONObject data) { }
    @Override
    public void onTryToStart(JSONObject data) { }
    @Override
    public void onJoinedRoom() { }
    @Override
    public void onNewPeerJoined() { }
    @Override
    public void onFailedCreatedRoom(JSONObject data){ }
}
