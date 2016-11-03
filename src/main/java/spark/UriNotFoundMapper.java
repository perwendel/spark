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
public class UriNotFoundMapper {

  /**
   * Holds a default instance for the not found mapper
   */
  private static UriNotFoundMapper defaultInstance;

  /**
   * Returns the default instance for the not found mapper
   *
   * @return Default instance
   */
  public static synchronized UriNotFoundMapper getInstance() {
    if (defaultInstance == null) {
      defaultInstance = new UriNotFoundMapper();
    }
    return defaultInstance;
  }

  private UriNotFoundHandler notFoundHandler;

  /**
   * Set the handler
   * 
   * @param notFoundHandler
   */
  public void map(UriNotFoundHandler notFoundHandler) {
    this.notFoundHandler = notFoundHandler;
  }

  /**
   * @return the handler
   */
  public UriNotFoundHandler getNotFoundHandler() {
    return notFoundHandler;
  }

  /**
   *
   * @return is a handler is configurated
   */
  public boolean hasHandler() {
    return notFoundHandler != null;
  }

}
