public class Round {
    private Gesture g1, g2;
    private Integer winner;

    Round(Gesture g){
        this.g1 = g;
    }

    public void addGesture(Gesture g){
        this.g2 = g;
        if(g1.compare(g2) == 0)
            this.winner = 3;
        else if(g1.compare(g2) > 0)
            this.winner = g1.getOwner();
        else
            this.winner = g2.getOwner();
    }

    public Integer getWinner() {
        return winner;
    }
}
