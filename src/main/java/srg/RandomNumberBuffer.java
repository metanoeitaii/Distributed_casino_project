package srg;


//buffer random numbers pou paragei o SRG kai katanalwnei o worker 
public class RandomNumberBuffer{
    private final int[] buffer;  
    private int count = 0; 
    private final int capacity; 

    public RandomNumberBuffer(int capacity){
        this.capacity = capacity;
        this.buffer = new int[capacity];
    }

    //o SRG bazei neo number, perimenei an to buffer einai gemato
    public synchronized void produce(int number) throws InterruptedException{
        while(count == capacity){ 
            wait();
        }
        buffer[count] = number; 
        count++;
        notifyAll(); 
    }

    //o Worker pairnei to 1o number (FIFO), perimenei an einai adeio 
    public synchronized int consume() throws InterruptedException{
        while(count == 0){ 
            wait();
        }

        int number = buffer[0];
        for(int i = 0; i < count - 1; i++){
            buffer[i] = buffer[i + 1]; 
        }

        count--;
        notifyAll(); 
        return number;
    }
}
