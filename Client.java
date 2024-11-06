import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.*;
import java.net.*;

/**
 * New Hope Client
 * @author niamh
 * 17/10/24
 */
public class Client{
    // main connection variables
    private static InetAddress host;
    private static final int PORT = 1234;

    // making the connection
    public static void main(String[] args) {
        try{
            host = InetAddress.getLocalHost();
        } 
        catch(UnknownHostException e){
            System.out.println("Error getting host ID");
            System.exit(1);
        }
        // run the main code
        run();
    }
    
   private static void run() {
       Socket sConnect = null;
       try{
           // changing the link depeneding on host ID
           sConnect = new Socket(host,PORT);
           
           // being able to receive and send to the server
           BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
           PrintWriter output = new PrintWriter(sConnect.getOutputStream(),true);            
           
           Boolean con;	
           
           // looping the possible inputs to complete multiple entries from the same client
           while(con = true){
               
               BufferedReader outMessage =new BufferedReader(new InputStreamReader(sConnect.getInputStream()));
               
               // Set up for inputs (plus resetting each time for new entries)
               String date = null;
               String message = null;
               String res = null;
               String addRes = null;
               String listRes = null;
               String action = null;
               
               //first receiving variables
               System.out.println("Enter Action ADD, LIST or STOP: ");
               //sets input as a variable
               action = input.readLine(); 
               //sends variable to server
               output.println(action); 
               
               //to dissconnect from the server
               if (action.equalsIgnoreCase("STOP")) {
                    System.out.println("Closing connection..\n");
                    sConnect.close();
                    con = false;
               } 
               
               //to add a task to the array
               else if (action.equalsIgnoreCase("ADD")) {
                   //adding the date
                   System.out.println("Enter date of task (year-month-day): ");
                   date = input.readLine();
                   //adding the task
                   System.out.println("Enter ToDo Task to add:");
                   message = input.readLine();
                   //sending the data to the server thread
                   output.println(date);
                   output.println(message);
                   //getting the output
                   addRes = outMessage.readLine();
                   //outputing a response to show the addition of the task
                   System.out.println("\n SERVER RESPONSE: Add: " + addRes);
               } 
               
               //to view the tasks for a particular day
               else if (action.equalsIgnoreCase("LIST")) {
                   //adding the date
                   System.out.println("Enter date to view (year-month-day): ");
                   date = input.readLine();
                   //sending the date to search
                   output.println(date);
                   //starting the response
                   System.out.println("\nSERVER RESPONSE: List: ");
                   // prints each task until there isnt a next one (stops an endless loop)
                   while ((listRes = outMessage.readLine()) != null){
                       if(listRes.isEmpty()){
                           break;
                       }
                       //prints the array as a list for the client to view
                       System.out.println(listRes);
                   }
               } 
               //if entered input isn't an option
               else {
                   //getting the response from the server
                   res = outMessage.readLine();
                   System.out.println("\nSERVER RESPONSE: " + res);
                   System.out.println("ACTION INPUT ERROR");
                   con = true;
                   action = null;
               }
               
           }
           
        } 
        catch(IOException e){
            e.printStackTrace();
        }  
    } 
}
