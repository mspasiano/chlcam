package it.cnr.chlcam;

import it.cnr.chlcam.dao.CROCAMDAOEquation;
import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Equation;
import it.cnr.chlcam.model.EquationType;
import it.cnr.chlcam.model.Equations;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class SlidingMenuFragment extends Fragment implements ExpandableListView.OnChildClickListener {
    
    private ExpandableListView sectionListView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        List<Equations> sectionList = createMenu();
                
        View view = inflater.inflate(R.layout.slidingmenu_fragment, container, false);
        this.sectionListView = (ExpandableListView) view.findViewById(R.id.slidingmenu_view);
        this.sectionListView.setGroupIndicator(null);
        
        EquationListAdapter sectionListAdapter = new EquationListAdapter(this.getActivity(), sectionList);
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

    private List<Equations> createMenu() {
        List<Equations> sectionList = new ArrayList<Equations>();
		Database db = Database.create(getActivity().getApplicationContext());		
		CROCAMDAOEquation dao = new CROCAMDAOEquation(db.open());

        Equations oMultipleRegressionModel = new Equations(EquationType.multiple_regression_model.getLabel());
        oMultipleRegressionModel.setEquations(dao.findByType(EquationType.multiple_regression_model));

        Equations oSingleRegressionModel = new Equations(EquationType.single_regression_model.getLabel());
        oSingleRegressionModel.setEquations(dao.findByType(EquationType.single_regression_model));
                
        sectionList.add(oMultipleRegressionModel);
        sectionList.add(oSingleRegressionModel);
        return sectionList;
    }

    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
    	AddEquationActivity addEquationActivity = (AddEquationActivity) getActivity();

    	Database db = Database.create(addEquationActivity.getApplicationContext());
    	CROCAMDAOEquation dao = new CROCAMDAOEquation(db.open());
    	Equation equation = dao.findById(id);
    	if (!equation.isProtetto()) {
        	addEquationActivity.editEquation(equation);
        	addEquationActivity.getSlidingMenu().toggle();    		
    	} else {
    		Toast.makeText(v.getContext(), getString(R.string.equation_protected, equation.getName()), Toast.LENGTH_LONG).show();
    	}
    	return false;
    }
}