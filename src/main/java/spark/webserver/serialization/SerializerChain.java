/*
 * Copyright 2015 - Per Wendel
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
package spark.webserver.serialization;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Chain of serializers for the output.
 */
public class SerializerChain {

    private Serializer root;

    /**
     * Constructs a serializer chain.
     */
    public SerializerChain() {

        DefaultSerializer defaultSerializer = new DefaultSerializer();

        InputStreamSerializer inputStreamSerializer = new InputStreamSerializer();
        inputStreamSerializer.setNext(defaultSerializer);

        BytesSerializer bytesSerializer = new BytesSerializer();
        bytesSerializer.setNext(inputStreamSerializer);

        this.root = bytesSerializer;
    }

    /**
     * Process the output.
     *
     * @param outputStream the output stream to write to.
     * @param element the element to serialize.
     * @throws IOException in the case of IO error.
     */
    public void process(OutputStream outputStream, Object element) throws IOException {
        this.root.processElement(outputStream, element);
    }

}
