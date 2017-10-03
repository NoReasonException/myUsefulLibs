package com.noReasonException.InotifyWrapper;
/***
 * @author noReasonException(Stefanos Stefanou)
 * @Note , no lisence , use free with respect to author!
 * @Note , Give a star if you like it ! :)
 * @Note , find me on github https://github.com/NoReasonException/
 */
/***
 * Known Bugs
 * Symptom                                                                  Status                  BranchName                      Solution
 * 1) no look current directory.....                                        SOLVED                  fixed on master                 in initializeInotify BFS Algorithm
 * 2)invalid string pass in parameter #1 of initializeInotify               SOLVED                  fixed on master                 use env->GetArrayLength instead of standard strlen
 * 3)invalid string returned by getLastModifiedFile()                       SOLVED                  fixed on master                 make buffer global , use posix_memalign for aligment
 * 4)Throw a boost C++ Exception in invalid path (Instead of java one)      PENDING
 * 5)the getLastModifiedFile returns only the name , not the relative path  PENDING
 * 6)the isBad() return bad status , in case of using the feature 3         SOLVED                  Ftr3                            check the initializeInotify_onCreateFileCreateNewWatchDecriptorFeature_enabled() func
 */
/***
 * Features i want to add on VER 0.2
 * Description                                                                                          BranchName              Status                          Notes...
 * 1)Pass in constructor the type of event i want to watch (now is simply all!)                         -                       PENDING
 * 2)Wrapper to native call getLastModifiedFile so to return a java.lang.String                         -                       PENDING
 * 3)When we Create an new file,if this feature is selected , add a watch descriptor automatically      Ftr3                    READY                           i forgot to switch to Ftr3 , so the major job is done on master , in "Feature 3: Commit...
 */


import java.io.IOException;
import java.util.Scanner;

/***
 * Enum ModifiedType
 * Represents a event type returned by Inotify (in (struct inotify_event)->mask field ) )
 * for more info @see inotify(7) and linux/inotify.h
 *
 */
enum ModifiedType{
    IN_ACCESS           (0x00000001),	    /* File was accessed */
    IN_MODIFY           (0x00000002),	    /* File was modified */
    IN_ATTRIB           (0x00000004),	    /* Metadata changed */
    IN_CLOSE_WRITE      (0x00000008),   	/* Writtable file was closed */
    IN_CLOSE_NOWRITE    (0x00000010),       /* Unwrittable file closed */
    IN_OPEN             (0x00000020),	    /* File was opened */
    IN_MOVED_FROM       (0x00000040),	    /* File was moved from X */
    IN_MOVED_TO         (0x00000080),	    /* File was moved to Y */
    IN_CREATE           (0x00000100),	    /* Subfile was created */
    IN_DELETE           (0x00000200),	    /* Subfile was deleted */
    IN_DELETE_SELF      (0x00000400),	    /* Self was deleted */
    IN_MOVE_SELF        (0x00000800);	    /* Self was moved */
    ModifiedType(int Mask){
        this.mask=Mask;
    }
    private int mask;
    public int getMask(){
        return this.mask;
    }
}

/***
 * Class InotifyNative , A wrapper to InotifySystem
*/
public class InotifyWrapper {
    private boolean bad=false;
    private native int      initializeInotify(byte path[]);                 //called from constructor
    private native int      initializeInotify_onCreateFileCreateNewWatchDecriptorFeature_enabled(byte path[]);
    public  native int       waitForFileEvent();                             //block until something happen (read() syscall blocks)
    public  native byte []   getLastModifiedFile() throws IOException;       //get path of last modified file
    public  native int       getLastModifiedType();                          //get type of event for last modified file !
    static{
        Runtime.getRuntime().loadLibrary("InotifyNative");
    }
    public InotifyWrapper(java.lang.String pathToWatch,boolean onCreateWatchDirectory){
        if(onCreateWatchDirectory){
            if(this.initializeInotify_onCreateFileCreateNewWatchDecriptorFeature_enabled(pathToWatch.getBytes())!=0){
                this.bad=true;
            }
        }
        else{
            if(this.initializeInotify(pathToWatch.getBytes())!=0){
                this.bad=true;
            }
        }
    }
    public boolean isBad(){
        return this.bad;
    }
    public static void main(String args[]) throws IOException{
        InotifyWrapper wr = new InotifyWrapper("../",true);
        if(wr.isBad()){
            throw new IllegalArgumentException();
        }
        wr.waitForFileEvent();
        System.out.print(new String(wr.getLastModifiedFile()));
    }





}
