package org.scannotation.archiveiterator;

import java.io.InputStream;

/**
 * Simpler iterator than java.util.iterator.  Things like JarInputStream does not allow you to implement hasNext()
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface StreamIterator
{
   /**
    * User is resposible for closing the InputStream returned
    *
    * @return null if no more streams left to iterate on
    */
   InputStream next();

   /**
    * Cleanup any open resources of the iterator
    *
    */
   void close();
}