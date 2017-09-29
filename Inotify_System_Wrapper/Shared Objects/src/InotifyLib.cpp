
/****
 * @author noReasonException(Stefanos Stefanou)
 * @date 26-10-2017
 * @version 1
 */

#include <sys/inotify.h>
#include <iostream>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <poll.h>
#include <boost/filesystem.hpp>
#include <queue>

using namespace boost::filesystem;
//Global Variables (Linked as native references on java )
int debug=0;
int isInitialized=0;
std::queue<char *> FileNamesModified;
std::queue<int>     TypeOfModification;
//char buff[4096]__attribute__ ((aligned(__alignof__(struct inotify_event))));
char *buff;
#define DEBUGv(str,...) if(debug)printf(str"\n",__VA_ARGS__)    //DEBUG with printf-format parameters
#define DEBUGp(str) if(debug)perror(str)                        //DEBUG wrapper around perror()
#define DEBUG(str) if(debug)printf(str"\n")                     //DEBUG simple printin message

/***
 * typedef struct DetectedDirectories as Directories_t
 * This struct is returned by DetectDirectories* Methods
 * @brief Used by InotifyWrapper to add watch descriptor in every subDirectory! :)
 *
 */
typedef struct DetectedDirectories{
    std::vector<boost::filesystem::path> DetectedDirectories;
}Directories_t;
/***
 * typedef struct  inotifyWrapperInfoStruct as InotifyInfo_t
 *
 * @brief Used by InotifyWrapper to keep internal info)! :)
 */
typedef struct inotifyWrapperInfoStruct{
    int     notifyFileDescriptor;                     //Inotify File Descriptor
    nfds_t  numberOfWatchDescriptors;                //Number of watch descriptors in array coming..
    int *   watchDescriptors;                       //watchDescriptors Array (uninitialized)
}InotifyInfo_t;
InotifyInfo_t *GlobalInotifyInfo;
Directories_t*DetectDirectories_recv(boost::filesystem::path &path,Directories_t*retval) __attribute_deprecated__; //forward Declarations
Directories_t*allocDetectedDirectoriesStruct();
InotifyInfo_t*allocInotifyInfo_t(nfds_t);

/***
 *
 * @param   path            -> the path to detect all subfolders
 * @param   retval          -> just leave it in default value,used by recv algorithm
 * @return  Directories_t   -> the directories found @see typedef struct DetectedDirectories as Directories_t
 * @purpose
 *         Find and return all subdirectories of a given path
 * @deprecated
 */
Directories_t*DetectDirectories_recv(boost::filesystem::path &path,Directories_t*retval=NULL)  {
    if(retval==NULL){
        retval = allocDetectedDirectoriesStruct();
    }
    directory_iterator end_iterator;
    for(directory_iterator itr(path);itr!=end_iterator;++itr){
        if(is_directory(itr->path())){
            retval->DetectedDirectories.push_back(itr->path());
            DetectDirectories_recv(const_cast<boost::filesystem::path &>(itr->path()),retval);

        }
    }
    return retval;
}
/***
 *
 * @param   path            -> the path to detect all subfolders
 * @param   retval          -> just leave it in default value,used by recv algorithm
 * @return  Directories_t   -> the directories found @see typedef struct DetectedDirectories as Directories_t
 * @purpose
 *         Find and return all subdirectories of a given path
 * @note
 *         This is the non-recv deprecated version , use this instead
 * @note
 *         Uses the BFS Algorithm (@see http://www.geeksforgeeks.org/breadth-first-traversal-for-a-graph/ )
 */
Directories_t*DetectDirectories(boost::filesystem::path &path) {
    directory_iterator end_iterator;
    Directories_t *retval = allocDetectedDirectoriesStruct();
    std::stack<boost::filesystem::path> findStack;

    findStack.push(path);
    directory_iterator *itr=new directory_iterator(findStack.top());
    retval->DetectedDirectories.push_back(path);
    do {
        findStack.pop();
        for (; *itr != end_iterator; itr->operator++()) {
            if (is_directory(itr->operator->()->path())) {
                findStack.push(itr->operator->()->path());
                retval->DetectedDirectories.push_back(itr->operator->()->path());
            }
            //std::cout<<itr->path().string()<<std::endl;
        }
        delete(itr);
        if(!findStack.empty()){

            itr=new directory_iterator(findStack.top());
        }

    }while(!findStack.empty());
    return retval;
}
/***
 *      Directories_t*allocDetectedDirectoriesStruct()
 * @return Directories_t
 * @brief
 *          Allocates and initializes a new Directories_t struct
 *
 */
