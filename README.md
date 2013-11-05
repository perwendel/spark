Spark - a Sinatra inspired web framework
==============================================

NEWS: Spark is now available on Maven central!!! :

```xml
    <dependency>
        <groupId>com.sparkjava</groupId>
        <artifactId>spark-core</artifactId>
        <version>1.1.1</version>
    </dependency>
```

NEWS: Spark google group created:
https://groups.google.com/d/forum/sparkjava

Examples can also be viewed on: http://code.google.com/p/spark-java/

Temporary API Docs: http://spark.screenisland.com

Getting started
---------------

```java
import static spark.Spark.*;

import spark.*;

public class HelloWorld {

   public static void main(String[] args) {
      
      get(new Route("/hello") {
         @Override
         public Object handle(Request request, Response response) {
            return "Hello World!";
         }
      });

   }

}
```

View at: http://localhost:4567/hello

More documentation is on the way!

Check out and try the examples in the source code.
You can also check out the javadoc. After getting the source from
github run: 

    mvn javadoc:javadoc

The result is put in /target/site/apidocs

Examples
---------

Simple example showing some basic functionality

```java
import static spark.Spark.*;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A simple example just showing some basic functionality
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        
        //  setPort(5678); <- Uncomment this if you wan't spark to listen on a port different than 4567.
        
        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });
        
        post(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World: " + request.body();
            }
        });
        
        get(new Route("/private") {
            @Override
            public Object handle(Request request, Response response) {
                response.status(401);
                return "Go Away!!!";
            }
        });
        
        get(new Route("/users/:name") {
            @Override
            public Object handle(Request request, Response response) {
                return "Selected user: " + request.params(":name");
            }
        });
        
        get(new Route("/news/:section") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("text/xml");
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
            }
        });
        
        get(new Route("/protected") {
            @Override
            public Object handle(Request request, Response response) {
                halt(403, "I don't think so!!!");
                return null;
            }
        });
        
        get(new Route("/redirect") {
            @Override
            public Object handle(Request request, Response response) {
                response.redirect("/news/world");
                return null;
            }
        });
        
        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return "root";
            }
        });
        
    }
}
```

-------------------------------

A simple RESTful example showing howto create, get, update and delete book resources

```java
import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A simple RESTful example showing howto create, get, update and delete book resources.
 */
public class Books {

    /**
     * Map holding the books
     */
    private static Map<String, Book> books = new HashMap<String, Book>();
    
    public static void main(String[] args) {
        
        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent as query parameters e.g. /books?author=Foo&title=Bar
        post(new Route("/books") {
            Random random = new Random();
            @Override
            public Object handle(Request request, Response response) {
                String author = request.queryParams("author");
                String title = request.queryParams("title");
                Book book = new Book(author, title);
                
                int id = random.nextInt(Integer.MAX_VALUE);
                books.put(String.valueOf(id), book);
                
                response.status(201); // 201 Created
                return id;
            }
        });
        
        // Gets the book resource for the provided id
        get(new Route("/books/:id") {
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
        put(new Route("/books/:id") {
            @Override
            public Object handle(Request request, Response response) {
                String id = request.params(":id");
                Book book = books.get(id);
                if (book != null) {
                    String newAuthor = request.queryParams("author");
                    String newTitle = request.queryParams("title");
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
        delete(new Route("/books/:id") {
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
        get(new Route("/books") {
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
```

---------------------------------

Example showing a very simple (and stupid) authentication filter that is executed before all other resources

```java
import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Example showing a very simple (and stupid) autentication filter that is
 * executed before all other resources.
 * 
 * When requesting the resource with e.g. 
 *     http://localhost:4567/hello?user=some&password=guy
 * the filter will stop the execution and the client will get a 401 UNAUTHORIZED with the content 'You are not welcome here'
 * 
 * When requesting the resource with e.g. 
 *     http://localhost:4567/hello?user=foo&password=bar
 * the filter will accept the request and the request will continue to the /hello route.
 * 
 * Note: There is a second "before filter" that adds a header to the response
 * Note: There is also an "after filter" that adds a header to the response
 */
public class FilterExample {

   private static Map<String, String> usernamePasswords = new HashMap<String, String>();

   public static void main(String[] args) {

      usernamePasswords.put("foo", "bar");
      usernamePasswords.put("admin", "admin");

      before(new Filter() {
         @Override
         public void handle(Request request, Response response) {
            String user = request.queryParams("user");
            String password = request.queryParams("password");

            String dbPassword = usernamePasswords.get(user);
            if (!(password != null && password.equals(dbPassword))) {
               halt(401, "You are not welcome here!!!");
            }
         }
      });
      
      before(new Filter("/hello") {
          @Override
          public void handle(Request request, Response response) {
              response.header("Foo", "Set by second before filter");
          }
       });

      get(new Route("/hello") {
         @Override
         public Object handle(Request request, Response response) {
            return "Hello World!";
         }
      });

      after(new Filter("/hello") {
          @Override
          public void handle(Request request, Response response) {
             response.header("spark", "added by after-filter");
          }
       });
      
   }
}
```

