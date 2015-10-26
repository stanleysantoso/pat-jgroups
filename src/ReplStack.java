import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.Stack;

/**
 * Created by Stanley on 10/25/2015.
 */
public class ReplStack<T> extends ReceiverAdapter{
    JChannel channel;
    final Stack<T> state=new Stack<T>();

    public void push(T obj){
        state.push(obj);
    }

    public T pop(){
        return state.pop();
    }

    public T top(){
        return state.peek();
    }

    public Stack<T> getStack() {
        return state;
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        String line= msg.getObject().toString();
        synchronized(state) {
            if(line.startsWith("push")){
                T content = (T) line.substring(line.indexOf(' ') + 1, line.length());
                push(content);
                System.out.print("Stack : [");
                for(T obj : state){
                    System.out.print(" " + obj);
                }
                System.out.print("]\n");
            }
            else if(line.startsWith("pop")){
                System.out.println("Pop value : " + pop());
                System.out.print("Stack : [");
                for(T obj : state){
                    System.out.print(" " + obj);
                }
                System.out.print("]\n");
            }
            else if(line.startsWith("top")){
                System.out.println(top());
            }
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @SuppressWarnings("unchecked")
    public void setState(InputStream input) throws Exception {
        Stack<T> stack=(Stack<T>)Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.addAll(stack);
        }
        System.out.println("received state (" + stack.size() + " value in stack):");
        System.out.print("Stack : [");
        for(T obj: stack) {
            System.out.print(" "+obj);
        }
        System.out.print("]\n");
    }


    private void start() throws Exception {
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ReplStack");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line=in.readLine();
                if(line.startsWith("quit") || line.startsWith("exit")){
                    break;
                }
                Message msg=new Message(null, null, line);
                channel.send(msg);
            }
            catch(Exception e) {
            }
        }
    }


    public static void main(String[] args) throws Exception {
        new ReplStack().start();
    }
}

