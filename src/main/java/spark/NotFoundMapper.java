/*
 * Copyright 2016 fcambarieri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark;

/**
 *
 * @author fcambarieri
 */
public class NotFoundMapper {
 
  private static NotFoundMapper defaultInstance;

  public static synchronized NotFoundMapper getInstance() {
    if (defaultInstance == null) {
      defaultInstance = new NotFoundMapper();
    }
    return defaultInstance;
  }
  
  private NotFoundHandler notFoundHandler;
  
  public void map(NotFoundHandler notFoundHandler) {
    this.notFoundHandler = notFoundHandler;
  }

  public NotFoundHandler getNotFoundHandler() {
    return notFoundHandler;
  }
  
  public boolean hasHandler() {
    return notFoundHandler != null;
  }
  
  
}
