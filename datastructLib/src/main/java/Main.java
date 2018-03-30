import com.noReasonException.mylibs.datastruct.Implementations.BSTSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.BinarySearchLinkedListSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.LinkedListSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.OrderedLinkedList;

import java.util.Random;

public class Main {
    public static void main(String args[]){
        Random r = new Random();
        BSTSymbolTable<Integer,String> a = new BSTSymbolTable<>();
        a.put(10,"aa");
        a.put(20,"aa");
        a.put(5,"aa");
        a.put(7,"aa");
        a.put(6,"aa");
        for (int i = 0; i < 20; i++) {
            System.out.println(i+"->"+a.ceiling(i));
        }

    }
}