---------------------------------

Example showing how to use attributes

```java
import static spark.Spark.after;
import static spark.Spark.get;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Example showing the use of attributes
 */
public class FilterExampleAttributes {

    public static void main(String[] args) {
        get(new Route("/hi") {
            @Override
            public Object handle(Request request, Response response) {
                request.attribute("foo", "bar");
                return null;
            }
        });
        
        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                for (String attr : request.attributes()) {
                    System.out.println("attr: " + attr);
                }
            }
        });
        
        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                Object foo = request.attribute("foo");
                response.body(asXml("foo", foo));
            }
        });
    }
    
    private static String asXml(String name, Object value) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + name +">" + value + "</"+ name + ">";
    }
    
}
```


---------------------------------

Example showing how to serve static resources

```java
import static spark.Spark.*;
import spark.*;

public class StaticResources {

    public static void main(String[] args) {

        // Will serve all static file are under "/public" in classpath if the route isn't consumed by others routes.
        staticFileLocation("/public");

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });
    }
}
```
---------------------------------

Example showing how to define content depending on accept type

```java
import static spark.Spark.*;
import spark.*;

public class JsonAcceptTypeExample {

	public static void main(String args[]) {

		//Running curl -i -H "Accept: application/json" http://localhost:4567/hello json message is read.
		//Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is thrown.
		get(new Route("/hello", "application/json") {
			@Override
			public Object handle(Request request, Response response) {
				return "{\"message\": \"Hello World\"}";
			}
		});

	}

} 
```
---------------------------------

Example showing how to render a view from a template. Note that we are using ModelAndView class for setting the object and name/location of template. 

First of all we define a class which handles and renders output depending on template engine used. In this case FreeMarker.

```java
public abstract class FreeMarkerTemplateView extends TemplateViewRoute {

	private Configuration configuration;
	
	protected FreeMarkerTemplateView(String path) {
		super(path);
		this.configuration = createFreemarkerConfiguration();
	}
	
	protected FreeMarkerTemplateView(String path, String acceptType) {
		super(path, acceptType);
		this.configuration = createFreemarkerConfiguration();
	}

	@Override
	public String render(ModelAndView modelAndView) {
		try {
			StringWriter stringWriter = new StringWriter();
			
			Template template = configuration.getTemplate(modelAndView.getViewName());
			template.process(modelAndView.getModel(), stringWriter);
			
			return stringWriter.toString();
			
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (TemplateException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(FreeMarkerTemplateView.class, "freemarker");
        return retVal;
    }
	
}
```

Then we can use it to generate our content. Note how we are setting model data and view name. Because we are using FreeMarker, in this case a Map and the name of the template is required:

```java
public class FreeMarkerExample {

	public static void main(String args[]) {

		get(new FreeMarkerTemplateView("/hello") {
			@Override
			public ModelAndView handle(Request request, Response response) {
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("message", "Hello World");
				return new ModelAndView(attributes, "hello.ftl");
			}
		});

	}

}
```

---------------------------------

Example of using Transformer.

First of all we define the transformer class, in this case a class which transforms an object to JSON format using gson API.

```java
public abstract class JsonTransformer extends ResponseTransformerRoute {

	private Gson gson = new Gson();
	
	protected JsonTransformer(String path) {
		super(path);
	}

	protected JsonTransformer(String path, String acceptType) {
		super(path, acceptType);
	}
	
	@Override
	public String render(Model model) {
		return gson.toJson(model.getModel());
	}

}
```

And then the code which return a simple POJO to be transformed to JSON:

```java
public class TransformerExample {

	public static void main(String args[]) {

		get(new JsonTransformer("/hello", "application/json") {
			@Override
			public Model handle(Request request, Response response) {
				return new Model(new MyMessage("Hello World"));
			}
		});

	}
	
}
```
