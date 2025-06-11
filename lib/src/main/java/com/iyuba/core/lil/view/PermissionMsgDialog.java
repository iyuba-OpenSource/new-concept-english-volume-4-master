package com.iyuba.core.lil.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 权限信息显示弹窗
 * @date: 2023/11/27 09:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PermissionMsgDialog extends AlertDialog {

    private Context context;
    private PermissionMsgAdapter msgAdapter;
    //当前需要申请的权限
    private String[] applyPermissions;

    public PermissionMsgDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    protected PermissionMsgDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected PermissionMsgDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permission_msg);

        initList();
        initClick();
    }

    private void initList(){
        msgAdapter = new PermissionMsgAdapter(context,new ArrayList<>());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(msgAdapter);
    }

    private void initClick(){
        TextView applyBtn = findViewById(R.id.apply);
        applyBtn.setOnClickListener(v->{
            dismiss();
            applyPermission();
        });
        TextView cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(v->{
            dismiss();
        });
    }

    //权限申请
    private void applyPermission(){
        XXPermissions.with(context)
                .permission(applyPermissions)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all){
                            if (applyListener!=null){
                                applyListener.onApplyResult(true);
                            }
                        }else {
                            if (applyListener!=null){
                                applyListener.onApplyResult(false);
                            }
                            ToastUtil.showToast(context,"请授权所需权限后使用");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never){
                            ToastUtil.showToast(context,"请前往应用授权界面手动授权");
                            if (applyListener!=null){
                                applyListener.onApplyResult(false);
                            }
                        }
                    }
                });
    }

    //设置权限和显示数据
    private void setShowMsg(String title,List<Pair<String,Pair<String,String>>> pairList){
        if (!TextUtils.isEmpty(title)){
            TextView titleView = findViewById(R.id.title);
            titleView.setText(title);
        }

        //这里循环判断当前的权限状态
        List<Pair<String,Pair<String,String>>> needList = new ArrayList<>();
        if (pairList!=null&&pairList.size()>0){
            for (int i = 0; i < pairList.size(); i++) {
                String permission = pairList.get(i).first;
                if (!XXPermissions.isGranted(context,permission)){
                    needList.add(pairList.get(i));
                }
            }
        }

        //权限信息
        StringBuffer buffer = new StringBuffer();
        //获取需要申请的权限
        applyPermissions = new String[needList.size()];
        for (int i = 0; i < needList.size(); i++) {
            applyPermissions[i] = needList.get(i).first;
            buffer.append(needList.get(i).second.first);

            if (i != needList.size()-1){
                buffer.append("、");
            }
        }

        //显示权限信息
        TextView msgView = findViewById(R.id.msg);
        msgView.setText("此功能需要授权 "+buffer.toString()+" 后使用");
        //显示每个的功能
        msgAdapter.refreshData(needList);
    }

    @Override
    public void show() {
        super.show();
    }

    //回调接口
    public OnPermissionApplyListener applyListener;

    public interface OnPermissionApplyListener{
        void onApplyResult(boolean isSuccess);
    }

    //判断权限是否需要申请
    private boolean isNeedApply(List<Pair<String,Pair<String,String>>> pairList){
        if (pairList!=null&&pairList.size()>0){
            String[] permissionArray = new String[pairList.size()];
            for (int i = 0; i < pairList.size(); i++) {
                permissionArray[i] = pairList.get(i).first;
            }

            if (!XXPermissions.isGranted(context,permissionArray)){
                return true;
            }
        }

        return false;
    }


    //显示弹窗
    public PermissionMsgDialog showDialog(String title,List<Pair<String,Pair<String,String>>> pairList,boolean isShowDialog,OnPermissionApplyListener applyListener){
        create();

        if (isNeedApply(pairList)){
            setShowMsg(title,pairList);
            this.applyListener = applyListener;

            if (isShowDialog){
                setCancelable(false);
                show();
            }else {
                applyPermission();
            }
        }else {
            if (applyListener!=null){
                applyListener.onApplyResult(true);
            }
        }

        return this;
    }
}
