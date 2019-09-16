package com.bincn.views.radius;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public class CoverConner {
  private float mSize;
  private @ColorRes int mColorResource;
  private Context mContext;
  private int mUnit = TypedValue.COMPLEX_UNIT_DIP;
  private ColorStateList mColorStateList;

  public CoverConner(@ColorRes int colorResource, int size, Context context) {
    setColorResource(colorResource);
    setSize(size);
    mContext = context;
  }

  public CoverConner(@NonNull ColorStateList colorStateList, int size, Context context) {
    setColorStateList(colorStateList);
    setSize(size);
    mContext = context;
  }

  public ColorStateList getColor() {
    if (mColorStateList != null) {
      return mColorStateList;
    } else {
      return mContext.getResources().getColorStateList(mColorResource);
    }
  }

  public void setColorStateList(ColorStateList colorStateList) {
    mColorStateList = colorStateList;
  }

  public void setColorResource(int colorResource) {
    mColorResource = colorResource;
  }

  public float getSize() {
    return mSize;
  }

  public void setSize(float size) {
    mSize = size;
  }

  public int getUnit() {
    return mUnit;
  }

  public void setUnit(int unit) {
    mUnit = unit;
  }
}
