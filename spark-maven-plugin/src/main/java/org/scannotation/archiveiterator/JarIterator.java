package org.scannotation.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JarIterator implements StreamIterator
{
   JarInputStream jar;
   JarEntry next;
   Filter filter;
   boolean initial = true;
   boolean closed = false;

   public JarIterator(File file, Filter filter) throws IOException
   {
      this(new FileInputStream(file), filter);
   }


   public JarIterator(InputStream is, Filter filter) throws IOException
   {
      this.filter = filter;
      jar = new JarInputStream(is);
   }

   private void setNext()
   {
      initial = true;
      try
      {
         if (next != null) jar.closeEntry();
         next = null;
         do
         {
            next = jar.getNextJarEntry();
         } while (next != null && (next.isDirectory() || (filter == null || !filter.accepts(next.getName()))));
         if (next == null)
         {
            close();
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("failed to browse jar", e);
      }
   }

   public InputStream next()
   {
      if (closed || (next == null && !initial)) return null;
      setNext();
      if (next == null) return null;
      return new InputStreamWrapper(jar);
   }

   public void close()
   {
      try
      {
         closed = true;
         jar.close();
      }
      catch (IOException ignored)
      {

      }

   }
}