package it.cnr.chlcam;

import it.cnr.chlcam.model.Equation;
import it.cnr.chlcam.model.Equations;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class EquationListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Equations> equations;

    public EquationListAdapter(Context context, List<Equations> equations) {
        this.equations = equations;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return equations.get(groupPosition).getEquations().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return equations.get(groupPosition).getEquations().get(childPosition).getId();
    }

    public int getChildrenCount(int groupPosition) {
        return equations.get(groupPosition).getEquations().size();
    }

    public Object getGroup(int groupPosition) {
        return this.equations.get(groupPosition);
    }

    public int getGroupCount() {
        return this.equations.size();
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
        textView.setText(((Equations) getGroup(groupPosition)).getTitle());

        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.slidingmenu_sectionitem,
                    parent, false);
        }

        Equation oEquation = this.equations.get(groupPosition).getEquations().get(childPosition);
        
        TextView textView = (TextView) convertView
                .findViewById(R.id.slidingmenu_sectionitem_label);
        textView.setText(oEquation.getName());

        return convertView;
    }

	public Context getContext() {
		return context;
	}    
}