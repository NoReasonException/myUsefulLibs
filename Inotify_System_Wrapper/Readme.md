Inotify Wrapper API 

some quick tips 

1) Install boost on your system ...<br>
			sudo apt-get install libboost-all-dev  <br>
      
Installation...
  1) Compile (without linking) the SharedObjects/src/InotifyLib.cpp <br>
                                    g++ InotifyLib.cpp -c -fPIC -o InotifyLib.o <br>
  2) Compile (without linking) the SharedObjects/src/ <br>
                                    g++ com_noReasonException_InotifyWrapper_InotifyWrapper_define.c -c -fPIC -I /usr/lib/jvm/java-1.8.0-openjdk-amd64/include/  -I /usr/lib/jvm/java-1.8.0-openjdk-amd64/include/linux/ -o JNIWrapper.o  <br> 
                                    
  3) Create .so file and link the two previous files ...!(And link the boost library of course :P )  <br>
                                    g++ JNIWrapper.o InotifyLib.o -shared -o libInotifyNative.so -L /usr/local/lib -lboost_filesystem -lboost_system<br>  
  4) Copy the generated libInotifyNative.so file in library.path , for example , /lib folder (need root..)  <br>
                                    sudo cp libInotifyNative.so /lib/  <br>
  And....voialla! ^^  
