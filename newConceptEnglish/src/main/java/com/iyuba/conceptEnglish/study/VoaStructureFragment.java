package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructure;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureOp;
import com.iyuba.configation.Constant;

import timber.log.Timber;


public class VoaStructureFragment extends Fragment {

    private VoaStructure voaStructure;
    private Context mContext;
    private VoaStructureOp structureOp = new VoaStructureOp(mContext);

    private RelativeLayout noStructureLayout;
    private LinearLayout structureLayout;
    private LinearLayout tagLayout;

    private TextView englishTagText;
    private TextView chineseTagText;
    private TextView noteText;

    private int voaId;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voa_structure, container, false);
        }

        mContext = getActivity();

        initVoaStructure();

        return rootView;
    }


    private void initVoaStructure() {
        voaId = VoaDataManager.Instace().voaTemp.voaId;

        Timber.d("VoaId: %d", voaId);
        voaStructure = structureOp.findData(voaId);

        noStructureLayout = (RelativeLayout) rootView.findViewById(R.id.no_structure_view);
        structureLayout = (LinearLayout) rootView.findViewById(R.id.structure_view);
        tagLayout = (LinearLayout) rootView.findViewById(R.id.tag);

        englishTagText = (TextView) rootView.findViewById(R.id.english_tag);
        chineseTagText = (TextView) rootView.findViewById(R.id.chinese_tag);
        noteText = (TextView) rootView.findViewById(R.id.note);

        if (voaStructure == null) {
            noStructureLayout.setVisibility(View.VISIBLE);
            structureLayout.setVisibility(View.GONE);
        } else {
            setVoaStructure();
        }
    }

    public void setVoaStructure() {
        if ((voaStructure.descEN == null || ("").equals(voaStructure.descEN.trim()))
                && (voaStructure.descCH == null || ("").equals(voaStructure.descCH.trim()))) {
            tagLayout.setVisibility(View.GONE);
        } else {
            tagLayout.setVisibility(View.VISIBLE);

            if ((voaStructure.descEN == null) || ("").equals(voaStructure.descEN.trim())) {
                englishTagText.setVisibility(View.GONE);
            } else {
//				englishTagText.setTextColor(Constant.NORMAL_COLOR);
                englishTagText.setTextSize(Constant.textSize);

                //这里处理下相关的内容，去掉回车显示
                String showEnText = voaStructure.descEN;
                showEnText = showEnText.replace("\n","");
                englishTagText.setText(showEnText);
            }

            if ((voaStructure.descCH == null) || ("").equals(voaStructure.descCH.trim())) {
                chineseTagText.setVisibility(View.GONE);
            } else {
//				chineseTagText.setTextColor(Constant.NORMAL_COLOR);
                chineseTagText.setTextSize(Constant.textSize);

                //这里处理下相关的内容，去掉回车显示
                String showCnText = voaStructure.descCH;
                showCnText = showCnText.replace("\n","");
                chineseTagText.setText(showCnText);
            }
        }

        noteText.setTextColor(Constant.normalColor);
        noteText.setTextSize(Constant.textSize);

        String note = voaStructure.note;
        Log.e("sssssssssss", note);
        SpannableStringBuilder style = transformString(HelpUtil.transTitleStyle(note));
        noteText.setText(style);
    }

    public SpannableStringBuilder transformString(String str) {
        String[] strs = str.split("\\+\\+\\+");
        str = str.replaceAll("\\+\\+\\+", "");
        int from = 0;
        int to = 0;

        SpannableStringBuilder style = new SpannableStringBuilder(str);

        if (strs.length > 1) {
            for (int i = 0; i < strs.length - 2; i = i + 2) {
                from += strs[i].length();
                to = from + strs[i + 1].length();
                style.setSpan(new StyleSpan(
                                android.graphics.Typeface.BOLD_ITALIC), from, to,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                from += strs[i + 1].length();
            }
        }

        return style;
    }

    public void onResume() {
        super.onResume();
    }
}
