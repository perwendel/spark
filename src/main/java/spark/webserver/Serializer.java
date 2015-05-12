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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class that serializers and writes the result to given output stream.
 * 
 * @author alex
 */
public abstract class Serializer {

    private Serializer next;

    public void setNext(Serializer serializer) {
        this.next = serializer;
    }

    public void processElement(OutputStream outputStream, Object element) throws IOException {
        if(canHandle(element)) {
            process(outputStream, element);
        } else {
            if(next != null) {
                this.next.processElement(outputStream, element);
            }
        }
    }

    public abstract boolean canHandle(Object element);
    public abstract void process(OutputStream outputStream, Object element) throws IOException;
}
