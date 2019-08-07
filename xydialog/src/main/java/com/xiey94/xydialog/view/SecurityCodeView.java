package com.xiey94.xydialog.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiey94.xydialog.R;


/**
 * @author xiey
 * @date created at 2018/6/5 11:07
 * @package com.hwelltech.easset.widget
 * @project EAsset
 * @email xiey94@qq.com
 * @motto Why should our days leave us never to return?
 */

public class SecurityCodeView extends RelativeLayout {
    private EditText editText;
    private TextView[] TextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 6;
    private int defaultCount = 6;
    private String inputContent;

    public void setPassword(boolean password) {
        if (password) {
            for (int i = 0; i < defaultCount; i++) {
                TextViews[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public SecurityCodeView(Context context) {
        this(context, null);
    }

    public SecurityCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecurityCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextViews = new TextView[defaultCount];
        View.inflate(context, R.layout.security_code_view_layout, this);

        editText = (EditText) findViewById(R.id.item_edittext);
        TextViews[0] = (TextView) findViewById(R.id.item_code_iv1);
        TextViews[1] = (TextView) findViewById(R.id.item_code_iv2);
        TextViews[2] = (TextView) findViewById(R.id.item_code_iv3);
        TextViews[3] = (TextView) findViewById(R.id.item_code_iv4);
        TextViews[4] = (TextView) findViewById(R.id.item_code_iv5);
        TextViews[5] = (TextView) findViewById(R.id.item_code_iv6);
        editText.setCursorVisible(false);//将光标隐藏
        setListener();
    }

    private void setListener() {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //如果字符不为""时才进行操作
                if (!editable.toString().equals("")) {
                    if (stringBuffer.length() > defaultCount - 1) {
                        //当文本长度大于3位时editText置空
                        editText.setText("");
                        return;
                    } else {
                        //将文字添加到StringBuffer中
                        stringBuffer.append(editable);
                        editText.setText("");//添加后将EditText置空
                        if (stringBuffer.length() > defaultCount)
                            stringBuffer.delete(defaultCount, stringBuffer.length());
                        count = stringBuffer.length();
                        inputContent = stringBuffer.toString();
                        if (stringBuffer.length() == defaultCount) {
                            //文字长度位4  则调用完成输入的监听
                            if (inputCompleteListener != null) {
                                inputCompleteListener.inputComplete(inputContent);
                            }
                        }
                    }

                    for (int i = 0; i < stringBuffer.length(); i++) {
                        if (i < defaultCount) {
                            TextViews[i].setText(String.valueOf(inputContent.charAt(i)));
                            TextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_blue);
                        }
                    }

                }
            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onKeyDelete()) return true;
                    return true;
                }
                return false;
            }
        });
    }


    public boolean onKeyDelete() {
        if (count == 0) {
            count = defaultCount;
            return true;
        }
        if (stringBuffer.length() > 0) {
            //删除相应位置的字符
            stringBuffer.delete((count - 1), count);
            count--;
            inputContent = stringBuffer.toString();
            TextViews[stringBuffer.length()].setText("");
            TextViews[stringBuffer.length()].setBackgroundResource(R.drawable.bg_user_verify_code_grey);
            if (inputCompleteListener != null)
                inputCompleteListener.deleteContent(true);//有删除就通知manger

        }
        return false;
    }

    /**
     * 清空输入内容
     */
    public void clearEditText() {
        stringBuffer.delete(0, stringBuffer.length());
        inputContent = stringBuffer.toString();
        for (int i = 0; i < TextViews.length; i++) {
            if (i < defaultCount) {
                TextViews[i].setText("");
                TextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_grey);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private InputCompleteListener inputCompleteListener;

    public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public interface InputCompleteListener {
        void inputComplete(String pwd);

        void deleteContent(boolean isDelete);
    }

    /**
     * 获取输入文本
     *
     * @return
     */
    public String getEditContent() {
        return inputContent;
    }

    public EditText getEditText() {
        return editText;
    }

}