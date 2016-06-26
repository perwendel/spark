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
package spark.staticfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import spark.resource.AbstractFileResolvingResource;

/**
 * Configures and holds mappings from file extensions to MIME types.
 */
public class MimeType {

    final static String CONTENT_TYPE = "Content-Type";

    private static Map<String, String> mappings = new HashMap<String, String>() {{
        put("au", "audio/basic");
        put("avi", "video/msvideo,video/avi,video/x-msvideo");
        put("bmp", "image/bmp");
        put("bz2", "application/x-bzip2");
        put("css", "text/css");
        put("dtd", "application/xml-dtd");
        put("doc", "application/msword");
        put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        put("eot", "application/vnd.ms-fontobject");
        put("es", "application/ecmascript");
        put("exe", "application/octet-stream");
        put("gif", "image/gif");
        put("gz", "application/x-gzip");
        put("hqx", "application/mac-binhex40");
        put("html", "text/html");
        put("jar", "application/java-archive");
        put("jpg", "image/jpeg");
        put("js", "application/javascript");
        put("midi", "audio/x-midi");
        put("mp3", "audio/mpeg");
        put("mpeg", "video/mpeg");
        put("ogg", "audio/vorbis,application/ogg");
        put("otf", "application/font-otf");
        put("pdf", "application/pdf");
        put("pl", "application/x-perl");
        put("png", "image/png");
        put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        put("ppt", "application/vnd.ms-powerpointtd");
        put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        put("ps", "application/postscript");
        put("qt", "video/quicktime");
        put("ra", "audio/x-pn-realaudio,audio/vnd.rn-realaudio");
        put("rar", "application/x-rar-compressed");
        put("ram", "audio/x-pn-realaudio,audio/vnd.rn-realaudio");
        put("rdf", "application/rdf,application/rdf+xml");
        put("rtf", "application/rtf");
        put("sgml", "text/sgml");
        put("sit", "application/x-stuffit");
        put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
        put("svg", "image/svg+xml");
        put("swf", "application/x-shockwave-flash");
        put("tgz", "application/x-tar");
        put("tiff", "image/tiff");
        put("tsv", "text/tab-separated-values");
        put("ttf", "application/font-ttf");
        put("txt", "text/plain");
        put("wav", "audio/wav,audio/x-wav");
        put("woff", "application/font-woff");
        put("woff2", "application/font-woff2");
        put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        put("xls", "application/vnd.ms-excel");
        put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        put("xml", "application/xml");
        put("zip", "application/zip,application/x-compressed-zip");
    }};

    public static void register(String extension, String mimeType) {
        mappings.put(extension, mimeType);
    }

    public static String fromResource(AbstractFileResolvingResource resource) {
        String filename = Optional.ofNullable(resource.getFilename()).orElse("");
        String fileExtension = filename.replaceAll("^.*\\.(.*)$", "$1");
        return mappings.getOrDefault(fileExtension, "application/octet-stream");
    }

}
