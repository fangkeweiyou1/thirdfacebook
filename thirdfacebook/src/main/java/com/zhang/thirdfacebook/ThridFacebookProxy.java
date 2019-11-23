package com.zhang.thirdfacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

/**
 * Created by zhangyuncai on 2019/7/23.
 */
public class ThridFacebookProxy {
    private static ThridFacebookProxy proxy;
    private ThridFacebookCallBack callBack;
    private FragmentActivity activity;
    LoginButton facebook_loginthird;
    private CallbackManager callbackManager;


    public static void onCreate(FragmentActivity activity, int button_id, ThridFacebookCallBack callBack) {
        if (proxy == null) {
            proxy = new ThridFacebookProxy(activity, button_id, callBack);
        }
        LoginManager.getInstance().logOut();//拿到个人消息后,退出facebook登录
        proxy.init();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        proxy.callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * @param activity
     * @param button_id LoginButton的id
     */
    public ThridFacebookProxy(FragmentActivity activity, int button_id, ThridFacebookCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        facebook_loginthird = activity.findViewById(button_id);
    }

    private void init() {
        facebook_loginthird.setPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        facebook_loginthird.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                getFacebookInfo(accessToken);
                System.out.println("----------->>>>>>>>-----------onSuccess");
            }

            @Override
            public void onCancel() {
                System.out.println("----------->>>>>>>>-----------onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                System.out.println("----------->>>>>>>>-----------onError" + exception.getMessage());
            }
        });
    }

    private void getFacebookInfo(AccessToken accessToken) {
        String userId = accessToken.getUserId();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        if (response.getError() != null) {
                            System.out.println("----------->>>>>>>>-----------response.getError():" + response.getError().getErrorMessage());
                        } else {
                            LoginManager.getInstance().logOut();//拿到个人消息后,退出facebook登录
                            if (callBack != null) {
                                callBack.result(object);
                            } else {
                                System.out.println("----------->>>>>>>>-----------object:" + object.toString());
                                String email = object.optString("email");
                                String userid = object.optString("id");
                                String name = "";
                                try {
                                    String first_name = object.optString("first_name");
                                    String last_name = object.optString("last_name");
                                    Profile currentProfile = Profile.getCurrentProfile();
                                    name = currentProfile.getName();
                                    System.out.println("----------->>>>>>>>-----------"
                                            + "name  " + currentProfile.getName() + "  email  " + email + "  gender  " + object.optString("gender") + "  user_birthday  " + object.optString("birthday"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
//                            if (TextUtils.isEmpty(email)) {
//                                new ThirdLoginFailDialog(mActivity).show();
//                                return;
//                            }
//                            ThirdInfoModel infoModel = new ThirdInfoModel();
//                            infoModel.setEmail(email);
//                            infoModel.setName("");
//                            infoModel.setUid(userid);
//                            infoModel.setPlatform(SHARE_MEDIA.FACEBOOK);
//
//                            login(infoModel);

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();

    }

}
