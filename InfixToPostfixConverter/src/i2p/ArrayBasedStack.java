
package i2p;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayBasedStack<E> implements PureStack<E> {
	private static final int DEFAULT_SIZE = 10;

	private int top;

	private E[] data;

	public ArrayBasedStack() {
		data = (E[]) new Object[DEFAULT_SIZE];
	}

	@Override
	public boolean isEmpty() {
		return top == 0;
	}

	@Override
	public E peek() {
		if (top == 0)
			throw new NoSuchElementException();
		return data[top - 1];
	}

	@Override
	public E pop() {
		if (top == 0)
			throw new NoSuchElementException();
		E ret = data[--top];
		data[top] = null;
		return ret;
	}

	@Override
	public void push(E item) {
		checkCapacity();
		data[top++] = item;
	}

	@Override
	public int size() {
		return top;
	}

	private void checkCapacity() {
		if (top == data.length) {
			data = Arrays.copyOf(data, data.length * 2);
		}
	}
}
