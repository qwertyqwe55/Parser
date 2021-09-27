public class Leksema {
    private final int tableId;
    private final int WordId;
    private final int BeginId;
    private final int EndId;


    public Leksema(int tableId, int wordId, int beginId, int endId) {
        this.tableId = tableId;
        this.WordId = wordId;
        this.BeginId = beginId;
        this.EndId = endId;
    }


    public int getBeginId() {
        return BeginId;
    }

    public int getTableId() {
        return tableId;
    }

    public int getWordId() {
        return WordId;
    }
}
