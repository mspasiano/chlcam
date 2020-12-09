package it.cnr.chlcam.dao;

import it.cnr.chlcam.database.CROCAMSchema;
import it.cnr.chlcam.model.Result;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CROCAMDAOResult extends AbstractDAO<Result> implements DAO<Result> {

	public CROCAMDAOResult(SQLiteDatabase db) {
		super(db);
	}

	public List<Result> findAll() {
		Cursor c = db.query(CROCAMSchema.TABLENAME_RESULTS, new String[] {
				CROCAMSchema.COLUMN_ID,
				CROCAMSchema.COLUMN_DATE,
				CROCAMSchema.COLUMN_ADRESS,
				CROCAMSchema.COLUMN_SPECIE,
				CROCAMSchema.COLUMN_EQUATION,
				CROCAMSchema.COLUMN_LEAF_COUNT,
				CROCAMSchema.COLUMN_LEAF_R,
				CROCAMSchema.COLUMN_LEAF_G,
				CROCAMSchema.COLUMN_LEAF_B,
				CROCAMSchema.COLUMN_AVERAGE_R,
				CROCAMSchema.COLUMN_AVERAGE_G,
				CROCAMSchema.COLUMN_AVERAGE_B,
				CROCAMSchema.COLUMN_LEAF_CHLOROPHYLL,
				CROCAMSchema.COLUMN_AVERAGE_CHLOROPHYLL}, null, null, null, null,
				null);
		return cursorToServers(c);
	}

	public Result findById(long id) {
		Cursor c = db.query(CROCAMSchema.TABLENAME_RESULTS, null,
				CROCAMSchema.COLUMN_ID + " like " + id, null, null,
				null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return cursorToServer(c);
	}

	public Result findMax() {
		Cursor c = db.query(CROCAMSchema.TABLENAME_RESULTS, null, null, null, null, null, CROCAMSchema.COLUMN_ID + " DESC", "1");

		if (c != null) {
			c.moveToFirst();
		}
		return cursorToServer(c);
	}
	
	private ContentValues createContentValues(Result result) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(CROCAMSchema.COLUMN_DATE, result.getDate().getTime());
		updateValues.put(CROCAMSchema.COLUMN_ADRESS, result.getAddress());
		updateValues.put(CROCAMSchema.COLUMN_SPECIE, result.getSpecie());
	    updateValues.put(CROCAMSchema.COLUMN_EQUATION, result.getEquation());
	    updateValues.put(CROCAMSchema.COLUMN_LEAF_COUNT, result.getLeafCount());
	    updateValues.put(CROCAMSchema.COLUMN_LEAF_R, result.getLeafR());
	    updateValues.put(CROCAMSchema.COLUMN_LEAF_G, result.getLeafG());
	    updateValues.put(CROCAMSchema.COLUMN_LEAF_B, result.getLeafB());
	    updateValues.put(CROCAMSchema.COLUMN_AVERAGE_R, result.getAverageR());
	    updateValues.put(CROCAMSchema.COLUMN_AVERAGE_G, result.getAverageG());
	    updateValues.put(CROCAMSchema.COLUMN_AVERAGE_B, result.getAverageB());
	    updateValues.put(CROCAMSchema.COLUMN_LEAF_CHLOROPHYLL, result.getLeafChlorophyll());
	    updateValues.put(CROCAMSchema.COLUMN_AVERAGE_CHLOROPHYLL, result.getAverageChlorophyll());		
		return updateValues;
	}

	public boolean update(Result result) {
		ContentValues updateValues = createContentValues(result);
		return db.update(CROCAMSchema.TABLENAME_RESULTS, updateValues,
				CROCAMSchema.COLUMN_ID + "=" + result.getId(), null) > 0;		
	}

	public long insert(Result result) {
		ContentValues insertValues = createContentValues(result);
		return db.insert(CROCAMSchema.TABLENAME_RESULTS, null, insertValues);
	}

	@SuppressLint("SimpleDateFormat")
	protected Result createServerFromCursor(Cursor c) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(c.getLong(CROCAMSchema.COLUMN_DATE_ID));
		Result server = new Result(
				c.getInt(CROCAMSchema.COLUMN_ID_ID),
				calendar.getTime(),
				c.getString(CROCAMSchema.COLUMN_ADRESS_ID),
				c.getString(CROCAMSchema.COLUMN_SPECIE_ID),
				c.getString(CROCAMSchema.COLUMN_EQUATION_ID),
				c.getInt(CROCAMSchema.COLUMN_LEAF_COUNT_ID),
				c.getInt(CROCAMSchema.COLUMN_LEAF_R_ID),
				c.getInt(CROCAMSchema.COLUMN_LEAF_G_ID),
				c.getInt(CROCAMSchema.COLUMN_LEAF_B_ID),
				c.getInt(CROCAMSchema.COLUMN_AVERAGE_R_ID),
				c.getInt(CROCAMSchema.COLUMN_AVERAGE_G_ID),
				c.getInt(CROCAMSchema.COLUMN_AVERAGE_B_ID),
				c.getDouble(CROCAMSchema.COLUMN_LEAF_CHLOROPHYLL_ID),
				c.getDouble(CROCAMSchema.COLUMN_AVERAGE_CHLOROPHYLL_ID));
		return server;
	}

}
