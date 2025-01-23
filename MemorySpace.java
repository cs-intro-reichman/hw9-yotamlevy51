/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {

    private LinkedList allocatedList; // blocks currently allocated
    private LinkedList freeList;      // blocks currently free

    /**
     * Constructs a new managed memory space of a given maximal size.
     */
    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList();
        freeList = new LinkedList();
        // entire memory is initially free
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

    /**
     * Allocates a memory block of length 'length', using a "first-fit" approach:
     * 1) Scans freeList from start to end for a block whose length >= requested 'length'.
     * 2) If found, carve out that portion from the free block and add to allocatedList.
     * 3) If not found, returns -1.
     * 
     * Return value = baseAddress of the allocated block, or -1 if fail.
     */
    public int malloc(int length) {
        ListIterator iterator = freeList.iterator();
		while (iterator.hasNext()) {
			MemoryBlock freeBlock = iterator.next();

			if (freeBlock.length >= length) { 
				MemoryBlock allocated = new MemoryBlock(freeBlock.baseAddress, length);
				allocatedList.addLast(allocated);

				freeBlock.baseAddress += length;
				freeBlock.length -= length;
				if (freeBlock.length == 0) {
					freeList.remove(freeBlock);
				}
				return allocated.baseAddress;
			}
		}
		return -1;
    }

    /**
     * Frees the memory block whose base address == address.
     * 1) If allocatedList is empty => throw new IllegalArgumentException("index must be between 0 and size");
     * 2) Otherwise, search for the block in allocatedList:
     *    - if found, remove it from allocatedList and add it to freeList (at the end)
     *    - if not found, do nothing.
     */
    public void free(int address) {
        if (allocatedList.getSize() == 0) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        
		ListIterator iterator = allocatedList.iterator();
		while (iterator.hasNext()) {
			MemoryBlock allocated = iterator.next();

			if (address == allocated.baseAddress) {
				allocatedList.remove(allocated);
				freeList.addLast(allocated);
				return;
            }
        }
	}

	 /**
     * Returns a textual representation:
     * 1) First line: freeList.toString()  (e.g. "(0 , 20) (20 , 30) ")
     * 2) Followed by "\n"
     * 3) Second line: allocatedList.toString() (e.g. "(50 , 10) (60 , 40) ")
     *
     * If freeList is empty => first line is "" => then appended "\n"
     * If allocatedList is empty => second line is "" => so the result might end with newline only.
     */
    public String toString() {
        return freeList.toString() + "\n" + allocatedList.toString();
    }

    /**
     * Performs defragmentation of the freeList:
     * 1) If freeList size < 2 => do nothing
     * 2) gather the free blocks in an array, sort by baseAddress
     * 3) rebuild freeList in sorted order
     * 4) merge consecutive blocks
     */
    public void defrag() {
		ListIterator iterator1 = freeList.iterator();
		
		while (iterator1.hasNext()) {
			MemoryBlock freeBlock = iterator1.next();
			ListIterator iterator2 = freeList.iterator();
			int newAddress = freeBlock.baseAddress + freeBlock.length;

			while (iterator2.hasNext()) {
				MemoryBlock checkedFreeBlock = iterator2.next();
				if (checkedFreeBlock.baseAddress == newAddress) {
					System.out.println("merging " + freeBlock + " with " + checkedFreeBlock);
					freeBlock.length += checkedFreeBlock.length;
					freeList.remove(checkedFreeBlock);
					iterator1 = freeList.iterator();
				}
			}
		}
	}
}
