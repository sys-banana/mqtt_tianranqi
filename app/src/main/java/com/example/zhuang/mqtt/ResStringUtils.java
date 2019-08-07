package com.example.zhuang.mqtt;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * @author xiey
 * @date created at 2018/7/11 8:43
 * @package com.bvc.adt.utils
 * @project SDChain
 * @email xiey94@qq.com
 * @motto Why should our days leave us never to return?
 */

public class ResStringUtils {
    public static String getString(Context mContext, @StringRes int stringRes) {
        if (stringRes != -1) {
            return mContext.getString(stringRes);
        }
        return "not found";
    }
}
