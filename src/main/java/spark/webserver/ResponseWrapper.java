/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.webserver;

import javax.servlet.http.HttpServletResponse;

import spark.Response;

class ResponseWrapper extends Response {

   private Response delegate;

   public void setDelegate(Response delegate) {
      this.delegate = delegate;
   }
   
   public void status(int statusCode) {
      delegate.status(statusCode);
   }

   public void body(String body) {
      delegate.body(body);
   }

   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   public int hashCode() {
      return delegate.hashCode();
   }

   public HttpServletResponse raw() {
      return delegate.raw();
   }

   public void redirect(String location) {
      delegate.redirect(location);
   }

   public void header(String header, String value) {
      delegate.header(header, value);
   }

   public String toString() {
      return delegate.toString();
   }

   public void type(String contentType) {
      delegate.type(contentType);
   }
   
}
