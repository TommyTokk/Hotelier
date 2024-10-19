package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
public class CliClientHandler {
    private BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
    private PrintWriter out;

    public CliClientHandler(PrintWriter out) {
        this.out = out;
    }

    public int eval(String message) {
        String userLine = "";
        if(message.equals("select")){
            try{
                userLine = this.scanner.readLine();
                out.printf("%s\n", userLine);
            } catch (Exception e) {
                System.err.printf("[Client] Error: %s\n", e.getMessage());
                return -1;
            }
            return 0;
        }else if(message.equals("input")){
            try {
                userLine = this.scanner.readLine(); 
                out.printf("%s\n", userLine);
            } catch (Exception e) {
                System.err.printf("[Client] Error: %s\n", e.getMessage());
                return -1;
            }
            return 0;
        }else if(message.equals("input-pwd")){
            try {
                userLine = this.scanner.readLine();
                out.printf("%s\n", userLine);
            } catch (Exception e) {
                System.err.printf("[Client] Error: %s\n", e.getMessage());
                return -1;
            }
            return 0;

        }else if(message.equals("wait")){
            try{
                userLine = this.scanner.readLine();
                this.out.printf("%s\n", "stop-wait");
            }catch(Exception e){
                System.err.printf("[Client] Error: %s\n", e.getMessage());
                return -1;
            }
            return 0;
        }else if(message.equals("logout")){
            return 3;
        }else if(message.equals("exit")){
            return 1;
        }else if(message.equals("login")){
            return 2;
        }else{
            synchronized (System.out) {
                System.out.println(message);
            }
            return 0;
        }

    }
}
