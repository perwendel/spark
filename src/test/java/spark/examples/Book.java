package spark.examples;

/**
 * Book domain class
 *
 * @author Per Wendel
 */
class Book {

    private String author;
    private String title;

    public Book (String author, String title) {
        this.author = author;
        this.title = title;
    }

    /**
     * @return the author
     */
    public String getAuthor () {
        return author;
    }

    /**
     * @return the title
     */
    public String getTitle () {
        return title;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor (String author) {
        this.author = author;
    }

    /**
     * @param title the title to set
     */
    public void setTitle (String title) {
        this.title = title;
    }
}
