package com.zhang.thirdfacebook;

import com.facebook.Profile;

import org.json.JSONObject;

/**
 * Created by zhangyuncai on 2019/8/8.
 */
public interface ThridFacebookCallBack {
    void result(JSONObject object, Profile currentProfile);

}
