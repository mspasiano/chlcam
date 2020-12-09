package it.cnr.chlcam;

import it.cnr.chlcam.dao.CROCAMDAOResult;
import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Result;
import it.cnr.chlcam.model.Results;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class SlidingMenuLogsFragment extends Fragment implements ExpandableListView.OnChildClickListener {
    
    private ExpandableListView sectionListView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        List<Results> sectionList = createMenu();
                
        View view = inflater.inflate(R.layout.slidingmenu_fragment, container, false);
        this.sectionListView = (ExpandableListView) view.findViewById(R.id.slidingmenu_view);
        this.sectionListView.setGroupIndicator(null);
        
        LogListAdapter sectionListAdapter = new LogListAdapter(this.getActivity(), sectionList);
        this.sectionListView.setAdapter(sectionListAdapter); 
        
        this.sectionListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
              public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
              }
            });
        
        this.sectionListView.setOnChildClickListener(this);
        
        int count = sectionListAdapter.getGroupCount();
        for (int position = 0; position < count; position++) {
            this.sectionListView.expandGroup(position);
        }
        
        return view;
    }

    private List<Results> createMenu() {
        List<Results> sectionList = new ArrayList<Results>();
		Database db = Database.create(getActivity().getApplicationContext());		
		CROCAMDAOResult dao = new CROCAMDAOResult(db.open());

        Results oMultipleRegressionModel = new Results("LOGS");
        oMultipleRegressionModel.setResults(dao.findAll());

        sectionList.add(oMultipleRegressionModel);
        return sectionList;
    }

    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
    	ShowLogActivity showLogActivity = (ShowLogActivity) getActivity();

    	Database db = Database.create(showLogActivity.getApplicationContext());
    	CROCAMDAOResult dao = new CROCAMDAOResult(db.open());
    	Result result = dao.findById(id);

    	showLogActivity.editResult(result);
    	
    	showLogActivity.getSlidingMenu().toggle();
    	return false;
    }
}