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
