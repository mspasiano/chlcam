package it.cnr.chlcam.dao;

import it.cnr.chlcam.database.CROCAMSchema;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDAO<T> {
	protected final SQLiteDatabase db;

	public AbstractDAO(SQLiteDatabase db) {
		this.db = db;
	}
	
	protected ArrayList<T> cursorToServers(Cursor c) {
		if (c.getCount() == 0) {
			return new ArrayList<T>();
		}

		ArrayList<T> servers = new ArrayList<T>(
				c.getCount());
		c.moveToFirst();

		do {
			T server = createServerFromCursor(c);
			servers.add(server);
		} while (c.moveToNext());
		c.close();
		return servers;
	}

	
	protected abstract T createServerFromCursor(Cursor c);


	protected T cursorToServer(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}
		T server = createServerFromCursor(c);
		c.close();
		return server;
	}

	public boolean delete(long id, String tableName) {
		return db.delete(tableName,
				CROCAMSchema.COLUMN_ID + "=" + id, null) > 0;
	}
	
}
