package com.iyuba.conceptEnglish.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;

import java.util.List;

public class SwitchDialog {

    private List<String> itemNameList;
    private List<Boolean> listSwitch;
    private List<SwitchDialogCallback> itemCallBackList;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;


    public static SwitchDialog getIntence() {
        return new SwitchDialog();
    }

    public SwitchDialog init(List<String> itemNameList,
                             List<Boolean> listSwitch,
                             List<SwitchDialogCallback> itemCallBackList) {
        this.itemNameList = itemNameList;
        this.listSwitch = listSwitch;
        this.itemCallBackList = itemCallBackList;
        return this;
    }

    public SwitchDialog inflateView(Context context) {
        mBuilder = new AlertDialog.Builder(context);
        LinearLayout containView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_switch, null, false);
        for (int loopI  = 0; loopI < itemNameList.size(); loopI++) {
            String str = itemNameList.get(loopI);
            View itemView = LayoutInflater.from(context).inflate(R.layout.dialog_switch_item, null, false);
            TextView textView = itemView.findViewById(R.id.text1);
            Switch switchBtn = itemView.findViewById(R.id.switch1);
            switchBtn.setChecked(listSwitch.get(loopI));
            SwitchDialogCallback callback=itemCallBackList.get(loopI);
            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    callback.change(isChecked);
                }
            });
            textView.setText(str);
            containView.addView(itemView);
        }
        LinearLayout line = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.line, null, false);
        containView.addView(line);

        mBuilder.setView(containView);
        return this;
    }

    public SwitchDialog show() {
        mDialog = mBuilder.create();
        mDialog.show();
        return this;
    }

    public interface SwitchDialogCallback {
        void change(boolean isTurn);
    }
}
