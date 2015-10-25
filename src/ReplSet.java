import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.HashSet;

/**
 * Created by Stanley on 10/25/2015.
 */
public class ReplSet<T> extends ReceiverAdapter{
    JChannel channel;
    final HashSet<T> state=new HashSet<T>();

    public boolean add(T obj){
        return state.add(obj);
    }

    public boolean contains(T obj){
        return state.contains(obj);
    }

    public boolean remove(T obj){
        return state.remove(obj);
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        String line= msg.getObject().toString();
        synchronized(state) {
            if(line.startsWith("add")){
                T content = (T) line.substring(line.indexOf(' ') + 1, line.length());
                if(!add(content)) {
                    System.out.println("Value was added before");
                }
                System.out.print("Set : [");
                for (T obj : state) {
                    System.out.print(" " + obj);
                }
                System.out.print("]\n");
            }
            else if(line.startsWith("remove")){
                T content = (T) line.substring(line.indexOf(' ') + 1, line.length());
                if(remove(content)){
                    System.out.println("Value "+ content +" removed");
                }
                else{
                    System.out.println("Value not in set");
                }
                System.out.print("Set : [");
                for(T obj : state){
                    System.out.print(" " + obj);
                }
                System.out.print("]\n");
            }
            else if(line.startsWith("contains")){
                T content = (T) line.substring(line.indexOf(' ') + 1, line.length());
                if(contains(content)){
                    System.out.println("Value "+ content +" is in set");
                }
                else{
                    System.out.println("Value not in set");
                }
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
        HashSet<T> set=(HashSet<T>)Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.addAll(set);
        }
        System.out.println("received state (" + set.size() + " value in set):");
        System.out.print("Set : [");
        for(T obj: set) {
            System.out.print(" "+obj);
        }
        System.out.print("]\n");
    }


    private void start() throws Exception {
        channel=new JChannel();
        channel.setReceiver(this);
        channel.connect("ReplSet");
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
        new ReplSet<>().start();
    }
}

