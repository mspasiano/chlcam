package it.cnr.chlcam.database;

import it.cnr.chlcam.dao.CROCAMDAOEquation;
import it.cnr.chlcam.model.EquationType;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CROCAMSchema {

	public static final String TABLENAME_EQUATIONS = "equations";
	public static final String TABLENAME_RESULTS = "results";
	
	public static final String COLUMN_ID = "id";
	public static final int COLUMN_ID_ID = 0;

	/**
	 * Equation column
	 */
    public static final String COLUMN_NAME = "name";
    public static final int COLUMN_NAME_ID = 1;
    
    public static final String COLUMN_EXPRESSION = "expression";
    public static final int COLUMN_EXPRESSION_ID = 2;
    
    public static final String COLUMN_TYPE = "type";
    public static final int COLUMN_TYPE_ID = 3;	

    public static final String COLUMN_PROTECTED = "protected";
    public static final int COLUMN_PROTECTED_ID = 4;	

    /**
	 * Result column
	 */
    public static final String COLUMN_DATE = "date";
    public static final int COLUMN_DATE_ID = 1;

    public static final String COLUMN_ADRESS = "adress";
    public static final int COLUMN_ADRESS_ID = 2;

    public static final String COLUMN_SPECIE = "specie";
    public static final int COLUMN_SPECIE_ID = 3;
    
    public static final String COLUMN_EQUATION = "equation";
    public static final int COLUMN_EQUATION_ID = 4;

    public static final String COLUMN_LEAF_COUNT = "leafCount";
    public static final int COLUMN_LEAF_COUNT_ID = 5;

    public static final String COLUMN_LEAF_R = "leafR";
    public static final int COLUMN_LEAF_R_ID = 6;

    public static final String COLUMN_LEAF_G = "leafG";
    public static final int COLUMN_LEAF_G_ID = 7;

    public static final String COLUMN_LEAF_B = "leafB";
    public static final int COLUMN_LEAF_B_ID = 8;

    public static final String COLUMN_AVERAGE_R = "averageR";
    public static final int COLUMN_AVERAGE_R_ID = 9;
    
    public static final String COLUMN_AVERAGE_G = "averageG";
    public static final int COLUMN_AVERAGE_G_ID = 10;

    public static final String COLUMN_AVERAGE_B = "averageB";
    public static final int COLUMN_AVERAGE_B_ID = 11;

    public static final String COLUMN_LEAF_CHLOROPHYLL = "leafChlorophyll";
    public static final int COLUMN_LEAF_CHLOROPHYLL_ID = 12;

    public static final String COLUMN_AVERAGE_CHLOROPHYLL = "averageChlorophyll";
    public static final int COLUMN_AVERAGE_CHLOROPHYLL_ID = 13;

    
    private static final String QUERY_TABLE_EQUATIONS_CREATE =
    	  "create table " + TABLENAME_EQUATIONS + " (" 
      	+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
      	+ COLUMN_NAME + " TEXT NOT NULL,"
      	+ COLUMN_EXPRESSION + " TEXT NOT NULL,"
      	+ COLUMN_TYPE + " TEXT NOT NULL,"
      	+ COLUMN_PROTECTED + " TEXT NOT NULL"
      	+ ");";

    private static final String QUERY_TABLE_RESULTS_CREATE =
      	  "create table " + TABLENAME_RESULTS + " (" 
        	+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        	+ COLUMN_DATE + " TEXT NOT NULL,"
        	+ COLUMN_ADRESS + " TEXT NULL,"
        	+ COLUMN_SPECIE + " TEXT NULL,"
        	+ COLUMN_EQUATION + " TEXT NULL,"
        	+ COLUMN_LEAF_COUNT + " INTEGER NULL,"
        	+ COLUMN_LEAF_R + " INTEGER NULL,"
        	+ COLUMN_LEAF_G + " INTEGER NULL,"
        	+ COLUMN_LEAF_B + " INTEGER NULL,"
        	+ COLUMN_AVERAGE_R + " INTEGER NULL,"
        	+ COLUMN_AVERAGE_G + " INTEGER NULL,"
        	+ COLUMN_AVERAGE_B + " INTEGER NULL,"
        	+ COLUMN_LEAF_CHLOROPHYLL + " REAL NULL,"
        	+ COLUMN_AVERAGE_CHLOROPHYLL + " REAL NULL"        	
        	+ ");";
    
	private static final String QUERY_TABLE_EQUATIONS_DROP = "DROP TABLE IF EXISTS "
			+ TABLENAME_EQUATIONS;
	private static final String QUERY_TABLE_RESULTS_DROP = "DROP TABLE IF EXISTS "
			+ TABLENAME_RESULTS;
    
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(CROCAMSchema.QUERY_TABLE_EQUATIONS_CREATE);
		db.execSQL(CROCAMSchema.QUERY_TABLE_RESULTS_CREATE);

		CROCAMDAOEquation serverDao = new CROCAMDAOEquation(db); 
		serverDao.insert("Quinoa", "exp(-0.0102*R-0.0287*G+0.00771*B+5.5337)", EquationType.multiple_regression_model, true); 
		serverDao.insert("Amaranth", "exp(-0.0217*R-0.0099*G+0.0064*B+4.2319)", EquationType.multiple_regression_model, true); 
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("ServersSchema: ", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL(QUERY_TABLE_EQUATIONS_DROP);
		db.execSQL(QUERY_TABLE_RESULTS_DROP);
		onCreate(db);
	}
	
}