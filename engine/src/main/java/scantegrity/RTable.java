package scantegrity;

import java.util.ArrayList;
import java.util.Random;



	public class RTable
	{
		ArrayList<Pointer<Integer, Integer>> c_tableRpointersQ;
		ArrayList<Pointer<Integer, Integer>> c_tableRpointersS;
		ArrayList<Boolean> c_tableRflags;
		
		int c_ballots;
		int c_columns;
		
		Random c_rand;
		
		public RTable(int p_ballots, int p_columns, Random p_rand)
		{
			c_tableRpointersQ = new ArrayList<Pointer<Integer, Integer>>();
			c_tableRpointersS = new ArrayList<Pointer<Integer, Integer>>();
			c_tableRflags = new ArrayList<Boolean>();
			
			c_ballots = p_ballots;
			c_columns = p_columns;
			
			c_rand = p_rand;
			
			//Initialize R table with default values, grouped by columns so they can be permuted by the engine
			for( int x = 0; x < p_columns; x++ )
			{
				for( int y = 0; y < p_ballots; y++ )
				{
					c_tableRpointersQ.add(new Pointer<Integer, Integer>(y, x));
					c_tableRpointersS.add(new Pointer<Integer, Integer>(y, x));
				}
			}
			for( int x = 0; x < c_columns; x++ )
			{
				ShuffleRange(x * c_ballots, (x+1) * c_ballots);
			}
		}
		
		//Switches column pointers when permuting rows in table Q
		public void SwitchQ(int p_index1, int p_index2)
		{
			Pointer<Integer, Integer> l_p1 = c_tableRpointersQ.get(p_index1);
			Pointer<Integer, Integer> l_p2 = c_tableRpointersQ.get(p_index2);
			int temp = l_p1.column;
			l_p1.column = l_p2.column;
			l_p2.column = temp;
		}
		
		public void Shuffle()
		{
		    for (int i = c_tableRpointersS.size(); i > 1; i--) {
		        int j = c_rand.nextInt(i);
		        
		        Pointer<Integer, Integer> temp = c_tableRpointersS.get(j);
		        c_tableRpointersS.set(j, c_tableRpointersS.get(i-1));
		        c_tableRpointersS.set(i-1, temp);
		        
		        temp = c_tableRpointersQ.get(j);
		        c_tableRpointersQ.set(j, c_tableRpointersQ.get(i-1));
		        c_tableRpointersQ.set(i-1, temp);
		    }
		}
		
		private void ShuffleRange(int x, int y)
		{
		    // i is the number of items remaining to be shuffled.
		    for (int i = y; i > x + 1; i--) {
		        // Pick a random element to swap with the i-th element.
		        int j = c_rand.nextInt(i - x) + x;  // 0 <= j <= i-1 (0-based array)
		        // Swap array elements.
		        Pointer<Integer, Integer> temp = c_tableRpointersS.get(j);
		        c_tableRpointersS.set(j, c_tableRpointersS.get(i-1));
		        c_tableRpointersS.set(i-1, temp);
		    }
		}
		
		public void Print()
		{
			for( int x = 0; x < c_tableRpointersQ.size(); x++ )
			{
				Pointer<Integer, Integer> l_p = c_tableRpointersQ.get(x);
				System.out.println("Q: " + l_p.row + " " + l_p.column);
				l_p = c_tableRpointersS.get(x);
				System.out.println("S: " + l_p.row + " " + l_p.column);
			}
		}

		//Will test table sanity if each row of confirmation codes is 0 1 2 3 etc.
		public void Test(String[][] p_tableQ) {
			for( int x = 0; x < c_tableRpointersS.size(); x++ )
			{
				Pointer<Integer, Integer> l_p = c_tableRpointersQ.get(x);
				int rowIndex = l_p.row;
				int columnIndex = l_p.column;
				l_p = c_tableRpointersS.get(x);
				int finalColumn = l_p.column;
				
				if(Integer.parseInt(p_tableQ[rowIndex][columnIndex]) != finalColumn)
				{
					System.out.println("FAILED: " + rowIndex + " " + columnIndex + " : " + finalColumn);
				}
			}
		}
		
	}

