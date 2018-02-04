
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Producer extends Thread { 
    private BlockingQueue<Integer> buffer;

    public Producer(BlockingQueue<Integer> buffer){ 
        this.buffer = buffer;
    }

    public void run(){
        Random r = new Random(); 
        while( true ){
            int num = r.nextInt();
            try {
                buffer.add( num );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Produced " + num );
        }
    }
}


class Consumer extends Thread {
    private BlockingQueue<Integer> buffer;
    
    public Consumer(BlockingQueue<Integer> buffer) {
        this.buffer = buffer;
    }
    
    public void run() {
        while (true) {
            int x = 0;
            try {
                x = buffer.poll();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Well, x it is: " + x);
        }
    }
}




/**
 * Blocking queue implementation. 
 * http://tutorials.jenkov.com/java-concurrency/blocking-queues.html
 *  
 * BB: 1
 */
public class BlockingQueue<E> {

    private final Queue<E> queue;
    private final int size;

    public BlockingQueue(int size) {
        queue = new LinkedList<E>();
        this.size = size;
    }

    public synchronized void add(E item) throws InterruptedException {
        while (queue.size() == size) {
            wait();
        }
        /* 
         * imagine a scenario like this:
         * 1. When queue size was 0, we polled, which resulted in a wait.
         * 2. Now, we added an item to the queue. since queue is not empty, it can be polled.
         * 3. Thus notifyAll.
         */
        if (queue.size() == 0) {
            notifyAll();
        }
        queue.add(item);
    }
    
    public synchronized E poll() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        /*
         * imagine a scenario:
         * 1. When queue size is full, and we try to add an element. It gets 'wait'
         * 2. Now we do a poll, and now queue size is not full once polling is done.
         * 3. Thus we call notify all.
         */
        if (queue.size() == size) {
            notifyAll();
        }
        return queue.poll();
    }
    
    
    public static void main(String[] args) {
        BlockingQueue<Integer> bQueue = new BlockingQueue<Integer>(10);
        Producer p = new Producer( bQueue ); 
        Consumer c = new Consumer( bQueue ); 
        p.start();
        c.start();
    }
}
