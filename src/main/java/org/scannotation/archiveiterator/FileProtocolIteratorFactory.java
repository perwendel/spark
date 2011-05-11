package org.scannotation.archiveiterator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileProtocolIteratorFactory implements DirectoryIteratorFactory
{

   public StreamIterator create(URL url, Filter filter) throws IOException
   {
      File f = new File(url.getPath());
      if (f.isDirectory())
      {
         return new FileIterator(f, filter);
      }
      else
      {
         return new JarIterator(url.openStream(), filter);
      }
   }
}