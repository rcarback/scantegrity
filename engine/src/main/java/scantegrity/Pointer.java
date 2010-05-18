package scantegrity;

public class Pointer<T, S>
{
	public Pointer(T left, S right)
	{
		row = left;
		column = right;
	}
	public T row;
	public S column;
}
