package scantegrity;

public class Pointer<T, S>
{
	public Pointer(T left, S right)
	{
		leftPointer = left;
		rightPointer = right;
	}
	public T leftPointer;
	public S rightPointer;
}
