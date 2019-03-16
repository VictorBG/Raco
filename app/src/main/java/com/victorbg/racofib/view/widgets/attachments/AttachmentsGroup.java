package com.victorbg.racofib.view.widgets.attachments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Attachment;
import com.victorbg.racofib.view.base.BaseActivity;

import java.util.List;


import androidx.annotation.Nullable;
import timber.log.Timber;

public class AttachmentsGroup extends ChipGroup {

    public AttachmentsGroup(Context context) {
        super(context);
    }

    public AttachmentsGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AttachmentsGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAttachments(List<Attachment> list) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        removeAllViews();
        for (Attachment attachment : list) {
            Chip chip = (Chip) layoutInflater.inflate(R.layout.attachment_chip, this, false);
            chip.setText(attachment.name);
            chip.setChipIconResource(getMimeDrawable(attachment.mime, attachment.name));

            chip.setOnClickListener(v -> {
                Context activityContext = getContext();
                if (activityContext instanceof BaseActivity) {
                    ((BaseActivity) activityContext).downloadFile(attachment);
                } else {
                    Timber.w("Could not get a reference to a BaseActivity. Download has not been performed");
                }
            });

            addView(chip);
        }
    }

    private int getMimeDrawable(String mime, String name) {
        switch (mime) {
            case "application/pdf":
                return R.drawable.ic_mime_pdf;
            case "text/plain":
                if (name.contains("zip") || name.contains("rar") || name.contains("tar")) {
                    return R.drawable.ic_mime_zip;
                }
                return R.drawable.ic_mime_file;
            case "application/msword":
                return R.drawable.ic_mime_word;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case "application/vnd.ms-excel":
            case "application/msexcel":
            case "application/x-msexcel":
            case "application/x-ms-excel":
            case "application/x-excel":
            case "application/x-dos_ms_excel":
            case "application/xls":
            case "application/x-xls":
                return R.drawable.ic_mime_excel;
            default:
                return R.drawable.ic_baseline_attachment_24px;
        }
    }
}
