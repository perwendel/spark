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

    private State state = State.NOT_PROCESSED;

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
        state = State.REDIRECTED;
        delegate.redirect(location);
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        state = State.REDIRECTED;
        delegate.redirect(location, httpStatusCode);
    }

    /**
     * @return true if redirected has been done
     */
    boolean isRedirected() {
        return state == State.REDIRECTED;
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
    public void removeCookie(String name) {
        delegate.removeCookie(name);
    }

    public void changeStateTo(State newState){
        this.state = newState;
    }

    public boolean inStateOf(State possibleState){
        return this.state == possibleState;
    }

    public boolean isNotInStateOf(State possibleState){
        return this.state != possibleState;
    }

    public boolean isNotPrepared(){
        return state.notPrepared();
    }

    public boolean isPrepared(){
        return state.prepared();
    }

    public enum State{
        NOT_PROCESSED, PROCESSED,
        EXCEPTION_HANDLED, EXCEPTION_NOT_HANDLED,
        HALT,
        REDIRECTED;

        public boolean prepared(){
            return this == PROCESSED
                    || this == EXCEPTION_HANDLED
                    || this == HALT
                    || this == REDIRECTED;
        }

        public boolean notPrepared(){
            return !prepared();
        }
    }
}
