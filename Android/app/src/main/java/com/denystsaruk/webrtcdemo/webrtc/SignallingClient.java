package com.denystsaruk.webrtcdemo.webrtc;

import android.util.Log;

import com.denystsaruk.webrtcdemo.ui.activity.VideoCallActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SignallingClient {
    private static SignallingClient instance;
    public String roomName = "some_room_name";
    public boolean isStarted = false;
    public SignalingInterface callback;
    public String myId;
    public String friendId;

    private Socket socket;
    public String socketServerUrl;
    public boolean isInitiator = false;

    public static SignallingClient getInstance() {
        if (instance == null) {
            instance = new SignallingClient();
        }
        if (instance.roomName == null) {
            //set the room name here
            instance.roomName = "some_room_name";
        }
        return instance;
    }
//    @SuppressLint("TrustAllX509TrustManager")
//    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//            return new java.security.cert.X509Certificate[]{};
//        }
//
//        public void checkClientTrusted(X509Certificate[] chain,
//                                       String authType) {
//        }
//
//        public void checkServerTrusted(X509Certificate[] chain,
//                                       String authType) {
//        }
//    }};

    public void init(String socketServer, String myId){
        try {
//            SSLContext sslcontext = SSLContext.getInstance("TLS");
//            sslcontext.init(null, trustAllCerts, null);
//            IO.setDefaultHostnameVerifier((hostname, session) -> true);
//            IO.setDefaultSSLContext(sslcontext);
            //set the socket.io url here
            socketServerUrl = socketServer;
            socket = IO.socket(socketServerUrl);
            socket.connect();

            emitJoinUser(myId);

            socket.on("created", args -> {
                Log.d("SignallingClient", "created with: args = [" + Arrays.toString(args) + "]");
                isInitiator = true;
                if (args[0] instanceof JSONObject) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String socketId = data.getString("socketID");
                        String userID = data.getString("userID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("createCall", args -> {
                if(callback!= null){
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        callback.onCreatedRoom(data);
                    }
                }
            });
            socket.on("notRegisterdFriend", args -> {
                if(callback!= null){
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        if(VideoCallActivity.instance != null){
                            VideoCallActivity.instance.endCalling();
                        }
                    }
                }
            });
            socket.on("callOffer", args -> {
                if(callback!= null){
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        callback.onOfferReceived(data);
                    }
                }
            });
            socket.on("callAnswer", args -> {
                if(callback!= null){
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        callback.onAnswerReceived(data);
                    }
                }
            });
            socket.on("callIceCandidate", args -> {
                if(callback!= null){
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        if(VideoCallActivity.instance != null) {
                            if (VideoCallActivity.instance.sendSDP) {
                                callback.onIceCandidateReceived(data);
                            } else {
                                VideoCallActivity.instance.iceCandidates.add(data);
                            }
                        }
                    }
                }
            });
            socket.on("callEnd", args -> {
                if(callback != null) {
                    callback.onRemoteHangUp();
                }
            });
//        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void emitJoinUser(String userId){
        socket.emit("createUser", userId);
    }
    public void emitCreateRoom(String roomName, String friendId, String myId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("roomName", roomName);
            obj.put("toUserId", friendId);
            obj.put("fromUserId", myId);
            socket.emit("createCall", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void emitCallOffer(SessionDescription message, String friendId, String myId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("roomName", roomName);
            obj.put("toUserId", friendId);
            obj.put("fromUserId", myId);
            obj.put("type", message.type.canonicalForm());
            obj.put("sdp", message.description);
            socket.emit("callOffer", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void emitAnswer(SessionDescription message, String friendId, String myId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("roomName", roomName);
            obj.put("toUserId", friendId);
            obj.put("fromUserId", myId);
            obj.put("type", message.type.canonicalForm());
            obj.put("sdp", message.description);
            socket.emit("callAnswer", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void emitIceCandidate(IceCandidate iceCandidate, String friendId, String myId) {
        try {
            JSONObject object = new JSONObject();
            object.put("toUserId", friendId);
            object.put("fromUserId", myId);
            object.put("type", "candidate");
            object.put("label", iceCandidate.sdpMLineIndex);
            object.put("id", iceCandidate.sdpMid);
            object.put("candidate", iceCandidate.sdp);
            socket.emit("callIceCandidate", object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void emitClose(String friendId, String myId){
        try {
            JSONObject obj = new JSONObject();
            obj.put("roomName", roomName);
            obj.put("toUserId", friendId);
            obj.put("fromUserId", myId);
            socket.emit("callEnd", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface SignalingInterface {
        void onRemoteHangUp();
        void onOfferReceived(JSONObject data);
        void onAnswerReceived(JSONObject data);
        void onIceCandidateReceived(JSONObject data);
        void onTryToStart(final JSONObject data);
        void onCreatedRoom(JSONObject data);
        void onFailedCreatedRoom(JSONObject data);
        void onJoinedRoom();
        void onNewPeerJoined();
    }
}
