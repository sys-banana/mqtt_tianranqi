package com.example.zhuang.mqtt;

import android.content.Context;
import android.graphics.Color;

import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;

/**
 * @author : xiey
 * @project name : EAsset.
 * @package name  : com.bvc.easset.utils.
 * @date : 2018/6/26.
 * @signature : do my best.
 * @explain :
 */
public class QrUtils {
    private static QrConfig qrConfig(Context context) {
        QrConfig qrConfig = new QrConfig.Builder()
                .setDesText(ResStringUtils.getString(context,R.string.common_qr))//Scan the text under the frame
                .setShowDes(false)//Displays the text below the scan box
                .setShowLight(true)//Display the flashlight button
                .setShowTitle(true)//show Title
                .setShowAlbum(true)//Displays the select button from the album
                .setCornerColor(context.getResources().getColor(R.color.theme))//Set scan frame color
                .setLineColor(context.getResources().getColor(R.color.theme))//Set the scan line color
                .setLineSpeed(QrConfig.LINE_MEDIUM)//Set the scan line speed
                .setScanType(QrConfig.TYPE_QRCODE)//Set the scan type (qr code, barcode, all, custom, default is qr code)
                .setScanViewType(QrConfig.SCANVIEW_TYPE_QRCODE)//Set scan frame type (qr code or barcode, by default is qr code)
                .setCustombarcodeformat(QrConfig.BARCODE_I25)//This is only valid if the scan type is TYPE_CUSTOM
                .setPlaySound(true)//Whether to scan the bi~ sound successfully
//                .setDingPath(R.raw.test)//Set prompt tone (not set as the default Ding~)
                .setIsOnlyCenter(false)//Whether only the contents in the box are recognized (the default is full-screen recognition)
                .setTitleText(ResStringUtils.getString(context,R.string.common_qr_open))//set Tilte text
                .setTitleBackgroudColor(context.getResources().getColor(R.color.translation))//Set the status bar color
                .setTitleTextColor(Color.WHITE)//set Title text color
                .create();
        return qrConfig;
    }

    public static QrManager getInstance(Context context) {
        return QrManager.getInstance().init(qrConfig(context));
    }
}
