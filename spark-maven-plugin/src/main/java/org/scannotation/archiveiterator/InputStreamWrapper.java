package org.scannotation.archiveiterator;

import java.io.InputStream;
import java.io.IOException;

/**
 * Delegate to everything but close().  This object will not close the stream
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InputStreamWrapper extends InputStream
{
   private InputStream delegate;

   public InputStreamWrapper(InputStream delegate)
   {
      this.delegate = delegate;
   }

   public int read()
           throws IOException
   {
      return delegate.read();
   }

   public int read(byte[] bytes)
           throws IOException
   {
      return delegate.read(bytes);
   }

   public int read(byte[] bytes, int i, int i1)
           throws IOException
   {
      return delegate.read(bytes, i, i1);
   }

   public long skip(long l)
           throws IOException
   {
      return delegate.skip(l);
   }

   public int available()
           throws IOException
   {
      return delegate.available();
   }

   public void close()
           throws IOException
   {
      // ignored
   }

   public void mark(int i)
   {
      delegate.mark(i);
   }

   public void reset()
           throws IOException
   {
      delegate.reset();
   }

   public boolean markSupported()
   {
      return delegate.markSupported();
   }
}