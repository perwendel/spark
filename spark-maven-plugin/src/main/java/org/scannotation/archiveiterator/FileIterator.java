package org.scannotation.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class FileIterator implements StreamIterator
{
   private ArrayList files;
   private int index = 0;

   public FileIterator(File file, Filter filter)
   {
      files = new ArrayList();
      try
      {
         create(files, file, filter);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected static void create(List list, File dir, Filter filter) throws Exception
   {
      File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         if (files[i].isDirectory())
         {
            create(list, files[i], filter);
         }
         else
         {
            if (filter == null || filter.accepts(files[i].getAbsolutePath()))
            {
               list.add(files[i]);
            }
         }
      }
   }

   public InputStream next()
   {
      if (index >= files.size()) return null;
      File fp = (File) files.get(index++);
      try
      {
         return new FileInputStream(fp);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void close()
   {
   }
}