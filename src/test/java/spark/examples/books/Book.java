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
package spark.examples.books;

/**
 * Book domain class
 *
 * @author Per Wendel
 */
public class Book {
    
    private String author;
    private String title;
    
    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }
    
    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
