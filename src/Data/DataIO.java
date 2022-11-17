package Data;

import java.io.*;
import java.util.LinkedList;

public class DataIO {

    private static final String fileName = "saves";

    static void saveTasks(LinkedList<Task> tasks){
        try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))){

            output.writeObject(tasks);

        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    static LinkedList<Task> loadTasks(){

        File file = new File(fileName);
        if(!file.exists()){
            try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))){

                output.writeObject(new LinkedList<Task>());

            }catch (IOException exc){
                exc.printStackTrace();
            }
        }

        try(ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))){

            return (LinkedList<Task>) inputStream.readObject();

        }catch (IOException | ClassNotFoundException exc){
            exc.printStackTrace();
        }

        return null;
    }
}
