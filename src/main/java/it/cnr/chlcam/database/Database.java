package it.cnr.chlcam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
	
	private static final String DATABASE_NAME = "CROCAM";
    
    private static final int DATABASE_VERSION = 1;
    
    private final SQLiteOpenHelper cmisDbHelper;
    private SQLiteDatabase sqliteDb;

    protected Database(Context ctx){
		this.cmisDbHelper = new CROCAMDBAdapterHelper(ctx);
	}
    
    public static Database create(Context ctx) {
    	return new Database(ctx);
	}
    
    public SQLiteDatabase open() {
    	if (sqliteDb == null || !sqliteDb.isOpen()) {
    		sqliteDb = cmisDbHelper.getWritableDatabase();
    	}
		return sqliteDb;
	}

	public void close() {
		sqliteDb.close();
	}
	
	
    private static class CROCAMDBAdapterHelper extends SQLiteOpenHelper {
    	CROCAMDBAdapterHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			CROCAMSchema.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			CROCAMSchema.onUpgrade(db, oldVersion, newVersion);
		}
	}
}
