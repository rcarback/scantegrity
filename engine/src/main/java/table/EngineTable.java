package table;

import java.util.ArrayList;

public interface EngineTable {

	public ArrayList<Object> getRow(int index);
	public ArrayList<Object> getRow(Object key);
	public void insertRow(ArrayList<Object> row);
	public ArrayList<ArrayList<Object>> getAllRows();
}
