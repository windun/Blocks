package com.nightsky.Blocks;

import java.util.LinkedList;

public abstract class FibonacciHeap<E> 
{
	DoubleLinkedList<HeapNode<E>> roots;	// Doubly-Linked List connecting all roots
	Array<HeapNode<E>> rank;				// rank[] used during remove operations / consolidation
	int ranked;
	
	private HeapNode<E> top;				// Pointer to the top of the heap
	
	private int num_nodes;

	private String top_label = "top";		// top_label will be used for debug output
											// if it is a min heap, you can call FibonacciHeap ("min", ...) constructor
	private boolean debug_output;			// boolean set when the constructor is called, 'true' if user wants debug output
	
	public FibonacciHeap () 
	{
		top = null;
		roots = new DoubleLinkedList<HeapNode<E>> ();
	}
	
	/**
	 * public FibonacciHeap (String top_label, boolean debug_mode)
	 * 
	 * @param String top_label - label to use in some debug output
	 * @param boolean debug_mode - 'true' to provide debug output
	 */
	public FibonacciHeap (String top_label, boolean debug_mode) 
	{
		top = null;
		roots = new DoubleLinkedList<HeapNode<E>> ();
		this.top_label = top_label;
		debug_output = debug_mode;
	}
	
	/**
	 * abstract HeapNode<E> compare (HeapNode<E> n1, HeapNode<E> n2) - Implementation must 
	 * 		return the Node which should be brought to the top of the tree.
	 * 		(e.g. for a Min Heap, it should return the smaller of the two nodes)
	 * 
	 * @param HeapNode<E> n1
	 * @param HeapNode<E> n2
	 * @return HeapNode<E> which should be brought to the top of the tree.
	 */
	abstract HeapNode<E> compare (HeapNode<E> n1, HeapNode<E> n2);
	
    
	/**
	 * public void insert (E value) - new node will be added to the heap. Inserts are handled
	 * 		lazily - they are not organized into a tree or ordered structure until a remove operation.
	 * 
	 * @param E value
     * 
     * @retrun HeapNode<E> pointer to node that was inserted.
	 */
	public HeapNode<E> insert (E value){
		HeapNode<E> n = new HeapNode<E> (value);
		insert (n);
        return n;
	}
	
	/**
	 * public void insert (HeapNode<E> n) - 
	 * 
	 * @param HeapNode<E> n
	 */
	public void insert (HeapNode<E> n)
	{
		if (top == null)
		{
			top = n;
			roots.add(n);
		}
		else 
		{
			top = compare(n, top);
			roots.add(n);
		}
		num_nodes++;
		if(debug_output) {System.out.print("heap.insert() ");printNodeKey(n);printNodeValue(n);printHeap("-> ");}
		
	}
	
	/**
	 * public HeapNode<E> getTop () - returns a HeapNode pointer to the top of the heap.
	 * 
	 * @return HeapNode<E> at the top of the heap.
	 */
	public HeapNode<E> getTop ()
	{
		return top;
	}
	
