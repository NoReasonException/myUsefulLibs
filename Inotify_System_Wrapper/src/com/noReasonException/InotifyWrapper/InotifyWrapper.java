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
 * 4)Throw a boost C++ Exception in invalid path (Instead of java one)      PENDING TO VER 0.2                              -
 */
/***
 * Features i want to add on VER 0.2
 * Description                                                                                      BranchName              Status
 * 1)Pass in constructor the type of event i want to watch (now is simply all!)                        -                    PENDING TO VER 0.2
 * 2)Wrapper to native call getLastModifiedFile so to return a java.lang.String                        -                    PENDING TO VER 0.2
 * 3)When we Create an new file,if this feature is selected , add a watch descriptor automatically     Ftr3                 Coding Process...
 */


import java.io.IOException;

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
    private native int      initializeInotify(byte path[]);                 //called from constructor
    public native int       waitForFileEvent();                             //block until something happen (read() syscall blocks)
    public native byte []   getLastModifiedFile() throws IOException;       //get path of last modified file
    public native int       getLastModifiedType();                          //get type of event for last modified file !
    static{
        Runtime.getRuntime().loadLibrary("InotifyNative");
    }
    public static void main(String args[])throws InterruptedException,IOException{
        InotifyWrapper wr = new InotifyWrapper("./Test");
        System.out.print(wr.waitForFileEvent());
        System.out.print("->>>>"+new String(wr.getLastModifiedFile()));
        int type = wr.getLastModifiedType();
        if((type|ModifiedType.IN_DELETE.getMask())==1) {
            System.out.print("IN CLOSE WRITE EVENT");
        }
    }
    public InotifyWrapper(java.lang.String pathToWatch){
        this.initializeInotify(pathToWatch.getBytes());
    }



}
