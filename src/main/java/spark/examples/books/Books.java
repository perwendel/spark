/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark.examples.books;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * A simple RESTful example showing howto create, get, update and delete book resources.
 * 
 * @author Per Wendel
 */
public class Books {

    /**
     * Map holding the books
     */
    private static Map<String, Book> books = new HashMap<String, Book>();
    
    public static void main(String[] args) {
        
        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent as query parameters e.g. /books?author=Foo&title=Bar
        Spark.post(new Route("/books") {
            Random random = new Random();
            @Override
            public Object handle(Request request, Response response) {
                String author = request.queryParam("author");
                String title = request.queryParam("title");
                Book book = new Book(author, title);
                
                int id = random.nextInt(Integer.MAX_VALUE);
                books.put(String.valueOf(id), book);
                
                response.status(201); // 201 Created
                return id;
            }
        });
        
        // Gets the book resource for the provided id
        Spark.get(new Route("/books/:id") {
            @Override
            public Object handle(Request request, Response response) {
                Book book = books.get(request.params(":id"));
                if (book != null) {
                    return "Title: " + book.getTitle() + ", Author: " + book.getAuthor();
                } else {
                    response.status(404); // 404 Not found
                    return "Book not found";
                }
            }
        });
        
        // Updates the book resource for the provided id with new information
        // author and title are sent as query parameters e.g. /books/<id>?author=Foo&title=Bar
        Spark.put(new Route("/books/:id") {
            @Override
            public Object handle(Request request, Response response) {
                String id = request.params(":id");
                Book book = books.get(id);
                if (book != null) {
                    String newAuthor = request.queryParam("author");
                    String newTitle = request.queryParam("title");
                    if (newAuthor != null) {
                        book.setAuthor(newAuthor);
                    }
                    if (newTitle != null) {
                        book.setTitle(newTitle);
                    }
                    return "Book with id '" + id + "' updated";
                } else {
                    response.status(404); // 404 Not found
                    return "Book not found";
                }
            }
        });
        
        // Deletes the book resource for the provided id 
        Spark.delete(new Route("/books/:id") {
            @Override
            public Object handle(Request request, Response response) {
                String id = request.params(":id");
                Book book = books.remove(id);
                if (book != null) {
                    return "Book with id '" + id + "' deleted";
                } else {
                    response.status(404); // 404 Not found
                    return "Book not found";
                }
            }
        });
        
        // Gets all available book resources (id's)
        Spark.get(new Route("/books") {
            @Override
            public Object handle(Request request, Response response) {
                String ids = "";
                for (String id : books.keySet()) {
                   ids += id + " "; 
                }
                return ids;
            }
        });
        
    }
    
}
