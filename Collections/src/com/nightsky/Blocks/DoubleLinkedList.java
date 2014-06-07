package com.nightsky.Blocks;
import java.util.Iterator;

public class DoubleLinkedList<N extends Node> implements Iterable<N>, Iterate<N>
{
	private int size = 0;
	private N p;
	private N recycle_pool = null;

	
	public DoubleLinkedList () 
	{

	}

	
	/**
	 * public void add (N n) - O(1) operation. Makes the previous tail point to this node, makes the this
	 * node point to the previous tail. Updates tail pointer to point to this node.
	 * 
	 * @param N n
	 */
	public void add (N n)
	{
		if (size == 0)
		{
			p = n;
			p.setNext(p);
			p.setPrevious(p);
		}
		else
		{
			N p_end = (N)p.getPrevious();
			p_end.setNext(n);
			p.setPrevious(n);
			n.setNext(p);
			n.setPrevious(p_end);
		}
		size++;
	}
	
	/**
	 * N remove (N n) - O(1) operation. Pointers updated to skip this node. This node is returned as a result.
	 * @param n
	 * @return
	 */
	public N remove (N n)
	{
		if (size == 0) return null;
		if (n == p) p = (N) n.getNext();
		
		N prev = (N) n.getPrevious();
		N next = (N) n.getNext();
		prev.setNext(next);
		next.setPrevious(prev);
		
		n.setNext(null);
		n.setPrevious(null);
		n.setParent(null);
		size--;
		if (size == 0) p = null;
		return n;
	}
	
	/**
	 * public N removeLast () - removes the last item in the list
	 * @return
	 */
	public N removeLast ()
	{
		N last = (N) p.getPrevious();
		return remove (last);
	}
	
	/**
	 * public N insertAfter (N target, N new_node)
	 * 
	 * @param target - node you want to attach new_node after
	 * @param new_node
	 * @return
	 */
	public N insertAfter (N target, N new_node)
	{
		N end = (N) target.getNext();
		target.setNext(new_node);
		end.setPrevious(new_node);
		new_node.setNext(end);
		new_node.setPrevious(target);
		size++;
		return new_node;
	}
	
	/**
	 * public void concat (DoubleLinkedList<N> list) - Concatenates 'list' to this linked list. The passed
	 * list is added after this list's tail.
	 * 
	 * @param DoubleLinkedList<N> list
	 */
	public void concat (DoubleLinkedList<N> list)
	{
		N p_end = (N) p.getNext();
		N n = (N) list.getPointer();
		N n_end = (N) n.getNext();
		
		p.setNext(n_end);
		n_end.setPrevious(p);
		n.setNext(p_end);
		p_end.setPrevious(n);
		
		size += list.size();
	}
	
	/**
	 * public void concatAt (N n, DoubleLinkedList<N> list) - will concatenate the linked list 'list'
	 * right after node 'n'
	 * 
	 * @param N n
	 * @param DoubleLinkedList<N> list
	 */
	public void concatAt (N n, DoubleLinkedList<N> list)
	{
		N n_next = (N) n.getNext();
		
		N p_ = (N) list.getPointer();
		N p_next = (N) list.getPointer().getNext();
		
		n.setNext(p_next);
		p_next.setPrevious(n);
		
		p_.setNext(n_next);
		n_next.setPrevious(p_);
		
		size += list.size();
	}
	
	
	public N getPointer () 
	{
		return p;
	}
    
    
    /////////////////////////////////////////////////////////
    //
    //  Iterator-less Iteration
    //
    private N pointer;
    private boolean visited_p;
    @Override
    public void begin ()
    {
        pointer = p;
        visited_p = false;
    }
    @Override
    public boolean hasNext ()
    {
        if (pointer == null) return false;
        if (pointer == p && visited_p) return false;
        return true;
    }
    @Override
    public N next ()
    {
    	visited_p = true;
        N temp = pointer;
        pointer = (N) pointer.getNext ();
        return temp;
    }
	/**
	 * public int size () - total number of nodes in this linked list.
	 * 
	 * @return
	 */
	public int size () 
	{
		return size;
	}
	
	public DoubleLinkedList<N> copy ()
	{
		DoubleLinkedList<N> copy = new DoubleLinkedList<N> ();
		N pointer = p;
		if (pointer == null) return null;
		copy.add((N)pointer.copy());
		pointer = (N) pointer.getNext();
		while (pointer != p)
		{
			copy.add((N)pointer.copy());
			pointer = (N) pointer.getNext();
		}
		return copy;
	}
    
    public void clear ()
    {
    	if (p == null)
    	{
    		return;
    	}
    	if (recycle_pool == null)
    	{
    		recycle_pool = p;
    	}
    	else
    	{
    		N rec_end = (N) recycle_pool.getNext();
    		N n_end = (N) p.getNext();
    		
    		recycle_pool.setNext(n_end);
    		n_end.setPrevious(recycle_pool);
    		p.setNext(rec_end);
    		rec_end.setPrevious(p);
    	}
        p = null;   
        size = 0;
    }
    
    public N getRecycledNode ()
    {
    	if (recycle_pool == null)
    	{
    		return null;
    	}
    	else
    	{
    		if (recycle_pool.getNext() == recycle_pool)
    		{
    			N n = recycle_pool;
    			recycle_pool = null;
    			return n;
    		}
    		N n = (N) recycle_pool.getNext();
    		N prev = (N) n.getPrevious();
    		N next = (N) n.getNext();
    		prev.setNext(next);
    		next.setPrevious(prev);
    		
    		n.setNext(null);
    		n.setPrevious(null);
    		n.setParent(null);
    		return n;
    	}
    }
    
    public boolean availableRecycledNode ()
    {
    	return recycle_pool != null;
    }

	@Override
	public Iterator<N> iterator() {
		Iterator<N> it = new Iterator<N> () 
		{
			private N ptr = null;
			
			@Override
			public boolean hasNext() {
				// if ptr == null, next() hasn't been called
				if (ptr == null)
				{
					return size > 0;
				}
				return ptr.getNext() != p;
			}

			@Override
			public N next() {
				// if ptr == null, next () is being called for the first time
				if (ptr == null)
				{
					ptr = p;
				}
				
				// if ptr != null, move ptr to the next node and return it
				else
				{
					ptr = (N) ptr.getNext();
				}
				return ptr;
			}
			
			private void start ()
			{
				
			}
			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}

			
		};
		return it;
	}
}