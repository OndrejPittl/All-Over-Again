package game;

public enum BoardDimension {

    TINY    (1, "tiny (1x1)"),
    SMALL   (2, "small (2x2)"),
    NORMAL  (3, "normal (3x3)"),
    LARGE   (4, "large (4x4)"),
    HUGE    (5, "huge (5x5)");



    private int dimension;

    private String title;

    private BoardDimension(int dim, String title) {
        this.dimension = dim;
        this.title = title;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static BoardDimension getNth(int dim){
        return BoardDimension.values()[dim];
    }


    public String toString(){
        return this.title;
    }
}
