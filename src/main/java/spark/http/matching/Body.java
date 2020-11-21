/*
 * Copyright 2016 - Per Wendel
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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.serialization.SerializerChain;
import spark.utils.GzipUtils;



/**.
 * Represents the 'body'
 */
final class Body {

    private Object content;
    //CS304 Issue link: https://github.com/perwendel/spark/issues/1022
    private boolean useEmpty = false;


    public static Body create() {
        return new Body();
    }

    private Body() {

    }

    public boolean notSet() {
        return content == null;
    }

    public boolean isSet() {
        return content != null;
    }

    public Object get() {
        return content;
    }

    public void set(Object content) {
        this.content = content;
    }


    /**.
     * CS304 Issue link: https://github.com/perwendel/spark/issues/1022
     * modify line 80
     * get default response content type
     * if useEmpty is set, content type can be null
     * otherwise null content type will be set to default
     * @param httpResponse response to the request
     * @param serializerChain Serialize the body to output stream
     * @param httpRequest received request
     * @throws IOException throw exception if there is Input/Output error.
     */
    public void serializeTo(HttpServletResponse httpResponse,
                            SerializerChain serializerChain,
                            HttpServletRequest httpRequest) throws IOException {

        if (!httpResponse.isCommitted()) {
            if (httpResponse.getContentType() == null && !useEmpty) {
                //CS304 Issue link: https://github.com/perwendel/spark/issues/911
                String type = Configuration.getDefaultcontentype();
                httpResponse.setContentType(type);
            }

            // Check if GZIP is wanted/accepted and in that case handle that
            OutputStream responseStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, true);

            // Serialize the body to output stream
            serializerChain.process(responseStream, content);

            responseStream.flush(); // needed for GZIP stream. Not sure where the HTTP response actually gets cleaned up
            responseStream.close(); // needed for GZIP
        }
    }

    /**
     * CS304 Issue link: https://github.com/perwendel/spark/issues/1022
     * setter for boolean useEmpty
     * @param useEmpty {@code boolean} the value that will change to useEmpty
     */
    public void setUseEmpty(boolean useEmpty) {
        this.useEmpty = useEmpty;
    }


}
