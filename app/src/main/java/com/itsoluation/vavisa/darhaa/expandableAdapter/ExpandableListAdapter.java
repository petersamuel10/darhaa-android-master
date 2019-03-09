package com.itsoluation.vavisa.darhaa.expandableAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itsoluation.vavisa.darhaa.R;
import com.itsoluation.vavisa.darhaa.common.OptionsSelection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle,childId;
    private HashMap<String, List<String>> expandableListDetail;
    private int id=-1;
    private String option_id;
    private boolean isCheckBox;
    private boolean isRequired;
    final List<String> child;

    private List<RadioButton> radioButtons = new ArrayList<>();

    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
                                 HashMap<String, List<String>> expandableListDetail, boolean isCK,
                                 Boolean isRequired, String option_id, List<String> childListId) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.isCheckBox = isCK;
        this.isRequired = isRequired;
        this.option_id = option_id;
        this.childId = childListId;

        Log.i("xxxxxx","hihioh");

        child = new ArrayList<>();

        if(isRequired)
            child.add("true");
        else
            child.add("false");
        OptionsSelection.optionsSelection.put(option_id,child);
        child.add(expandableListTitle.get(0));
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             final boolean isLastChild, View convertView, ViewGroup parent) {


        final String item_txt = (String) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(isCheckBox)
                convertView = layoutInflater.inflate(R.layout.list_item_ch, null);
            else
                convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        if(isCheckBox){

            CheckBox item = convertView.findViewById(R.id.Child_ch);
            item.setTag(expandedListPosition);
            item.setText(item_txt);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // OptionsSelection.optionsSelection.remove(option_id);

                    if(((CheckBox)v).isChecked()){
                        child.add(childId.get(expandedListPosition));
                        child.add(item_txt);
                    }else{
                        if(OptionsSelection.optionsSelection.containsValue(child)){
                            child.remove(childId.get(expandedListPosition));
                            child.remove(item_txt);
                        }
                    }
                }
            });

            OptionsSelection.optionsSelection.put(option_id,child);

        }else{
            RadioButton item = convertView.findViewById(R.id.Child_ch);
            item.setTag(expandedListPosition);
            item.setText(item_txt);
            radioButtons.add(item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();

                    if(p != id){
                        id = p;
                        for ( RadioButton radioButton: radioButtons) {
                            radioButton.setChecked(false);
                            if(child.size()>2) {
                                child.remove(2);
                                child.remove(2);
                            }
                        }
                       // OptionsSelection.optionsSelection.remove(option_id);
                        ((RadioButton)v).setChecked(true);
                        child.add(childId.get(expandedListPosition));
                        child.add(item_txt);

                    }
                }
            });
            OptionsSelection.optionsSelection.put(option_id,child);
        }

        return convertView;
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView title_txt = (TextView) convertView
                .findViewById(R.id.listTitle);

        title_txt.setTypeface(null, Typeface.BOLD);
        title_txt.setText(listTitle);

        ImageView item_required = (ImageView) convertView
                .findViewById(R.id.item_required);

        if(isRequired)
            item_required.setVisibility(View.VISIBLE);

        if(isExpanded)
            title_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_up,0);
        else
            title_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_down,0);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return false;
    }
}
