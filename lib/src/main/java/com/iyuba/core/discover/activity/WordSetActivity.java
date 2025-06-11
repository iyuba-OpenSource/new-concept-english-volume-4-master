package com.iyuba.core.discover.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.discover.adapter.MaterialDialogAdapter;
import com.iyuba.core.discover.adapter.MyLinearLayoutManager;
import com.iyuba.core.discover.adapter.OnRecyclerViewItemClickListener;
import com.iyuba.lib.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import me.drakeet.materialdialog.MaterialDialog;


public class WordSetActivity extends BasisActivity {

    private RoundRelativeLayout group, showDef;
    private CheckBox currShowDef;
    private TextView currGroup;
    private Context context;
   private ImageView button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set);
        context = this;

        initViews();
        loadData();
    }



    protected void initViews( ) {
        group = (RoundRelativeLayout) findViewById(R.id.word_set_group);
        currGroup = (TextView) findViewById(R.id.word_set_group_current);
        showDef = (RoundRelativeLayout) findViewById(R.id.word_set_show_def);
        currShowDef = (CheckBox) findViewById(R.id.word_set_show_def_current);
        button_back = findViewById(R.id.button_back);
    }


    protected void loadData() {
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popGroupDialog();
            }
        });
        currGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currGroup.setOnClickListener(this);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currShowDef.setChecked(!currShowDef.isChecked());
                ConfigManager.Instance().setShowDef(currShowDef.isChecked());
            }
        });
        currShowDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigManager.Instance().setShowDef(currShowDef.isChecked());
            }
        });
        changeUIByPara();
    }


    protected void changeUIByPara() {
        currGroup.setText(getWordOrder(ConfigManager.Instance().getWordSort()));
        currShowDef.setChecked(ConfigManager.Instance().isShowDef());
    }

    private void popGroupDialog() {
        //如此量级的应用大部分I/O操作都放在主线程实在不敢苟同
        final MaterialDialog groupDialog = new MaterialDialog(context);
        groupDialog.setTitle("排列方式");
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.word_group)));
        adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ConfigManager.Instance().setWordSort(position);
                currGroup.setText(getWordOrder(position));
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.Instance().getWordSort());
        languageList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private String getWordOrder(int order) {
        String[] wordGroup = context.getResources().getStringArray(R.array.word_group);
        return wordGroup[order];
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
