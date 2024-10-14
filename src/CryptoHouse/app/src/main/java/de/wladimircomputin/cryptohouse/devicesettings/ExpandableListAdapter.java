package de.wladimircomputin.cryptohouse.devicesettings;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.devicesettings.PersistentMemory.KVPair;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static class CustomWatcher implements TextWatcher {
        private final KVPair<String, String> item;
        private CustomWatcher(KVPair<String, String> item)
        {
            this.item = item;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            item.value = editable.toString();
            item.changed = true;
        }

    }

    private final Context context;
    // group titles
    final private List<String> expandableList;
    private final HashMap<String, ArrayList<KVPair<String, String>>> expandableListDetail;

    public ExpandableListAdapter(Context context, List<String> expandableList, LinkedHashMap<String, ArrayList<KVPair<String, String>>> expandableListDetail) {
        this.context = context;
        this.expandableList = expandableList;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.expandableListDetail.get(this.expandableList.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int listPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final KVPair<String, String> pair = (KVPair<String, String>) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.settings_list_item, null);
        }

        TextInputLayout keyText = convertView.findViewById(R.id.key_text);
        EditText valueText =  convertView.findViewById(R.id.value_text);
        CustomWatcher oldWatcher = (CustomWatcher)valueText.getTag();
        if(oldWatcher != null) valueText.removeTextChangedListener(oldWatcher);
        keyText.setHint(pair.key);
        valueText.setText(pair.value);
        CustomWatcher newWatcher = new CustomWatcher(pair);
        valueText.setTag(newWatcher);
        valueText.addTextChangedListener(newWatcher);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListDetail.get(this.expandableList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return expandableList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.settings_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}