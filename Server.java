import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {

    private int port;

    private ConnThread connthread;
    private Consumer<Serializable> callback;
    private Consumer<Serializable> controlCallback;
    private Game game;
    private int newGameFlag;

    Server(int port, Consumer<Serializable> callback, Consumer<Serializable> callback2) {
        this.callback = callback;
        this.controlCallback = callback2;

        this.connthread = new ConnThread();
        this.connthread.setDaemon(true);
        this.port = port;

        this.game = new Game();
        this.newGameFlag = 0;


    }

    public void startConn(){
        connthread.start();
    }

    public void send(Serializable data) throws Exception{
        for(ClientThread t:this.connthread.socketList){
            t.out.writeObject(data);
        }
    }

    public void closeConn() throws Exception{
        for(ClientThread t:this.connthread.socketList){
            t.socket.close();
        }
    }


    protected int getPort() {
        return port;
    }


    class ConnThread extends Thread{
        private ArrayList<ClientThread> socketList = new ArrayList<>();

        public void run() {
            try{
                ServerSocket server = new ServerSocket(getPort());
                while(true) {
                    Socket s = server.accept();
                    ClientThread t1 = new ClientThread(s, socketList.size());
                    this.socketList.add(t1);

                    //update player number
                    send("#playerNum"+this.socketList.size());
                    controlCallback.accept("#playerNum"+this.socketList.size());

                    t1.start();
                }

            }
            catch(Exception e) {
                System.out.println(e);
                callback.accept("connection Closed, this is from the callback of cnnthread in server.java");
            }
        }
    }

    public class ClientThread extends Thread {
        private Integer playerIndex;
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;


        ClientThread(Socket s, Integer n){
            this.socket = s;
            this.playerIndex = n;

            try {
                this.socket.setTcpNoDelay(true);
                this.out = new ObjectOutputStream(this.socket.getOutputStream());
                this.in = new ObjectInputStream(this.socket.getInputStream());
                this.out.writeObject("You are Player"+this.playerIndex);
            }
            catch (Exception e){
                System.out.println(e);
                System.out.println("this is from ClientThread, Sever.java");
            }
        }

        public void run() {
            try{
                while(true) {
                    Serializable data = "Player" + this.playerIndex + ": "  + in.readObject();

                    if(data.toString().contains("#startNewGame")){

                        Server.this.send("Player" + this.playerIndex + " want continue a new game! ");
                        callback.accept("Player" + this.playerIndex + " want continue a new game! ");

                        Server.this.newGameFlag++;
                        if(Server.this.newGameFlag==2){     // if both two players request to start new game

                            System.out.println("startNewGame");
                            Server.this.game = new Game();
                            controlCallback.accept("#startNewGame");
                            Server.this.send("#stopWait");
                            controlCallback.accept("#point:" + game.pointString());
                            Server.this.send("#point:" + game.pointString());
                        }
                    }
                    else {
                        callback.accept(data);
                        game.addGesture(data.toString());
                        if (game.roundOver()) {
                            Server.this.send(game.printRound());
                            if (game.getCurWinner() == 3) {
                                callback.accept("Server: it's a tie." + "\n");
                            } else {
                                callback.accept("Server: hand winner: Player" + (game.getCurWinner()) + "\n");
                            }
                            Server.this.send("#stopWait");

                            controlCallback.accept("#point:" + game.pointString());
                            Server.this.send("#point:" + game.pointString());

                        }
                        if (game.gameOver()) {
                            Server.this.send("#gameOver");
                            Server.this.send("Server: Game is Over.\n");
                            Server.this.send("Server: game winner: Player" + +game.getOverallWinner() + "\n");

                            callback.accept("Server: Game is Over.\n");
                            callback.accept("Server: game winner: Player" + +game.getOverallWinner() + "\n");
                        }
                        //Server.this.send(data);
                    }
                }
            }
            catch(Exception e) {
                System.out.println(e + "this is from run() in ClientThread in Server.java");

                callback.accept("connection Closed, this is from clientThread");
            }
        }
    }

}
