package game;

public class Pingu {

    private boolean isKI;

    private int id;

    public Pingu(boolean isKI, int id) {
        this.isKI = isKI;
        this.id = id;
    }

    public boolean isKI() {
        return isKI;
    }

    public void setKI(boolean KI) {
        isKI = KI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
