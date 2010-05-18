package scantegrity;

import java.util.ArrayList;
import java.util.Collections;
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
		}
		
		//Switches column pointers when permuting rows in table Q
		public void SwitchQ(int p_index1, int p_index2)
		{
			Pointer<Integer, Integer> l_p1 = c_tableRpointersQ.get(p_index1);
			Pointer<Integer, Integer> l_p2 = c_tableRpointersQ.get(p_index2);
			int temp = l_p1.rightPointer;
			l_p1.rightPointer = l_p2.rightPointer;
			l_p2.rightPointer = temp;
		}
		
		public void Shuffle()
		{
			for( int x = 0; x < c_columns; x++ )
			{
				ShuffleRange(x * c_ballots, (x+1) * c_ballots);
			}
			ShuffleFull();
		}
		
		private void ShuffleFull()
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
		        int j = c_rand.nextInt(i);  // 0 <= j <= i-1 (0-based array)
		        // Swap array elements.
		        Pointer<Integer, Integer> temp = c_tableRpointersS.get(j);
		        c_tableRpointersS.set(j, c_tableRpointersS.get(i-1));
		        c_tableRpointersS.set(i-1, temp);
		    }
		}
		
	}

