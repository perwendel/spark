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
package spark.examples;

import static spark.SparkJ8.delete;
import static spark.SparkJ8.get;
import static spark.SparkJ8.post;
import static spark.SparkJ8.put;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple RESTful example showing howto create, get, update and delete book resources.
 *
 * @author Per Wendel
 */
public class BooksJ8 {

    /**
     * Map holding the books
     */
    private static Map<String, Book> books = new HashMap<String, Book> ();

    private static int id = 1;

    public static void main (String[] args) {
        books ();
    }

    public static void books () {

        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent as query parameters e.g. /books?author=Foo&title=Bar
        post("/j8/books", it -> {
            String author = it.queryParams ("author");
            String title = it.queryParams ("title");
            Book book = new Book (author, title);

            books.put (String.valueOf (id), book);

            it.status (201); // 201 Created
            return id++;
        });

        // Gets the book resource for the provided id
        get("/j8/books/:id", it -> {
            Book book = books.get (it.params (":id"));
            if (book != null) {
                return "Title: " + book.getTitle () + ", Author: " + book.getAuthor ();
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        // Updates the book resource for the provided id with new information
        // author and title are sent as query parameters e.g. /books/<id>?author=Foo&title=Bar
        put("/j8/books/:id", it -> {
            String id = it.params (":id");
            Book book = books.get (id);
            if (book != null) {
                String newAuthor = it.queryParams ("author");
                String newTitle = it.queryParams ("title");
                if (newAuthor != null)
                    book.setAuthor (newAuthor);
                if (newTitle != null)
                    book.setTitle (newTitle);
                return "Book with id '" + id + "' updated";
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        // Deletes the book resource for the provided id
        delete("/j8/books/:id", it -> {
            String id = it.params (":id");
            Book book = books.remove (id);
            if (book != null) {
                return "Book with id '" + id + "' deleted";
            }
            else {
                it.status (404); // 404 Not found
                return "Book not found";
            }
        });

        // Gets all available book resources (id's)
        get("/j8/books", it -> {
            String ids = "";
            for (String id : books.keySet ())
                ids += id + " ";
            return ids;
        });
    }
}
