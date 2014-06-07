package com.nightsky.Blocks;

public interface Iterate<E> 
{
	public void begin ();
	
	public boolean hasNext ();
	
	public E next ();
	
	public int size ();
	
	public void clear ();
}