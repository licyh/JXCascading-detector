package LogClass;

import java.util.HashMap;
import java.util.Map;

public enum LogType {
	
	//Process
	ProcessCreate, 	//process create
	//Thread
	ThdCreate,  	//create thread
    ThdEnter,   	//enter thread
    ThdExit,    	//exit thread
    ThdJoin,    	//join thread
    //model RPC
    MsgProcEnter, 	//msg handler enter
    MsgProcExit, 	//msg handler exit
    MsgSending, 	//msg sending (rpc or sendSocket)
    //model EventHandler
    EventCreate, 	//event create
    EventProcEnter, //event handler enter
    EventProcExit, 	//event handler exit
    
    // heap R/W - no use in my project
    HeapRead,   	//read a heap var
    HeapWrite,  	//write a heap var
    //log Locks
    LockRequire, 	//require lock
    LockRelease, 	//release lock
    
    //Added by JX
    EventHandlerCreate,   //no use now
    EventHandlerBegin,
    EventHandlerEnd,
    //Added by JX
    RWLockCreate,   	//jx - for creating ReentrantReadWriteLock
    TargetCodeBegin,
    TargetCodeEnd,
    LargeLoopBegin,
    //LargeLoopCenter,         //tmp
    //end-Added
    LoopBegin,               //also for spoon
    
    //only for dt
    LoopCenter,
    LoopPrint,     //only for javassist
    LoopEnd,       //only for spoon
    IO,
    RPC,
   
    //for dynamic slicing
    DynamicPoint;

    
	static Map<LogType,LogType> logTypeMapping = new HashMap<LogType,LogType>() {{
		put(EventHandlerBegin, EventHandlerCreate);
		put(EventProcEnter, EventCreate);
		put(MsgProcEnter, MsgSending);
		put(ThdEnter, ThdCreate);  // or ProcessCreate
	}};
}
