package srg;


//buffer random numbers pou paragei o SRG kai pane ston worker 
public class RandomNumberBuffer{
    private final int[] buffer; //pinakas poy krataei ta numbers 
    private int count = 0; //posoi ari8moi twra sto buffer
    private final int capacity; //max 8eseis sto buffer

    public RandomNumberBuffer(int capacity){
        this.capacity = capacity;
        this.buffer = new int[capacity];
    }

    //producer -> SRG, bazei neo number sto buffer
    public synchronized void produce(int number) throws InterruptedException{
        while(count == capacity){ //gemato buffer, perimenoume 
            wait();
        }
        buffer[count] = number; //bazw ari8mo sthn epomenh eleu8erh 8esh
        count++;
        notifyAll(); //eidopoiw consumer oti exoume neo number 
    }

    //consumer -> worker, pairnei 1o number apo buffer
    public synchronized int consume() throws InterruptedException{
        while(count == 0){ //adeio buffer, perimenw ton producer 
            wait();
        }

        int number = buffer[0]; //pairnw 1o number san oura 
        for(int i = 0; i < count - 1; i++){
            buffer[i] = buffer[i + 1]; //kanw ta numbers mia 8esh aristera -> h 8esh 0 einai panta o epomenos ari8mos  
        }

        count--;
        notifyAll(); //eidopoiw producer oti yparxei free 8esh
        return number;
    }
}