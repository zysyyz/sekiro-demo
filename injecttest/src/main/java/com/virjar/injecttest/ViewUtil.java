package com.virjar.injecttest;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.collect.Lists;


import java.util.List;

import external.org.apache.commons.lang3.StringUtils;

import static com.virjar.injecttest.HookEntry.TAG;

public class ViewUtil {

    /**
     * 获取某个View的所有子View及自身
     * 具体实现查看{@link this#collectViews(View, List)}
     */
    public static List<View> getAllViews(View view) {
        if (view == null) {
            return Lists.newArrayList();
        }
        List<View> views = Lists.newArrayList();
        return collectViews(view, views);
    }

    /**
     * 简单递归实现获取某个View的所有子View
     * 深度遍历
     */
    private static List<View> collectViews(View view, List<View> views) {
        views.add(view);
        // 获取子View
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; ++i) {
                View childView = ((ViewGroup) view).getChildAt(i);
                // 递归
                collectViews(childView, views);
            }
        }
        return views;
    }

    public static void performClickViewByResId(Activity activity, String resourceId) {
        View parentView = activity.getWindow().peekDecorView();
        List<View> allViews = getAllViews(parentView);
        for (View view : allViews) {
            if (!StringUtils.equals(getViewIdStrings(view), resourceId)) {
                continue;
            }
            // 找到第一个匹配的view
            view.performClick();
            break;
        }
    }

    public static String getViewIdStrings(View view) {
        String message = "";
        int id = view.getId();
        if (id > 0) {
            try {
                message = view.getResources().getResourceName(id);
            } catch (Throwable t) {
                Log.i(TAG, "获取resourceId失败");
            }
        }
        return message;
    }

    public static boolean isAdapterView(View view) {
        if (view == null) {
            return false;
        }
        return AdapterView.class.isAssignableFrom(view.getClass());
    }

    /**
     * 获取listView中item的布局
     *
     * @param pos      位置
     * @param listView listView
     * @return
     */
    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
