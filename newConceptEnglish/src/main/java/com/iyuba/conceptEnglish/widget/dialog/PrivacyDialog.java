package com.iyuba.conceptEnglish.widget.dialog;

import android.content.Context;


public class PrivacyDialog {


    public static void showDialog(final Context context) {
//        String privacy1 = "";
//        String privacy2 = "2.使用本app需要您了解并同意";
//        String privacy3 = "用户协议及隐私政策";
//        String privacy4 = "，点击同意即代表您已阅读并同意该协议";
//
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                Intent intent = new Intent(context, Web.class);
//                String url = PrivacyUtil.getPrivacyUrl();
//                intent.putExtra("url", url);
//                intent.putExtra("title", "用户协议及隐私政策");
//                context.startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
//            }
//        };
//        int start=privacy1.length()+privacy2.length();
//        int end= start+privacy3.length();
//
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
//        strBuilder.append(privacy1);
//        strBuilder.append(privacy2);
//        strBuilder.append(privacy3);
//        strBuilder.append(privacy4);
//        strBuilder.setSpan(clickableSpan,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        final AlertDialog dialog = new AlertDialog.Builder(context)
//                .setCancelable(false)
//                .create();
//
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacy, null);
//        dialog.setView(view);
//        dialog.show();
//
//        TextView textView = view.findViewById(R.id.text_link);
//
//        textView.setText(strBuilder);
//
//        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//
//        TextView agreeNo = view.findViewById(R.id.text_no_agree);
//        TextView agree = view.findViewById(R.id.text_agree);
//
//        agreeNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.showToast(context, "请同意");
//            }
//        });
//
//        agree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InfoHelper.getInstance().setHidePrivacy(true);
//                dialog.dismiss();
//            }
//        });

    }
}
