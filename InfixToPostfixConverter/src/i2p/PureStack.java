package i2p;

public interface PureStack<T> {

	void push(T item);

	T pop();

	T peek();

	boolean isEmpty();

	int size();
}
