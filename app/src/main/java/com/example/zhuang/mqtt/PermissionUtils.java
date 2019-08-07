package com.example.zhuang.mqtt;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;


import com.xiey94.xydialog.dialog.XyDialog2;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

/**
 * @author : xiey
 * @project name : EAsset.
 * @package name  : com.bvc.easset.utils.
 * @date : 2018/6/27.
 * @signature : do my best.
 * @explain :
 */
public class PermissionUtils {
    private Context context;
    private String[] requestPermissions;
    private String errHint;
    private RequestPermission request;

    public PermissionUtils getInstance(Context context) {
        this.context = context;
        return this;
    }

    public PermissionUtils permissions(String... permissions) {
        this.requestPermissions = permissions;
        return this;
    }

    public PermissionUtils errHint(String errHint) {
        this.errHint = errHint;
        return this;
    }

    public PermissionUtils permission(RequestPermission request) {
        this.request = request;
        return this;
    }

    public void start() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            request.success(null);
            return;
        }
        AndPermission.with(context)
                .permission(requestPermissions)
                .onGranted(permissions -> {
                    //succeed
                    try {
                        if (request != null) {
                            request.success(permissions);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "try again", Toast.LENGTH_SHORT).show();
                    }
                })
                .onDenied(permissions -> {
                    //failed
                    Loggers.e("Application permission failed");
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                        // These permissions are always denied by the user。
                        // Here, a Dialog is used to show that an application cannot continue to run without these permissions, asking the user whether to authorize in the Settings.
                        try {
                            if (request != null) {
                                request.failed(permissions);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "try again", Toast.LENGTH_SHORT).show();
                        }
                        SettingService settingService = AndPermission.permissionSetting(context);
                        new XyDialog2.Builder(context)
                                .title(ResStringUtils.getString(context,R.string.permissions_tips ))
                                .message( errHint + ResStringUtils.getString(context,R.string.permissions_tips))
                                .setPositiveButtonListener(ResStringUtils.getString(context,R.string.permissions_ok), (o, dialog, i) -> {
                                    // If the user agrees to set:
                                    settingService.execute();
                                    dialog.dismiss();
                                })
                                .setNegativeButtonListener(ResStringUtils.getString(context,R.string.permissions_cancel), (o, dialog, i) -> {
                                    // If the user does not agree to set it：
                                    settingService.cancel();
                                    dialog.dismiss();
                                })
                                .createNoticeDialog()
                                .show();
                    }
                })
                .rationale((context1, permissions, executor) -> {
                    Loggers.e("Application permission denied");
                    try {
                        if (request != null) {
                            request.refuse(context1, permissions, executor);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "try again", Toast.LENGTH_SHORT).show();
                    }
                    //reapply
                    executor.execute();
                })
                .start();
    }

    public RequestPermission requestPermission;

    public void setRequest(RequestPermission request) {
        this.requestPermission = request;
    }

    public interface RequestPermission {
        void success(List<String> permissions);

        default void failed(List<String> permissions) {
        }

        default void refuse(Context context, List<String> permissions, RequestExecutor executor) {
        }


    }
}
