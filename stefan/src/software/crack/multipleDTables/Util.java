package software.crack.multipleDTables;

import java.util.Vector;

public class Util {

	public static <T> Vector<T> myIntersectAll(Vector<T> a, Vector<T> b) {
		Vector<T> aa=new Vector<T>(a);
		Vector<T> bb=new Vector<T>(b);
		Vector<T> ret=new Vector<T>();
		
		for (T k:a) { 
			if (bb.contains(k)) {
				aa.remove(k);
				bb.remove(k);
				ret.add(k);
			}
		}
		return ret;
	}
	
	public static void main(String[] args) {
		Vector<String> v1=new Vector<String>();
		v1.add("1");
		v1.add("2");
		v1.add("3");
		v1.add("4");
		v1.add("5");
		v1.add("6");
		v1.add("7");
		
		
		Vector<String> v2=new Vector<String>();
		
		v2.add("1");
		v2.add("2");
		v2.add("3");
		v2.add("6");
		v2.add("7");
		v2.add("8");
		v2.add("9");
		
		System.out.println(myIntersectAll(v1, v2));
	}
	
}
