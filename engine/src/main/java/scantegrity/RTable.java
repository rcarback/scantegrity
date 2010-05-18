package scantegrity;

import java.util.ArrayList;
import java.util.Random;



	public class RTable
	{
		ArrayList<Pointer<Integer, Integer>> c_tableRpointersQ;
		ArrayList<Pointer<Integer, Integer>> c_tableRpointersS;
		ArrayList<Boolean> c_tableRflags;
		
		public RTable(int p_ballots, int p_columns)
		{
			c_tableRpointersQ = new ArrayList<Pointer<Integer, Integer>>();
			c_tableRpointersS = new ArrayList<Pointer<Integer, Integer>>();
			c_tableRflags = new ArrayList<Boolean>();
			
			//Initialize R table with default values
			for( int x = 0; x < p_ballots; x++ )
			{
				for( int y = 0; y < p_columns; y++ )
				{
					c_tableRpointersQ.add(new Pointer<Integer, Integer>(x, y));
					c_tableRpointersS.add(new Pointer<Integer, Integer>(x, y));
				}
			}
		}
		
		
	}

