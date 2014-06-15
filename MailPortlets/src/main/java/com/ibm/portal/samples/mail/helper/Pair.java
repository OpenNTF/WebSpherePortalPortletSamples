package com.ibm.portal.samples.mail.helper;

import java.io.Serializable;
import java.util.Map;

/**
 * Quick implementation of a pairing of values. Sigh, no utility class in java
 * available.
 * 
 * @author cleue
 */
public final class Pair<K, V> implements Map.Entry<K, V>, Serializable {

  /**
   * Helper to contruct the pair. Saves us from repeating the generics classes
   * 
   * @param aKey
   *          key
   * @param aValue
   *          value
   * @return the pair instance
   */
  public static final <K, V> Pair<K, V> pair(final K aKey, final V aValue) {
    return new Pair<K, V>(aKey, aValue);
  }

  /**
   * serialization
   */
  private static final long serialVersionUID = 1L;

  /** the key in the pair */
  private final K key;

  /** the value in the pair */
  private final V value;

  /**
   * hash code
   */
  private final int hashCode;

  private Pair(final K aKey, final V aValue) {
    key = aKey;
    value = aValue;
    // generate the hash code
    hashCode = ((key == null) ? 0 : key.hashCode())
        ^ ((value == null) ? 0 : value.hashCode());

  }

  /*
   * Overriding method... does not require Javadoc.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o) {
    // compare for identity
    if (o == this) {
      return true;
    }
    // check for the interface
    if (!(o instanceof Map.Entry<?, ?>)) {
      return false;
    }
    // access
    final Map.Entry<?, ?> e1 = this, e2 = (Map.Entry<?, ?>) o;
    // compare
    final Object key1 = e1.getKey(), key2 = e2.getKey();
    final Object value1 = e1.getValue(), value2 = e2.getValue();
    // compare
    return ((key1 == null) ? (key2 == null) : key1.equals(key2))
        && ((value1 == null) ? (value2 == null) : value1.equals(value2));
  }

  /*
   * Overriding method... does not require Javadoc.
   * 
   * @see java.util.Map.Entry#getKey()
   */
  @Override
  public K getKey() {
    return key;
  }

  /*
   * Overriding method... does not require Javadoc.
   * 
   * @see java.util.Map.Entry#getValue()
   */
  @Override
  public V getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    // returns the precomputed hash
    return hashCode;
  }

  /*
   * Overriding method... does not require Javadoc.
   * 
   * @see java.util.Map.Entry#setValue(java.lang.Object)
   */
  @Override
  public V setValue(final V v) {
    // not supported
    throw new UnsupportedOperationException();
  }

  /*
   * Overriding method... does not require Javadoc.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    // convert to a readable form
    return "[" + getKey() + ", " + getValue() + "]";
  }
}
