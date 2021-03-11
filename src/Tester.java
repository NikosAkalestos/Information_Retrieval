public class Tester {
    private String key;
    private int counter;

    public Tester(String key) {
        this.key = key;
        this.counter = 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Tester{" +
                "key='" + key + '\'' +
                ", counter=" + counter +
                '}';
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