Directories_t*allocDetectedDirectoriesStruct(){
    return new Directories_t;
}
/***
 *      InotifyInfo_t*allocInotifyInfo_t()
 * @return InotifyInfo_t
 * @brief
 *          Allocates and initializes a new InotifyInfo_t struct
 *
 */
InotifyInfo_t*allocInotifyInfo_t(nfds_t DirectoriesToWatch){
    InotifyInfo_t*ptr=new InotifyInfo_t;
    ptr->numberOfWatchDescriptors=DirectoriesToWatch;
    ptr->watchDescriptors=(int *)malloc(sizeof(int)*DirectoriesToWatch);
    if(ptr->watchDescriptors==NULL)   return NULL;
    ptr->notifyFileDescriptor=inotify_init();
    if(ptr->notifyFileDescriptor==-1) return NULL;
    return ptr;


}
/***
 *      bool initializeInotify(char path[]){
 * @return int
 *              0           Success
 *              -1          Error(See StdErr)
 * @brief
 *          Allocates and initializes a new InotifyInfo_t struct
 *
 */
int initializeInotify(char path[]){
    debug=1;
    if(posix_memalign((void **)(&buff),sizeof(struct inotify_event),4096)){
        return -1;//NOZERO ON ERROR
    }
    boost::filesystem::path p(path);
    Directories_t*directories=DetectDirectories(p);
    DEBUGv("Directories founded %d",directories->DetectedDirectories.size());
    if(__builtin_expect((GlobalInotifyInfo=allocInotifyInfo_t(directories->DetectedDirectories.size()))==NULL,0)){ //if unlikely the caller return error
        return -1;
    }
    DEBUG("Inotify Info Struct initialized Successfully");
    for (int i=0;i<directories->DetectedDirectories.size();i++) {
        GlobalInotifyInfo->watchDescriptors[i]=inotify_add_watch(GlobalInotifyInfo->notifyFileDescriptor,directories->DetectedDirectories[i].string().c_str(),IN_ACCESS|IN_ATTRIB|IN_CLOSE_WRITE|IN_CLOSE_NOWRITE|IN_CREATE|IN_DELETE_SELF|IN_MODIFY|IN_MOVE_SELF|IN_MOVED_FROM|IN_MOVED_TO|IN_OPEN);
        DEBUGv("[Directory] %s , Prepare for listening ...",directories->DetectedDirectories[i].string().c_str());
    }
    DEBUG("All Listeners is set up and waiting....");
    isInitialized=1;
    return 0;

}
//recheck tomorrow
int waitForFileEvent(){
    if(!isInitialized)return -1;
    struct inotify_event *event;
    ssize_t len;
    int mask;
    char *ptr;
    len=read(GlobalInotifyInfo->notifyFileDescriptor,buff,4096);

    for(ptr=buff;ptr<buff+len;ptr+=(sizeof(struct inotify_event)+event->len)){
        event=(struct inotify_event *)ptr;

        if (event->mask & IN_MODIFY)
            printf("Modified: ");
        if (event->mask & IN_CLOSE_NOWRITE)
            printf("IN_CLOSE_NOWRITE: ");
        if (event->mask & IN_CLOSE_WRITE)
            printf("IN_CLOSE_WRITE: ");
        char *tmp = (char *)malloc(event->len);
        memcpy(tmp,event->name,event->len);

        FileNamesModified.push(tmp);
        mask=event->mask;
        TypeOfModification.push(mask);


    }
    return 1;
}
/***
 *
 * @return a pointer to heap position , to the file path as C-style string
 * @Note -> you must free after use the pointer returned by getLastModifiedPath
 */
char *getLastModifiedFile(){
    char *retval=FileNamesModified.back();
    FileNamesModified.pop();
    return retval;
}

int getLastModifiedType(){
    int retval=TypeOfModification.back();
    TypeOfModification.pop();
    return retval;
}
/***
 *
 * @param argc
 * @param argv
 * @return
 */
int main(){}
/***
int main(int argc,char *argv[]) {
    if(argc>1 and !strcmp(argv[1],"--Debug")){
        debug=1;
        printf("Inotify Wrapper (--Debug) Started...\n");
    }
    if(!initializeInotify((char *)"../")){
        DEBUGp("Inotify Info Struct Corrupted");

    }
    isInitialized=1;
    waitForFileEvent();
    std::cout<<getLastModifiedFile();
    return 0;

}




**/