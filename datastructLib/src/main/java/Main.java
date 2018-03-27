import com.noReasonException.mylibs.datastruct.Implementations.LinkedListSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.OrderedLinkedList;

import java.util.Random;

public class Main {
    public static void main(String args[]){
        Random r = new Random();
        LinkedListSymbolTable<Integer,String> a = new OrderedLinkedList<>();
        int j=0;
        for (int i = 0 ;i<1000; i++) {
            a.put(r.nextInt()%30+30,"aa");
        }


    }
}
