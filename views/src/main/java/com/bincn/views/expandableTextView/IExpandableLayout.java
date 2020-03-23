package com.bincn.views.expandableTextView;

/**
 * @author mwb
 * @date 2020-03-16
 */
public interface IExpandableLayout {

    /**
     * 收缩的高度
     */
    int getCollapsedHeight();

    /**
     * 展开的高度
     */
    int getExpandedHeight();

    /**
     * 变化的高度
     */
    int getOffsetHeight();
}
