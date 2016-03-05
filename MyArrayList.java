/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.UnaryOperator;

/**
 *
 * @author olk
 */
public class MyArrayList<E>
        implements List<E> {
    E array[] = (E[]) new Object[1];
    int size = 0; // number of nonempty cells
    static final int INIT_SIZE = 10;
    
    @Override
    public boolean add(E element) {
        if (size == array.length) {
            expandArray();
        }
        array[size++] = element;
        return true;
    }
    
    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (size == array.length) {
            expandArray();
        }
        
        for (int i = size; i > index; --i) {
            array[i] = array[i - 1];
        }
        array[index] = element;
        ++size;
    }
    
    @Override
    public boolean addAll(Collection <? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        
        // TODO: Come up with a better name for newSize
        int newSize = size + c.size();
        
        if(newSize > array.length) {
            expandArray(newSize);
        }
        
        int i = size;
        Iterator<? extends E> it = c.iterator();
        
        while (it.hasNext()) {
            array[i++] = it.next();
        }
        size = newSize;    
        return false;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (c == null) {
            throw new NullPointerException();
        }
        
        // TODO: Come up with a better name for newSize
        int newSize = size + c.size();
        
        if (newSize > array.length) {
            expandArray(newSize);
        }
        
        for (int i = newSize; i > index + c.size(); --i) {
            array[i] = array[i - c.size()];
        }

        Iterator<? extends E> it = c.iterator();
        
        while (it.hasNext()) {
            array[index++] = it.next();
        }
        size = newSize;
        return false;
    }
    
    @Override
    public void clear() {
        array = (E[]) new Object[1];
        size = 0;
    }
    
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            for (int i = 0; i < size; ++i) {
                if (array[i] == null) {
                    return true;
                }
            }
        }
        else {
            for (int i = 0; i < size; ++i) {
                if (o.equals(array[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        
        Iterator<?> it = c.iterator();
        
        while (it.hasNext()) {
            // QUESTION: Why can't I just use the Logical NOT operator?
            if (contains(it.next())) {
            } else {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyArrayList)) {
            return false;
        }
        if (size != ((MyArrayList) o).size) {
            return false;
        }
        
        Object[] otherArray = ((MyArrayList)o).array;
        
        for (int i = 0; i < size; ++i) {
            if (!array[i].equals(otherArray[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }
    
    @Override
    public int hashCode() {
        int hashCode = 1;
        
        for (int i = 0; i < size; ++i) {
            hashCode = 31 * hashCode +
                    (array[i] == null ? 0 : array[i].hashCode());
        }
        return hashCode;
    }
    
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; ++i) {
                if (array[i] == null) {
                    return i;
                }
            }
        }
        else {
            for (int i = 0; i < size; ++i) {
                if (array[i].equals(o)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new MyArrayListIterator<>();
    }
    
    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; --i) {
                if (array[i] == null) {
                    return i;
                }
            }
        }
        else {
            for (int i = size - 1; i >= 0; --i) {
                if (array[i].equals(o)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    public ListIterator<E> listIterator() {
        // TODO Implement
        return sth;
    }
    
    @Override
    public ListIterator<E> listIterator(int index) {
        // TODO Implement
        return sth;
    }
    
    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        E elem = array[index];
        
        remove_fast(index);
        shrinkArray();
        
        return elem;
    }
    
    @Override
    public boolean remove(Object o) {
        boolean wasChanged = removeFast(o);
        shrinkArray();
        return wasChanged;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        
        boolean wasChanged = false;
        
        // QUESTION: Is it possible that the elements of this Collection
        // would not be the instances of an Object type? (e.g. int, boolean etc)
        for (Object o : c) {
            wasChanged = wasChanged || removeFast(o);
        }
        
        // NOTE: This should be tested for size = 0
        if (array.length >= size * 2) {
            shrinkArray(size);
        }
        
        return wasChanged;
    }
    
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        if (operator == null) {
            throw new NullPointerException();
        }
        
        for (int i = 0; i < size; ++i) {
            array[i] = operator.apply(array[i]);
        }
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        
        // TODO: Come up with better names for newArray and newSize
        E newArray[] = (E[]) new Object[array.length];
        int newSize = 0;
        
        for (int i = 0; i < size; ++i) {
            if (c.contains(array[i])) {
                newArray[size++] = array[i];
            }
        }
        
        boolean wasChanged = (size > newSize);
        
        array = newArray;
        size = newSize;
        
        // NOTE: This should be tested for size = 0
        if (array.length >= size * 2) {
            shrinkArray(size);
        }
        
        return wasChanged;
    }
    
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        E prev = array[index];
        array[index] = element;
        
        return prev;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public void sort(Comparator<? super E> c) {
        // NOTE This is a default method
        // TODO Implement
    }
    
    @Override
    public Spliterator<E> spliterator() {
        // NOTE This is a default method
        // TODO Implement
        return sth;
    }
    
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex >= size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        
        // TODO: Implement
        return this;
    }
    
    @Override
    public Object[] toArray() {
        // TODO: Come up with a better name for retArray
        E[] retArray = (E[]) new Object[size];
        
        for (int i = 0; i < size; ++i) {
            retArray[i] = array[i];
        }
        return retArray;
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Implement
        return (T[]) array;
    }
    
    private void remove_fast(int index) {
        for (int i = index; i < size - 1; ++i) {
            array[i] = array[i + 1];
        }
        --size;
    }
    
    private boolean removeFast(Object o) {
        if (o == null) {
            for (int i = 0; i < size; ++i) {
                if (array[i] == null) {
                    remove_fast(i);
                    --size;
                    return true;
                }
            }
        }
        else {
            for (int i = 0; i < size; ++i) {
                if (o.equals(array[i])) {
                    remove_fast(i);
                    --size;
                    return true;
                }
            }   
        }
        return false;
    }
    
    private void expandArray() {
        array = Arrays.copyOf(array, array.length * 2);
    }
    
    private void expandArray(int size) {
        int len = array.length;
        
        while (len < size) {
            len *= 2;
        }
        array = Arrays.copyOf(array, len);
    }
    
    private void shrinkArray() {
        array = Arrays.copyOf(array, array.length / 2);
    }
    
    private void shrinkArray(int size) {
        int len = array.length;
        
        // PROBLEM: Complexity of division
        // TODO: Optimize this
         while (len / 2 >= size) {
             len /= 2;
         }
         
         array = Arrays.copyOf(array, len);
    }
    
    public void shrinkToFit() {
        array = Arrays.copyOf(array, size);
    }
    
    private class MyArrayListIterator<E> implements Iterator<E> {
        int index = 0;
        
        @Override
        public boolean hasNext() {
            return index < size;
        }
        
        @Override
        public E next() {
            return (E) array[index++];
        }
        
        @Override
        public void remove() {
            // TODO Implement
        }
    }
}