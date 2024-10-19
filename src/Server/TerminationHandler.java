package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TerminationHandler extends Thread{
    private int maxDelay;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public TerminationHandler(ServerSocket serverSocket, ExecutorService pool, int maxDelay){
        this.serverSocket = serverSocket;
        this.pool = pool;
        this.maxDelay = maxDelay;
    }

    @Override
    public void run(){
        System.out.println("[Server] Server is shutting down...");
        
        SaverThread saver = new SaverThread();
        saver.run();

        try{serverSocket.close();}
        catch(IOException e){e.printStackTrace();}
        pool.shutdown();
        try{
            if(!pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS)){
                pool.shutdownNow();
            }
        }catch(InterruptedException e){
            pool.shutdownNow();
            System.out.println("[SERVER] Server shutdown interrupted");
        }
        System.out.println("[Server] Server shutdown completed");
    }

}
