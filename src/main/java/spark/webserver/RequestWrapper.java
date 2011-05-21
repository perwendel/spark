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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import spark.Request;

class RequestWrapper extends Request {

   private Request delegate;

   public void setDelegate(Request delegate) {
      this.delegate = delegate;
   }
   
   public String requestMethod() {
      return delegate.requestMethod();
   }

   public String scheme() {
      return delegate.scheme();
   }

   public int port() {
      return delegate.port();
   }

   public String pathInfo() {
      return delegate.pathInfo();
   }

   public String contentType() {
      return delegate.contentType();
   }

   public String body() {
      return delegate.body();
   }

   public int contentLength() {
      return delegate.contentLength();
   }

   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   public int hashCode() {
      return delegate.hashCode();
   }

   public final String params(String param) {
      return delegate.params(param);
   }

   public String host() {
      return delegate.host();
   }

   public String ip() {
      return delegate.ip();
   }

   public String queryParams(String queryParam) {
      return delegate.queryParams(queryParam);
   }

   public String headers(String header) {
      return delegate.headers(header);
   }

   public Set<String> queryParams() {
      return delegate.queryParams();
   }

   public Set<String> headers() {
      return delegate.headers();
   }

   public String queryString() {
      return delegate.queryString();
   }

   public HttpServletRequest raw() {
      return delegate.raw();
   }

   public String toString() {
      return delegate.toString();
   }

   public String userAgent() {
      return delegate.userAgent();
   }

   public String url() {
      return delegate.url();
   }
   
}