	/**
	 * public HeapNode<E> remove () - This will remove and return the top item from the heap.
	 * 
	 * @return HeapNode<E> top
	 */
	public HeapNode<E> remove ()
	{
		
		// If there is no top, return
		if (top == null)
		{
			return null;
		}
		
		// Keep a pointer to the top of the heap, we will return this later
		HeapNode<E> top_ptr = top;
		
		// We will walk across the roots
		HeapNode<E> current = null;
		
		// We will remove the top of the heap, so we must move 'top' pointer to 
		// a node that will exist after we remove 'top'
		if (top == top.getNext())
		{
			current = top.getChildren().getPointer();
		}
		else
		{
			current = (HeapNode<E>) top.getNext();
		}
		
		// #DEBUG OUTPUT - showing which node will be removed
		if(debug_output) {
            printHeap ("heap.remove() Heap: ");
			System.out.print("heap.remove() will remove "); printNodeKey(top_ptr);printNodeValue(top_ptr);System.out.print('\n');
			System.out.print("heap.remove(), "); printNodeKey(top_ptr);printNodeValue(top_ptr); System.out.print(" //next=");printNodeKey((HeapNode<E>) top_ptr.getNext());printNodeValue((HeapNode<E>) top_ptr.getNext());System.out.println();
		}
		
		
		// If the top of the heap has children, add them to the list of root nodes, 
		// then remove the top
		if (top.getChildren().size() != 0) 
		{
			for (HeapNode<E> c : top.getChildren())
			{
				c.setParent(null);
			}
			roots.concatAt(top, top.getChildren());
		}
		roots.remove(top);
		num_nodes--;
		
		// #DEBUG OUTPUT - shows the result of removing the top, the current roots, and whole heap
		if (debug_output)
		{
			System.out.print("heap.remove() roots.getPointer=");
            if (roots.getPointer() != null)
            {
                printNodeKey(roots.getPointer());
                printNodeValue(roots.getPointer());      
            }
            System.out.println();
			printRoots ("heap.remove() following removal, roots=");
			printHeap("heap.remove() following removal, ");
		}
		
		
		/////////////////////////////////////////////////////////////////////
		//
		//	Each root must have a different rank, we are setting up variables for 
		//  accomplishing that here
		int n_ranks;
		if (num_nodes > 0) n_ranks = (int)((2.5 * (Math.log(num_nodes))) + 1);
		else n_ranks = 1;
		if(debug_output) {System.out.println("heap.remove() making rank[" + n_ranks + "] for num_nodes=" + num_nodes);}
		
														// e.g. if roots[] = {A, B, C, D}
		rank = new Array<HeapNode<E>> (n_ranks);		// rank[], if node A has 1 child, then rank[1] = A
		ranked = 0;
		
		
		/////////////////////////////////////////////////////////////////////
		//
		// Consolidate the trees, no trees can have the 
		// same rank (rank = number of children)
		
		top = current;		// Assume the first root should be at the top of the heap, we will analyze all roots
		
		if (roots.size() == 0) 
		{
			top = null;	
		}
		else
		{
			do 
			{				
				
				// #DEBUG OUTPUT - print out which node is 'current'
				if(debug_output) {System.out.print("heap.remove() current("); printNodeKey(current); System.out.println(") requests rank=" + current.getChildren().size());}
				
				
				// If current is a node that is already ranked, 
				// change it to a node from the rank_list (the list of unranked roots)
				while (rank.get(current.getChildren().size()) == current)
				{
					if(debug_output) System.out.println("heap.remove() current was already ranked");
					current = (HeapNode<E>) current.getNext();
					// 'current' has changed. Compare it with the current 'top'	
				}
				top = compare (current, top);
				
				// If the rank[current.children] is empty, register 
				// current with  this rank
				if (rank.get(current.getChildren().size()) == null)
				{
					
					// rank[root.num_children] ---> root
					// e.g.
					//       0 1 2 3 4 5
					//	rank[+| | | | | ]
					//       |
					//       root
					rank.set(current.getChildren().size(), current); ranked++;
					if (debug_output) {System.out.print("heap.remove() current(");printNodeKey(current);System.out.println(") given rank " + current.getChildren().size());}
					current = (HeapNode<E>)current.getNext();
				}
				
				// If there is a root of this rank already, we will have to attach this root to another 
				// root's tree (in this implementation, we will attach it to the first root that is registered
				// in the rank[] array
				// e.g.
				//	       0 1 2 3 4 5
				//	rank[+| | | | | ]
				//       |\
				//    root root				Must attach to another tree!
				else
				{
					if (debug_output) System.out.println("heap.remove() rank not available...");
					
					// Scan rank[] for a tree to attach to
					for (int r=0; r < rank.length(); r++)
					{
						
						HeapNode<E> rank_root = rank.get(r);
						if (rank_root != null)
						{
							rank.remove(r); ranked--;
							current = combine (rank_root, current);
							break;
						}
					}
				}
				if(debug_output) {System.out.print("heap.remove() ");printRanks ();}
				
				
				// 'current' has changed. Compare it with the current 'top'
				top = compare (current, top);
				
			} while (ranked != roots.size());
		}
		if(debug_output)
		{
			printHeap("heap.remove() finished, ");
			printRoots ("heap.remove() finished, roots=");
		}
		return top_ptr;	// temp pointer to top we set at the top of remove ()
	}
	
	
	/**
	 * private void printRanks () - will print the rank[] array in this format '|A| |C| | | | |' if node A is in rank[0] and C in ran[2]
	 */
	private void printRanks () 
	{
		for (int i = 0; i < rank.length(); i++)
		{
			System.out.print("|");
			if (rank.get(i) != null) 
			{
				 printNodeKey(rank.get(i));
			}
			else
			{
				System.out.print(" ");
			}
		}
		System.out.println();
	}
	
