
/****
 * @author noReasonException(Stefanos Stefanou)
 * @date 26-10-2017
 * @version 1
 */
#include <boost/filesystem.hpp>

#include <sys/inotify.h>
#include <iostream>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <poll.h>
#include <queue>
#include <sstream>

/**
 * Debug macros....
 */
#define DEBUGv(str,...) if(debug)printf(str"\n",__VA_ARGS__)    //DEBUG with printf-format parameters
#define DEBUGp(str) if(debug)perror(str)                        //DEBUG wrapper around perror()
#define DEBUG(str) if(debug)printf(str"\n")

/**
 *
 * Forward Declarations....
 */
int addInotifyWatchToFolder(char *path);
int ifFolderAddWatch(std::string fullPath);


using namespace boost::filesystem;


//Global Variables (Linked as native references on java )
int debug=0;
int isInitialized=0;
std::queue<char *> FileNamesModified;
std::queue<int>     TypeOfModification;
char *buff;
bool onCreateFileCreateNewWatchDecriptorFeature = false;
               //DEBUG simple printin message

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
    std::vector<int>  watchDescriptors;                       //watchDescriptors vector...
}InotifyInfo_t;
InotifyInfo_t *GlobalInotifyInfo;
Directories_t*directories;
Directories_t*DetectDirectories_recv(boost::filesystem::path &path,Directories_t*retval) __attribute_deprecated__; //forward Declarations
std::string getPathFromEvent(struct inotify_event *ptr);

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
    //ptr->watchDescriptors=(int *)malloc(sizeof(int)*DirectoriesToWatch);
    ptr->notifyFileDescriptor=inotify_init();
    if(ptr->notifyFileDescriptor==-1) return NULL;
    return ptr;


}

/***
 * Waits for any I/O ...
 * @return 0 on success , -1 on error ...
 *
 * @Note
 *      Possible Errors ..
 *              1) in case of non initialized library , you must call initializeInotify() first
 *              2) if Len syscall return -1;
 */
int waitForFileEvent(){
    if(!isInitialized){
        DEBUG("[ERR]You must initialize the library first !,call initializeInotify(char * path)");
        return -1;
    }
    struct inotify_event *event;
    ssize_t len;
    int mask;
    char *ptr;
    DEBUG("Blocking until detect any I/O...");
    if((len=read(GlobalInotifyInfo->notifyFileDescriptor,buff,4096))<0){
        DEBUG("[FATAL_ERR]I/O read() syscall encountered an error .... ");
        return -1;
    }
    DEBUG("I/O Detected ....");
    for(ptr=buff;ptr<buff+len;ptr+=(sizeof(struct inotify_event)+event->len)){
        event=(struct inotify_event *)ptr;
        std::string fullpath = getPathFromEvent(event);
        char *tmp = (char *)malloc(event->len);
        memcpy(tmp,event->name,event->len);
        FileNamesModified.push(tmp);
        mask=event->mask;
        TypeOfModification.push(mask);
        if(mask&IN_CREATE and ifFolderAddWatch(fullpath)){
            DEBUGv("[Directory] %s , Created and prepare for listening ...",fullpath.c_str());
        }

    }
    return 1;
}
/***
 *      bool initializeInotify(char path[]){
 * @return int
 *              0           Success
 *              -1          Error(See StdErr)
 * @brief
 *          Initialize The library
 *
 */
int initializeInotify(char path[]){
    debug=1;
    if(posix_memalign((void **)(&buff),sizeof(struct inotify_event),4096)){
        return -1;//NOZERO ON ERROR
    }
    boost::filesystem::path p(path);
    directories=DetectDirectories(p);
    DEBUGv("Directories founded %d",directories->DetectedDirectories.size());
    if(__builtin_expect((GlobalInotifyInfo=allocInotifyInfo_t(directories->DetectedDirectories.size()))==NULL,0)){ //if unlikely the caller return error
        return -1;
    }
    DEBUG("Inotify Info Struct initialized Successfully");
    for (int i=0;i<directories->DetectedDirectories.size();i++) {
        if(addInotifyWatchToFolder(const_cast<char *>(directories->DetectedDirectories[i].string().c_str()))){
            DEBUGv("[Directory] %s , FAIL TO LISTEN , CHECK PERMISSIONS ...",directories->DetectedDirectories[i].string().c_str());
            return -1;
        }
        DEBUGv("[Directory] %s , Prepare for listening ...",directories->DetectedDirectories[i].string().c_str());
    }
    DEBUG("All Listeners is set up...");
    isInitialized=1;
    return 0;

}
/*
 * Same as the initializeInotify
 */
int initializeInotify_onCreateFileCreateNewWatchDecriptorFeature_enabled(char *path){
    if(isInitialized){
        DEBUG("[ERR]Already initialized");
        return -1;
    }
    onCreateFileCreateNewWatchDecriptorFeature=true;
    initializeInotify(path);
    return 1;
}
/***
 *
 * @param path
 * @return 0 to success , !0 on error , (see list below...)
 *
 */
int addInotifyWatchToFolder(char *path){
    int descriptorTemp=inotify_add_watch(GlobalInotifyInfo->notifyFileDescriptor,path,IN_ALL_EVENTS);
    if(descriptorTemp<0)return descriptorTemp;
    GlobalInotifyInfo->watchDescriptors.push_back(descriptorTemp);
    return 0;
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
int ifFolderAddWatch(std::string fullPath) {
    if (!onCreateFileCreateNewWatchDecriptorFeature)return 0;
    if (!boost::filesystem::is_directory(path(fullPath))) {
        return 0;
    }
    int tempDescriptor = 0;
    tempDescriptor = inotify_add_watch(GlobalInotifyInfo->notifyFileDescriptor, fullPath.c_str(), IN_ALL_EVENTS);
    if (tempDescriptor < 0)return -1;
    GlobalInotifyInfo->watchDescriptors.push_back(tempDescriptor);
    directories->DetectedDirectories.push_back(std::string(fullPath));
    return 1;

}
std::string getPathFromEvent(struct inotify_event *ptr){
    std::stringstream builder ;
    builder<<directories->DetectedDirectories.operator[](ptr->wd-1).string();
    if(ptr->wd>1){
        builder<<"/";
    }
    builder<<ptr->name;
    return std::string(builder.str());
}
/***
 *
 * @param argc
 * @param argv
 * @return
 */
int main(){}





