import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {

    private String ip;
    private int port;

    private ConnThread connthread = new ConnThread();
    private Consumer<Serializable> callback;           // this callback is to change the textfile
    private Consumer<Serializable> controlCallback;    // this callback is to change other GUI elements


    Client(String ip, int port, Consumer<Serializable> callback, Consumer<Serializable> callback2) {
        this.callback = callback;
        this.controlCallback = callback2;
        connthread.setDaemon(true);
        this.ip = ip;
        this.port = port;
    }

    public void startConn() throws Exception{
        connthread.start();
    }

    public void send(Serializable data) throws Exception{
        connthread.out.writeObject(data);
        //this.waitFlag = true;
    }

    public void closeConn() throws Exception{
        connthread.socket.close();
    }


    class ConnThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;

        public void run() {
            try(
                Socket socket = new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while(true) {
                    Serializable data = (Serializable) in.readObject();
                    decode(data);
                }

            }
            catch(Exception e) {
                callback.accept("connection Closed");
            }
        }
    }

    private void decode(Serializable data){
        String s = data.toString();

        if(s.charAt(0)=='#')       // the changing GUI elements string starts with '#'
        {
            this.controlCallback.accept(data);
        }
        else{
            this.callback.accept(data);
        }
    }


    protected String getIP() {
        return this.ip;
    }

    protected int getPort() {
        return this.port;
    }


}
