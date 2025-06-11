package com.iyuba.core.talkshow.lesson.watch.comment;


import com.iyuba.core.common.data.model.Comment;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface CommentMvpView extends MvpView {
    void showComments(List<Comment> commentList);

    void showEmptyComment();

    void showToast(int id);

    void clearInputText();

    void setCommentNum(int num);

    void startDubbingActivity(TalkLesson voa);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showCommentLoadingLayout();

    void dismissCommentLoadingLayout();

    void dismissRefreshingView();

}
