import com.noReasonException.mylibs.datastruct.AbstractSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.LinkedListSymbolTable;
import com.noReasonException.mylibs.datastruct.Implementations.MoveToFrontHeuristicLinkedListSymbolTable;

import java.util.Random;

public class Main {
    public static void main(String args[]){
        Random r = new Random();
        AbstractSymbolTable<Integer,String> a = new MoveToFrontHeuristicLinkedListSymbolTable<>();
        a.put(10,"AAA");
        a.put(20,"bbb");
        a.put(30,"ccc");
        System.out.println(a.get(10));

        for (Integer i:a.keys()) {
            System.out.println(i+"->"+a.get(i));
        }
    }
}
