package com.example.importapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ImportAppEntryPoint implements EntryPoint {
    @Override
    public void onModuleLoad() {
        RootPanel.get("app").add(new ImportPanel());
    }

    private static class ImportPanel extends Composite {
        public ImportPanel() {
            FlowPanel root = new FlowPanel();
            root.setStyleName("panel");

            Label title = new Label("سیستم بارگذاری فایل اکسل");
            title.setStyleName("status");

            TextBox fileName = new TextBox();
            fileName.setValue("هیچ فایلی انتخاب نشده است");
            fileName.setEnabled(false);

            Button select = new Button("انتخاب فایل");
            Button approve = new Button("تأیید");
            Button cancel = new Button("لغو");
            Button start = new Button("شروع پردازش");

            select.setStyleName("button");
            approve.setStyleName("button secondary");
            cancel.setStyleName("button danger");
            start.setStyleName("button");

            FlowPanel firstRow = new FlowPanel();
            firstRow.add(select);
            firstRow.add(fileName);

            FlowPanel controls = new FlowPanel();
            controls.add(approve);
            controls.add(cancel);
            controls.add(start);

            Label status = new Label("وضعیت: آماده برای بارگذاری");
            Label worker = new Label("سرور: localhost");
            Label lockUser = new Label("کاربر قفل‌کننده: -");
            Label progress = new Label("تعداد ردیف‌های خوانده‌شده: 0 | درج‌شده: 0");
            Label elapsed = new Label("زمان سپری‌شده: 00:00");

            root.add(title);
            root.add(firstRow);
            root.add(controls);
            root.add(status);
            root.add(worker);
            root.add(lockUser);
            root.add(progress);
            root.add(elapsed);

            initWidget(root);
        }
    }
}
