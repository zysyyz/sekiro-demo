package com.virjar.injecttest;

import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;

public class ViewModel {
    private String type;
    private String idString;
    private int idInt;
    private String contentDescription;

    private ViewModel parent;
    private Map<String, CharSequence> attribute = new HashMap<>();

    private Set<ViewModel> children = new HashSet<>();

    private View originView;


    public static ViewModel from(View view) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("can not call viewModel parser from sub thread");
        }
        ViewModel ret = new ViewModel();
        ret.originView = view;
        ret.type = view.getClass().getName();
        ret.idInt = view.getId();
        if (ret.idInt > 0) {
            ret.idString = view.getResources().getResourceName(ret.idInt);
        }

        ret.contentDescription = view.getContentDescription().toString();
        if (view instanceof ViewGroup) {
            ret.fillChildren();
        }

        if (view instanceof TextView) {
            ret.parseTextViewAttr();
        }
        if (view instanceof ImageView) {
            ret.parseImageViewAttr();
        }

        return ret;
    }


    private void parseTextViewAttr() {
        TextView textView = (TextView) originView;
        attribute.put("text", textView.getText());
        attribute.put("hint", textView.getHint());
    }

    private void parseImageViewAttr() {
        ImageView imageView = (ImageView) originView;
        try {
            Uri uri = (Uri) XposedHelpers.getObjectField(imageView, "mUri");
            attribute.put("ImageUrl", uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillChildren() {
        ViewGroup viewGroup = (ViewGroup) originView;
        children.clear();
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewModel child = from(viewGroup.getChildAt(i));
            child.parent = this;
            children.add(child);
        }
    }

    private JSONObject dumpToJsonInner() throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("type", type);
        if (idInt > 0) {
            ret.put("idInt", idInt);
            ret.put("idString", idString);
        }
        ret.put("contentDescription", contentDescription);
        for (Map.Entry<String, CharSequence> attEntry : attribute.entrySet()) {
            ret.put(attEntry.getKey(), attEntry.getValue());
        }

        if (children.size() > 0) {
            List<JSONObject> childrenNodes = new ArrayList<>();
            for (ViewModel viewModel : children) {
                childrenNodes.add(viewModel.dumpToJsonInner());
            }
            ret.put("childrenNodes", childrenNodes);
        }
        return ret;
    }


    public String dumpToJson() {
        JSONObject map;
        try {
            map = dumpToJsonInner();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return map.toString();
    }


}
