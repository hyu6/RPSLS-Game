import java.util.HashMap;

public class Gesture {
    private String name;
    private Integer index;
    private Integer owner;

    Gesture(String data){
        String tmp = data.replace("Player", "");
        String[] tmp1 = tmp.split(": ");

        this.owner = Integer.valueOf(tmp1[0]);
        this.name = tmp1[1];

        if(this.name.equals("rock"))
            this.index = 0;
        else if(this.name.equals("paper"))
            this.index = 1;
        else if(this.name.equals("scissors"))
            this.index = 2;
        else if(this.name.equals("lizard"))
            this.index = 3;
        else if(this.name.equals("spock"))
            this.index = 4;
        else {
            System.out.println("wrong gesture name");
        }
    }

    public Integer getIndex(){
        return this.index;
    }

    public Integer getOwner(){
        return this.owner;
    }

    public Integer compare(Gesture g) {
        Integer g1 = this.index;
        Integer g2 = g.getIndex();

        if (g1 == g2) {
            return 0;
        }

        if (g1 == 0) {
            if ((g2 == 2) || (g2 == 3)) {
                return 1;
            } else {
                return -1;
            }
        }

        if (g1 == 1) {
            if ((g2 == 0) || (g2 == 4)) {
                return 1;
            } else {
                return -1;
            }
        }

        if (g1 == 2) {
            if ((g2 == 3) || (g2 == 1)) {
                return 1;
            } else {
                return -1;
            }
        }

        if (g1 == 3) {
            if ((g2 == 4) || (g2 == 1)) {
                return 1;
            } else {
                return -1;
            }
        }

        else {
            if ((g2 == 0) || (g2 == 2)) {
                return 1;
            } else {
                return -1;
            }
        }

    }

}
