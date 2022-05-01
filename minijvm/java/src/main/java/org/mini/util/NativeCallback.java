package org.mini.util;

import java.util.HashMap;

/**
 *
 * @author Paul Kashofer (Starcommander@github)
 */
public class NativeCallback
{
  static HashMap<Long,CallbackItem> list = new HashMap<>();
  static long internalID = 0;
  
  private static synchronized long getId()
  {
    internalID++;
    return internalID;
  }
  
  /** Adds a callback that allows to execute an object-instance from native side.
   * <br>Just ensure, native-side will execute native_callback(l,s) function when finished.
   * @param cb The Callback-obj to register.
   * @return The ID for use in callback. */
  public static long registerCallback(CallbackItem cb)
  {
    long newId = getId();
    list.put(newId, cb);
    return newId;
  }
  
  /** Called from native side.
   * @param data A returned value if provided.
   * @param id The id associated to the callback. */
  public static void doCallback(String data, long id)
  {
    CallbackItem matchedItem = list.get(id);
    if (matchedItem != null)
    {
        boolean isLastCall = matchedItem.callback(data);
        if (isLastCall) { list.remove(id); }
    }
  }
  
  public static interface CallbackItem
  {
    /** Callback from native side.
     * @param data Response data from native side.
     * @return True, if this CallbackItem can be cleaned up. **/
    public boolean callback(String data);
  }
}
