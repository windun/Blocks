package com.nightsky.Blocks;

import java.util.Iterator;

public class Array<E> implements Iterable<E>, Iterate<E>{
	private static final String TAG = "GameBase [Array]";
	
	private Object[] a;
	private int length;
	private int size;
	
	public Array (int length)
	{
		this.length = length;
		a = new Object[length];
		size = 0;
	}
	
	public Array () 
	{
		length = 1;
		a = new Object[length];
		size = 0;
	}
	
	public E get(int index)
	{
		if (index > length-1)
		{
			return null;
		}
		return (E) a[index]; 
	}
	
	/**
	 * public void set (int index, E value) - this is an expanding 'set'. If the index doesn't exist,
	 * the array will be enlarged to about 1.5 * the previous size (an O(n) operation) in order to 
	 * accommodate the set request.
	 * 
	 * @param int index
	 * @param E value
	 */
	public E set (int index, E value)
	{
		if (value == null)
		{
			try {
				throw new Exception ("Use remove() to delete items. You are attempting to set index " + index + " to null.");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.exit(-1);
			}
		} 
		else {
			if (index > length-1)
			{
				int length_new = (int)((index + 1) + (0.5 * index));
				Object[] a_new = new Object[length_new];
				for (int i=0; i < length; i++)
				{
					a_new[i] = a[i];
				}
				a = a_new;
				length = length_new;
			}
			if (a[index] == null)
			{
				
				size++;
				//Log.i(TAG, "adding, size increment to " + size);
			}
			else
			{
				//Log.i(TAG, "replacing, size is " + size);
			}
			a[index] = (Object) value;
			return (E) a[index];
		}
		return null;
	}
	
	public void remove (int index)
	{
		a[index] = null;
		size--;
	}
	
	////////////////////////////////////////////////
	//
	//	Iterator-less iterations
	//
	private int pointer = 0;
	private int value_seen = 0;
	@Override
	public void begin ()
	{
		pointer = 0;
		value_seen = 0;
	}
	@Override
	public E next ()
	{
		E value = (E) a[pointer];
		if (value != null)
		{
			value_seen++;
		}
		pointer++;
		return value;
	}
	@Override
	public boolean hasNext ()
	{
		return (value_seen < size) && (pointer < length);
	}
	
	/**
	 * public int length () - !!! Note: this returns the current length of the array, NOT the number of items this array contains!!!
	 * 
	 * @return int - length of this array.
	 */
	public int length ()
	{
		return length;
	}
	
	/**
	 * public int size () - !!! Note: this returns the number of items contained in this array, NOT the length of the array!!!
	 * 
	 * @return int - number of items contained in the array.
	 */
	public int size ()
	{
		return size;
	}

	/**
	 * public Iterator<E> iterator() - this will iterate through the array but will stop after all non-null values 
	 * have been encountered.
	 * 
	 */
	@Override
	public Iterator<E> iterator() {
		Iterator<E> iterator = new Iterator<E> () {
			int current = -1;
			int values_seen = 0;
			
			@Override
			public boolean hasNext() {
				return current < length-1 && values_seen < size;
			}

			@Override
			public E next() {
				current++;
				E obj = (E)a[current];
				if (obj != null)
				{
					values_seen++;
				}
				return obj;
			}

			@Override
			public void remove() {
				System.err.println (TAG + ", Iterator<E>.remove() is not implemented");
			}
		};
		return iterator;
	}

	@Override
	public void clear() {
		a = new Object[length];
		size = 0;
	}
}