package com.iyuba.core.common.widget.dialog;


import com.iyuba.core.common.injection.PerDialog;

@PerDialog
//@Subcomponent
public interface DialogComponent {
//        void inject(DubbingDialog dubbingDialog);
//
//        void inject(SchoolDialog schoolDialog);

        void inject(DownloadDialog downloadDialog);
}