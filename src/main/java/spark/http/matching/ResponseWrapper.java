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
package spark.http.matching;

import javax.servlet.http.HttpServletResponse;

import spark.Response;

class ResponseWrapper extends Response {

    static ResponseWrapper create() {
        return new ResponseWrapper();
    }

    private Response delegate;

    private boolean redirected = false;

    private ResponseWrapper() {
        // hidden
    }

    public void setDelegate(Response delegate) {
        this.delegate = delegate;
    }

    Response getDelegate() {
        return delegate;
    }

    @Override
    public void status(int statusCode) {
        delegate.status(statusCode);
    }

    @Override
    public int status() {
        return delegate.status();
    }

    @Override
    public void body(String body) {
        delegate.body(body);
    }

    @Override
    public String body() {
        return delegate.body();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public HttpServletResponse raw() {
        return delegate.raw();
    }

    @Override
    public void redirect(String location) {
        redirected = true;
        delegate.redirect(location);
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        redirected = true;
        delegate.redirect(location, httpStatusCode);
    }

    /**
     * @return true if redirected has been done
     */
    boolean isRedirected() {
        return redirected;
    }

    @Override
    public void header(String header, String value) {
        delegate.header(header, value);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void type(String contentType) {
        delegate.type(contentType);
    }

    @Override
    public String type() {
        return delegate.type();
    }

    @Override
    public void cookie(String name, String value) {
        delegate.cookie(name, value);
    }

    @Override
    public void cookie(String name, String value, int maxAge) {
        delegate.cookie(name, value, maxAge);
    }

    @Override
    public void cookie(String name, String value, int maxAge, boolean secured) {
        delegate.cookie(name, value, maxAge, secured);
    }

    @Override
    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        delegate.cookie(path, name, value, maxAge, secured);
    }

    @Override
    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        delegate.cookie(path, name, value, maxAge, secured, httpOnly);
    }

    @Override
    public void cookie(String domain, String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        delegate.cookie(domain, path, name, value, maxAge, secured, httpOnly);
    }

    @Override
    public void removeCookie(String name) {
        delegate.removeCookie(name);
    }

    @Override
    public void removeCookie(String path, String name) {
        delegate.removeCookie(path, name);
    }
}
