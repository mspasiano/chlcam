package it.cnr.chlcam.dao;

import it.cnr.chlcam.database.CROCAMSchema;
import it.cnr.chlcam.model.Equation;
import it.cnr.chlcam.model.EquationType;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CROCAMDAOEquation extends AbstractDAO<Equation> implements DAO<Equation> {

	public CROCAMDAOEquation(SQLiteDatabase db) {
		super(db);
	}

	public long insert(String name, String expression, EquationType type, boolean protetto) {
		ContentValues insertValues = createContentValues(name, expression, type, protetto);
		return db.insert(CROCAMSchema.TABLENAME_EQUATIONS, null, insertValues);
	}

	public boolean update(long id, String name, String expression, EquationType type, boolean protetto) {
		ContentValues updateValues = createContentValues(name, expression, type, protetto);

		return db.update(CROCAMSchema.TABLENAME_EQUATIONS, updateValues,
				CROCAMSchema.COLUMN_ID + "=" + id, null) > 0;
	}


	public List<Equation> findAll() {
		Cursor c = db.query(CROCAMSchema.TABLENAME_EQUATIONS, new String[] {
				CROCAMSchema.COLUMN_ID,
				CROCAMSchema.COLUMN_NAME,
				CROCAMSchema.COLUMN_EXPRESSION,
				CROCAMSchema.COLUMN_TYPE,
				CROCAMSchema.COLUMN_PROTECTED}, null, null, null, null,
				null);
		return cursorToServers(c);
	}

	public Equation findById(long id) {
		Cursor c = db.query(CROCAMSchema.TABLENAME_EQUATIONS, null,
				CROCAMSchema.COLUMN_ID + " like " + id, null, null,
				null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return cursorToServer(c);
	}

	public List<Equation> findByType(EquationType type) {
		Cursor c = db.query(CROCAMSchema.TABLENAME_EQUATIONS, new String[] {
				CROCAMSchema.COLUMN_ID,
				CROCAMSchema.COLUMN_NAME,
				CROCAMSchema.COLUMN_EXPRESSION,
				CROCAMSchema.COLUMN_TYPE,
				CROCAMSchema.COLUMN_PROTECTED},
				CROCAMSchema.COLUMN_TYPE + " = \"" + type.name() +"\"", null, null,
				null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return cursorToServers(c);		
	}
	
	private ContentValues createContentValues(String name, String expression, EquationType type, boolean protetto) {
		ContentValues updateValues = new ContentValues();

		updateValues.put(CROCAMSchema.COLUMN_NAME, name);
		updateValues.put(CROCAMSchema.COLUMN_EXPRESSION, expression);
		updateValues.put(CROCAMSchema.COLUMN_TYPE, type.name());
		updateValues.put(CROCAMSchema.COLUMN_PROTECTED, String.valueOf(protetto));
		return updateValues;
	}

	public boolean update(Equation equation) {
		return update(equation.getId(), equation.getName(), equation.getExpression(),equation.getType(), equation.isProtetto());
	}

	public long insert(Equation equation) {
		return insert(equation.getName(), equation.getExpression(), equation.getType(), equation.isProtetto());
	}

	protected Equation createServerFromCursor(Cursor c) {
		Equation server = new Equation(
				c.getInt(CROCAMSchema.COLUMN_ID_ID),
				c.getString(CROCAMSchema.COLUMN_NAME_ID),
				c.getString(CROCAMSchema.COLUMN_EXPRESSION_ID),
				EquationType.valueOf(c
						.getString(CROCAMSchema.COLUMN_TYPE_ID)),
				Boolean.valueOf(c.getString(CROCAMSchema.COLUMN_PROTECTED_ID)));
		return server;
	}

}
