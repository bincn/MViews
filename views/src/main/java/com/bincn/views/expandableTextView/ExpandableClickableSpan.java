package com.bincn.views.expandableTextView;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by bin on 2019/1/22.
 */
public abstract class ExpandableClickableSpan extends ClickableSpan {

    public ExpandableClickableSpan() {
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
//        ds.setColor(Color.parseColor("#ff74b9"));
        ds.setUnderlineText(false);
    }
}
