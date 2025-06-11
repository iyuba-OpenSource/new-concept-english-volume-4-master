package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iyuba.conceptEnglish.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 知识页面 fragment
 */
public class KnowledgeFragment extends Fragment {

    private int curSelectActivity = 0;

    @BindView(R.id.knowledgeBody)
    FrameLayout knowledgeBody;
    @BindView(R.id.l1)
    RelativeLayout l1;
    @BindView(R.id.voa_annotations)
    ImageView voaAnnotations;
    @BindView(R.id.voa_annotations_ln)
    LinearLayout voaAnnotationsLn;
    @BindView(R.id.voa_diffculty)
    ImageView voaDiffculty;
    @BindView(R.id.voa_diffculty_ln)
    LinearLayout voaDiffcultyLn;
    @BindView(R.id.voa_important_sentences)
    ImageView voaImportantSentences;
    @BindView(R.id.voa_important_sentences_ln)
    LinearLayout voaImportantSentencesLn;
    @BindView(R.id.voa_words)
    ImageView voaWords;
    @BindView(R.id.voa_words_ln)
    LinearLayout voaWordsLn;

    private Context mContext;
    private View rootView;
    Unbinder unbinder;

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle paramBundle) {
        if (this.rootView == null) {
            this.rootView = inflater.inflate(R.layout.fragment_knowlage, viewGroup, false);
        }

        this.unbinder = ButterKnife.bind(this, this.rootView);

        //屏蔽单词显示
        voaWordsLn.setVisibility(View.GONE);
        this.curSelectActivity = 1;

        clickTab();
        return this.rootView;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    public void onResume() {
        super.onResume();
    }

    private void clickTab() {
        voaWords.setImageResource(R.drawable.voa_words_normal_new);
        voaAnnotations.setImageResource(R.drawable.voa_annotations_normal_new);
        voaImportantSentences.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
        voaDiffculty.setImageResource(R.drawable.voa_diffcult_normal_new);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        switch (curSelectActivity) {
            case 0:
                ft.replace(R.id.knowledgeBody, new VoaWordFragment());
                ft.commitAllowingStateLoss();
                this.voaWords.setImageResource(R.drawable.voa_words_press_new);
                break;
            case 1:
                ft.replace(R.id.knowledgeBody, new VoaAnnotationFragment());
                ft.commitAllowingStateLoss();
                voaAnnotations.setImageResource(R.drawable.voa_annotations_press_new);
                break;
            case 2:
                ft.replace(R.id.knowledgeBody, new VoaStructureFragment());
                ft.commitAllowingStateLoss();
                this.voaImportantSentences.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                break;
            case 3:
                ft.replace(R.id.knowledgeBody, new VoaDiffcultyFragment());
                ft.commitAllowingStateLoss();
                this.voaDiffculty.setImageResource(R.drawable.voa_diffcult_press_new);
                break;

        }

    }

    public static KnowledgeFragment newInstence(int voaId) {
        KnowledgeFragment localKnowledgeFragment = new KnowledgeFragment();
        Bundle localBundle = new Bundle();
        localBundle.putInt("curVoaId", voaId);
        localKnowledgeFragment.setArguments(localBundle);
        return localKnowledgeFragment;
    }


    @OnClick({R.id.voa_annotations_ln})
    void goToAnnotations() {
        this.curSelectActivity = 1;
        clickTab();
    }

    @OnClick({R.id.voa_diffculty_ln})
    void goToDiffcult() {
        this.curSelectActivity = 3;
        clickTab();
    }

    @OnClick({R.id.voa_important_sentences_ln})
    void goToImportant() {
        this.curSelectActivity = 2;
        clickTab();
    }

    @OnClick({R.id.voa_words_ln})
    void goToWord() {
        this.curSelectActivity = 0;
        clickTab();
    }


    public void refreshView() {
        //这里将单词界面放在单独的tab中，因此初始显示为1
        this.curSelectActivity = 1;
        clickTab();
    }
}
