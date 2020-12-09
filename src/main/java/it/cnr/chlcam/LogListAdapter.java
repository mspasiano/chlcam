package it.cnr.chlcam;

import it.cnr.chlcam.model.Result;
import it.cnr.chlcam.model.Results;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class LogListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Results> results;

    public LogListAdapter(Context context, List<Results> results) {
        this.results = results;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return results.get(groupPosition).getResults().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return results.get(groupPosition).getResults().get(childPosition).getId();
    }

    public int getChildrenCount(int groupPosition) {
        return results.get(groupPosition).getResults().size();
    }

    public Object getGroup(int groupPosition) {
        return this.results.get(groupPosition);
    }

    public int getGroupCount() {
        return this.results.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.slidingmenu_sectionview,
                    parent, false);
        }

        TextView textView = (TextView) convertView
                .findViewById(R.id.slidingmenu_section_title);
        textView.setText(((Results) getGroup(groupPosition)).getTitle());

        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.slidingmenu_sectionitem,
                    parent, false);
        }

        Result result = this.results.get(groupPosition).getResults().get(childPosition);
        
        TextView textView = (TextView) convertView
                .findViewById(R.id.slidingmenu_sectionitem_label);
        textView.setText(result.toShortView(convertView.getContext()));

        return convertView;
    }

	public Context getContext() {
		return context;
	}    
}