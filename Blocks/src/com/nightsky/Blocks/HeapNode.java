package com.nightsky.Blocks;
public class HeapNode<E> implements Node<E>
{
	private E value;
	
	private HeapNode<E> previous;
	private HeapNode<E> next;
	
	private HeapNode<E> parent;
	private DoubleLinkedList<HeapNode<E>> children;

	public boolean deleting;
	
	private boolean marked;
	
	private boolean ranked;
	
	/**
	 * public HeapNode (E val)
	 * 
	 * @param E val
	 */
	public HeapNode (E val) 
	{
		this.value = val;
		parent = null;
		marked = false;
		children = new DoubleLinkedList<HeapNode<E>> ();
	}
	
	public boolean isMarked ()
	{
		return marked;
	}
	
	public void setMarked (boolean mark)
	{
		marked = mark;
	}
	public void setRanked (boolean r)
	{
		ranked = r;
	}
	public boolean isRanked ()
	{
		return ranked;
	}
	
	/**
	 * public void addChild (HeapNode<K,V> n) - This will add a child to this nodes'
	 * 		doubly-linked list.
	 * 
	 * @param HeapNode<K,V> n
	 */
	public void addChild (HeapNode<E> n)
	{
		children.add(n);
		n.setParent(this);
	}
	public DoubleLinkedList<HeapNode<E>> getChildren() {
		return children;
	}
    
    public void setChildren (DoubleLinkedList<HeapNode<E>> children)
    {
        this.children = children;   
    }

	@Override
	public E getValue() {
		return value;
	}

	@Override
	public void setValue(E val) {
		this.value = val;
	}



	@Override
	public Node<E> getNext() {
		return next;
	}

	@Override
	public void setNext(Node<E> n) {
		this.next = (HeapNode<E>)n;
	}

	@Override
	public Node<E> getPrevious() {
		return this.previous;
	}

	@Override
	public void setPrevious(Node<E> n) {
		this.previous = (HeapNode<E>)n;
	}



	@Override
	public HeapNode<E> getParent() {
		return parent;
	}

	@Override
	public void setParent(Node<E> n) {
		this.parent = (HeapNode<E>)n;
	}

    @Override
	public HeapNode<E> copy() {
		return new HeapNode<E> (value);
	}
}