	/**
	 * private Node<E> combine (Node<E> root, Node<E> n) - combines 'root' tree and 'n' tree in 
	 * 		the fashion dictated by the function compare (). Whichever is determined to be below the other
	 * 		will be attached to the other's list of children. It will return the node chosen as
	 * 		the new root.
	 * 
	 * @param Node<E> root
	 * @param Node<E> n
	 * @return Node<E> new root;
	 */
	private HeapNode<E> combine (HeapNode<E> root, HeapNode<E> n)
	{
		// If the current node, n, is to be added below
		// the root...
		if (compare (root, n) == root)
		{
			roots.remove(n);
			root.addChild(n); 
			if (debug_output)
			{
				printRoots("heap.combine() result:");
				printHeap("heap.combine() result:"); 
			}
			return root;
		}
		// If the current node must take the root's place
		// and the root will become the current node's 
		// child...
		else 
		{
			roots.remove(root);
			n.addChild(root);
			if (debug_output)
			{
				printRoots("heap.combine() result:");
				printHeap("heap.combine() result:");
			}
			return n;
		}	
		
	}
	
	/**
	 * private void changeKey (HeapNode<E> n, V newValue) - Applies the correct procedure depending on if
	 * the value is increased or decreased: <p>
	 * 
	 * private void moveDown (HeapNode<E> n), or <p>
	 * private void moveDown (HeapNode<E> n) <p>
	 * 
	 * @param HeapNode<E>n
	 * @param V newValue
	 */
	public void changeValue (HeapNode<E> n, E newValue)
	{	
		if(debug_output){System.out.print("heap.changeValue() ");printNodeKey (n);printNodeValue(n); System.out.print(" changing to " + newValue + ",");}
		/*
        if (n.getValue() == newValue) 
		{
			return;
		}*/
		HeapNode<E> n_ = new HeapNode<E>(newValue);
		if (compare(n, n_) == n)
		{
			n.setValue(newValue);
			moveDown (n);
		}
		else
		{
			n.setValue(newValue);
			top = compare (n, top);
			if (n.getParent() != null) moveUp (n);
		}
	}
    
	/**
	 * public void nodeValueDecreased (HeapNode<E> n) - Used to readjust the heap for a node whose value has been changed.
	 * 
	 * @param HeapNode<E> n
	 */
    public void nodeValueDecreased (HeapNode<E> n)
    {
        if(debug_output){System.out.print("heap.changeValue() ");printNodeKey (n);printNodeValue(n); System.out.println();}
        top = compare (n, top);
        if (n.getParent() != null) moveUp (n);
    }
	
	/**
	 * private void moveDown (HeapNode<E> n) - Currently unimplemented. Will be used if 
	 * a node's key must be adjusted in a manner that it has to move down the tree.
	 * 
	 * @param HeapNode<E> n
	 */
	private void moveDown (HeapNode<E> n)
	{
		if(debug_output) System.out.print("down,");
		System.err.println("FibonacciHeap.moveDown() is unimplemented.");
		// TODO
	}
	
