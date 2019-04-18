import java.util.ArrayList;

public class Game {
    private ArrayList<Round> rounds;
    private ArrayList<String> playRecord;
    private Integer point1, point2;
    private Integer curWinner;
    private Integer overallWinner;
    private Integer counter;
    private Integer handCounter;
    Game(){
        this.rounds = new ArrayList<>();
        this.playRecord = new ArrayList<>();
        this.point1 = 0;
        this.point2 = 0;

        this.counter = 0;
        this.handCounter = 0;

        this.curWinner = 3;
        this.overallWinner = 3;
    }

    public boolean roundOver(){
        if(this.counter%2 == 0)
            return true;
        else
            return false;
    }

    public boolean gameOver(){
        return (this.point2==3)||(this.point1==3);
    }

    public Integer getCurWinner(){
        return curWinner;
    }

    public Integer getOverallWinner(){
        return overallWinner;
    }

    public void addGesture(String data){

        this.playRecord.add(data);
        Gesture g = new Gesture(data);
        Round cur;
        if(this.counter%2==0){
            cur = new Round(g);
            this.rounds.add(cur);
        }
        else{
            cur = this.rounds.get(this.rounds.size() - 1);
            cur.addGesture(g);
            if(cur.getWinner()==0){
                this.point1++;
                this.curWinner = 0;
            }
            else if(cur.getWinner()==1){
                this.point2++;
                this.curWinner = 1;
            }
            if(cur.getWinner()==3){
                this.handCounter -= 2;
                this.curWinner = 3;
            }

            if(this.point1==3){
                this.overallWinner = 0;
            }
            if(this.point2==3){
                this.overallWinner = 1;
            }
        }
        this.counter++;
        this.handCounter++;
        System.out.println(this.counter);
    }

    public String printRound(){
        if(roundOver()){
            String s = new String();
            s += playRecord.get(counter-1) + "\n";
            s += playRecord.get(counter-2) + "\n";
            if(this.curWinner != 3)
                s += "Server: hand winner: Player" + (curWinner) + "\n";
            else{
                s +="Server: it's a tie. \n";
            }
            return s;
        }
        return "";
    }

    public String pointString(){
        return "Player0: "+this.point1+"pnts, Player1: "+this.point2+"pnts";
    }



}
