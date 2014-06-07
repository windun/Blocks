package com.nightsky.Blocks;

interface Node<E>
{
	public E getValue ();
	
	public void setValue (E val);
	
	public Node<E> getNext ();
	
	public void setNext (Node<E> n);
	
	public Node<E> getPrevious ();
	
	public void setPrevious (Node<E> n);
	
	public Node<E> getParent ();
	
	public void setParent (Node<E> n);
    
    public Node<E> copy ();
}