import static algorithm.server.Server.todoList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * New Hopes Server
 * @author niamh
 * 17/10/24
 */
public class Server {
    // main variables
    private static final int PORT = 1234;
    private static int cConnect = 0; 
    static ArrayList<CalenderEntry> todoList = new ArrayList<>();
    
    static class CalenderEntry{
        LocalDate date;
        String event;
        
        public CalenderEntry(LocalDate date, String event){
            this.date = date;
            this.event = event;
        }
        
        @Override
        public String toString(){
            return "Date: " + date + ", Task: " + event;
        }
    }

    //main code run on start of the server
    public static void main(String[] args) {
        try(ServerSocket sSocket = new ServerSocket(PORT)){
            System.out.println("Waiting for connection at port " + PORT);
            
            while(true){
                Socket client = sSocket.accept();
                System.out.println(client + ": new connection");
                cConnect++;
                
                new ThreadHandler(client, cConnect).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }    
}

class ThreadHandler extends Thread {
    
    private Socket client;
    private int cConnect;
        
    public ThreadHandler(Socket socket, int cConnect){
        this.client = socket;
        this.cConnect = cConnect;
    }
        
    // run method helps with multithreading
    @Override
    public void run() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream())); 
                PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            ) {
            try{
                Boolean con = true;

                while (con = true) {
                    //getting inputs from client
                    String action = input.readLine();
                    if(action.equalsIgnoreCase("STOP")){
                        try {
                            System.out.println("Closing connection..\n");
                            con = false;
                            client.close();
                        } catch (IOException e) {
                            System.out.println("Error with Disconnecting");
                        }
                    }
                    else if(!action.equalsIgnoreCase("ADD") && !action.equalsIgnoreCase("LIST")){
                        throw new IncorrectActionException("Invaild action: " + action);
                    }
                    else{
                        String date = input.readLine();
                        String message = input.readLine();
                        String addResult = null;
                        String listResult = null;
                        StringBuilder response1 = new StringBuilder();
                        StringBuilder response2 = new StringBuilder();
                        boolean eventFound = false;

                        if (action.equalsIgnoreCase("ADD")) {
                            LocalDate dateIn = LocalDate.parse(date, dateFormatter);
                            todoList.add(new Server.CalenderEntry(dateIn, message));
                            System.out.println(cConnect + ": ADD " + message + ", Successfully added");
                            //finding the tasks for that day
                            for (Server.CalenderEntry entry : todoList) {
                                if (entry.date.equals(dateIn)) {
                                    response1.append(entry.toString()).append(" ~ ");
                                    eventFound = true;
                                }
                                
                            }
                            if (eventFound) {
                                addResult = response1.toString();
                            } else {
                                addResult = ("Error with " + dateIn);
                            }
                            output.println("Return: ADD " + addResult);
                            con = true;

                        } else if (action.equalsIgnoreCase("LIST")) {
                            LocalDate dateIn = LocalDate.parse(date, dateFormatter);
                            for (Server.CalenderEntry entry : todoList) {
                                if (entry.date.equals(dateIn)) {
                                    response2.append(entry.toString()).append("\n");
                                    eventFound = true;
                                }
                            }
                            if (eventFound) {
                                listResult = response2.toString();
                            } else {
                                listResult = ("No ToDo Tasks for " + dateIn);
                            }
                            System.out.println(cConnect + ": LIST, " + listResult);
                            output.println(listResult);
                            con = true;
                        }
                        else {
                            System.out.println("Error");
                        }
                    }
                }
            } catch (IncorrectActionException ex) {
                output.println("Error: " + ex.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error");
            try {
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }                     
}
