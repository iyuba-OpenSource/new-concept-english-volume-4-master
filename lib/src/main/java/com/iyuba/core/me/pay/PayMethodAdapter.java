package com.iyuba.core.me.pay;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.me.holder.BaseViewHolder;
import com.iyuba.lib.R;


public class PayMethodAdapter extends BaseAdapter {
    private static final String TAG = PayMethodAdapter.class.getSimpleName();

    public interface PayMethod {
        int WEIXIN = 0;
        int ALIPAY = 1;
        int BANKCARD = 2;
    }

    private LayoutInflater mInflater;
    public String[] methods, hints;
    private int[] imageIds;
    private boolean[] selections;

    public PayMethodAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        TypedArray ta = null;

        //根据包名判断
        if ("com.iyuba.conceptStory".equals(context.getPackageName())
                ||"com.iyuba.concept2".equals(context.getPackageName())
                ||"com.iyuba.englishfm".equals(context.getPackageName())
                ||"com.iyuba.learnNewEnglish".equals(context.getPackageName())
        ){
            ta = context.getResources().obtainTypedArray(R.array.multiPay_methods_images);
            methods = context.getResources().getStringArray(R.array.multiPay_methods);
            hints = context.getResources().getStringArray(R.array.multiPay_method_hints);
        }else {
            ta = context.getResources().obtainTypedArray(R.array.alipay_methods_images);
            methods = context.getResources().getStringArray(R.array.alipay_methods);
            hints = context.getResources().getStringArray(R.array.alipay_method_hints);
        }

        int length = ta.length();
        imageIds = new int[length];
        selections = new boolean[length];
        for (int i = 0; i < length; i++) {
            imageIds[i] = ta.getResourceId(i, 0);
            selections[i] = (i == 0);
        }
        ta.recycle();
    }

    public int getMethodsLength() {
        return methods.length;
    }

    public void changeSelectPosition(int position) {
        resetSelection();
        selections[position] = true;
    }

    private void resetSelection() {
        for (int i = 0; i < selections.length; i++) {
            selections[i] = false;
        }
    }

    public int getSelectedPayWay() {
        int selectedPosition = 0;
        for (int i = 0; i < selections.length; i++) {
            if (selections[i]) {
                selectedPosition = i;
                break;
            }
        }
        return PayMethodHelper.mapPositionToMethod(selectedPosition);
    }

    @Override
    public int getCount() {
        return methods.length;
    }

    @Override
    public Object getItem(int position) {
        return methods[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pay_method, null);
        }
        ImageView methodIcon = BaseViewHolder.get(convertView, R.id.order_pay_method_icon);
        TextView methodName = BaseViewHolder.get(convertView, R.id.pay_by_method_text);
        TextView methodHint = BaseViewHolder.get(convertView, R.id.pay_by_method_text_hint);
        CheckBox methodBox = BaseViewHolder.get(convertView, R.id.pay_by_method_checkbox);
        methodIcon.setImageResource(imageIds[position]);
        methodName.setText(methods[position]);
        methodHint.setText(hints[position]);
        methodBox.setChecked(selections[position]);
        return convertView;
    }
}