	/**
	 * private void moveUp (HeapNode<E> n) - If the node value is such that
	 * it may move up the tree, it will be handled here.
	 * 
	 * @param HeapNode<E> n - the node whose key is changed
	 */
	private void moveUp (HeapNode<E> n)
	{
		if(debug_output) System.out.print("up,");
		HeapNode<E> p = n.getParent();
		if (compare(n, p) == p)
		{
			if(debug_output) System.out.println(" same position");
			return;
		}
		else
		{
			if(debug_output) System.out.print(" move to root,");
			
			// n must be moved to a root position
			// remove it from the parent
			if(debug_output) { printNodeKey (p); System.out.print(" removing "); printNodeKey (n); System.out.print('\n');}
			p.getChildren().remove(n);
			roots.add(n);
			n.setMarked(false);
			top = compare (n, top);
			
			// If the parent was marked, before the cut,
			// move it to a root position as well
			while (p.isMarked() == true && p.getParent() != null)
			{
				HeapNode<E> pp = p.getParent();
				
				if(debug_output){printNodeKey (pp); System.out.print(" removing "); printNodeKey (p); System.out.print('\n');}
				pp.getChildren().remove(p);
				roots.add(p);
				p.setMarked(false);
				top = compare (n, top);
				p = pp;
			}
			if (p.getParent() != null)
			{
				p.setMarked(true);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void printHeap (String title) 
	{
		System.out.print(title);
		System.out.print("{");
		for (HeapNode<E> n : roots)
		{
			System.out.print(" "); printNodeKey(n); printNodeValue(n); System.out.print(" ");
			printChildren (n);
		} 
		System.out.print("} " + top_label + "=");
		if (top != null) 
		{
			printNodeValue (top);
		}
		else
		{
			System.out.print("null");
		}
		System.out.println(" num_nodes=" + num_nodes);
	}
	
	private void printRoots (String title)
	{
		System.out.print(title);
		System.out.print("{");
		for (HeapNode<E> n : roots)
		{
			System.out.print(" "); printNodeKey(n); printNodeValue(n); System.out.print(" ");
		} 
		System.out.print("} " + top_label + "=");
		if (top != null) 
		{
			printNodeValue (top);
		}
		else
		{
			System.out.print("null");
		}
		System.out.println(" num_nodes=" + num_nodes);
	}
	
	/**
	 * abstract void printNodeValue (HeapNode<E> node) - Extending classes must implement this
	 * 		method if the user intends to view debug output by calling super(..., true) in the 
	 * 		extending class' constructor.
	 * 
	 * @param HeapNode<E> node
	 */
	abstract void printNodeValue (HeapNode<E> node);
	
	/**
	 * abstract void printNodeKey (HeapNode<E> node) - Extending classes must implement this
	 * 		method if the user intends to view debug output by calling super(..., true) in the 
	 * 		extending class' constructor.
	 * 
	 * @param HeapNode<E> node
	 */
	abstract void printNodeKey (HeapNode<E> node);
	
	private void printChildren (HeapNode<E> n)
	{
		if (n.getChildren().size() == 0) return;
		
		HeapNode<E> child = (HeapNode<E>) n.getChildren().getPointer();
		System.out.print ("{");
		do 
		{
			System.out.print(" "); printNodeKey(child); printNodeValue(child); System.out.print(" ");
			printChildren (child);
			child = (HeapNode<E>)child.getNext();
		} while (child != n.getChildren().getPointer());
		System.out.print("|" + n.getChildren().size() + "|}");
		
	}
	
	/**
	 * public int size () - returns the size of nodes in the whole heap.
	 * 
	 * @return int - size of the heap.
	 */
	public int size () 
	{
		return num_nodes;
	}
	
	private static int logPhi (int x)
	{
	    return (int) (Math.log(x) / Math.log(1.5));
	}
	
	
}