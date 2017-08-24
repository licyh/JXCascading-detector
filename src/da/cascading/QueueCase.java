package da.cascading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import LogClass.LogType;
import da.graph.HappensBeforeGraph;
import da.graph.LogInfoExtractor;
import da.graph.Pair;
import da.tagging.JobTagger;


public class QueueCase {

	String projectDir;
	
	HappensBeforeGraph hbg;
	
	// Queue-related Info
	LinkedHashMap<Integer, Integer> eventHandlerBlocks;   // beginIndex -> endIndex
    LinkedHashMap<Integer, Integer> loopBlocks;   // beginIndex -> endIndex
	
    LinkedHashMap<Integer, Integer> handlerBlocks;
    LinkedHashMap<Integer, Integer> handlerThreads;
    
    
    
    // results
    BugPool bugPool;
    
	
	public QueueCase(String projectDir, HappensBeforeGraph hbg, LogInfoExtractor logInfo) {
		this.projectDir = projectDir;
		this.hbg = hbg;
	    //added for CA
		this.handlerBlocks = logInfo.getHandlerBlocks();
		this.handlerThreads = logInfo.getHandlerThreads();
	    this.eventHandlerBlocks = logInfo.getEventHandlerBlocks(); 
	    this.loopBlocks = logInfo.getLoopBlocks();	    
	    
	    this.bugPool = new BugPool(this.projectDir, this.hbg);
	}
	
	public void doWork() {
		System.out.println("JX - INFO - QueueCase: doWork...");
		// Traverse event handlers
		traverseEventHandlers();
		bugPool.printResults(true);
	}
	
	

	
	/*********************************************************
	 * Core
	 ********************************************************/
	
	public void traverseEventHandlers() {
		List< Entry<Integer, Integer> > list = new ArrayList<>( eventHandlerBlocks.entrySet() );
		//traverse every pair of Event Hanlder (i:length-1..0, j:i-1..0)
		for( int i = list.size()-1; i >= 0; i--) {
		    Entry<Integer, Integer> iEntry = list.get(i);
		    if (iEntry.getValue() == null) continue;
		    int iBeginIndex = iEntry.getKey();
		    int iEndIndex = iEntry.getValue();
		    
		    for (int j = i-1; j >= 0; j--) {
		    	Entry<Integer, Integer> jEntry = list.get(j);
		    	if (jEntry.getValue() == null) continue;
		    	int jBeginIndex = jEntry.getKey();
			    int jEndIndex = jEntry.getValue();
			    if ( !hbg.isSameThread(iBeginIndex, jBeginIndex) ) break;
			    //
			    if ( !hbg.getNodeOPVAL(iBeginIndex).equals("xx")
			    		|| !hbg.getNodeOPVAL(jBeginIndex).equals("xx")
			    		|| !hbg.isSameValue(iBeginIndex, jBeginIndex) 
			    		) {
			    	findBugLoops(jBeginIndex, jEndIndex);
			    	// do we need to consider the reverse case?
			    	//findBugLoops(iBeginIndex, iEndIndex);
			    }
		    }
		    
		}
	}
	
	
	/**
	 * find bug loops between (beginIndex, endIndex)
	 */
	public void findBugLoops(int beginIndex, int endIndex) {
	    // tmp vars
	    BitSet traversedNodes = new BitSet();  	//tmp var. set of traversed nodes for a single code snippet, e.g, event handler
		traversedNodes.clear();
		dfsTraversing(beginIndex, endIndex, traversedNodes);
	}
	
	
    public void dfsTraversing( int x, int endIndex, BitSet traversedNodes ) {
    	traversedNodes.set( x );
    	// find the bug loop
    	if ( hbg.getNodeOPTY(x).equals(LogType.LoopBegin.name()) ) {
    		bugPool.addLoopBug( x );
    	}

        List<Pair> list = hbg.getEdge().get(x);
        for (Pair pair: list) {
        	int y = pair.destination;
        	if ( !traversedNodes.get(y) && hbg.getReachSet().get(y).get(endIndex) )
        		dfsTraversing( y, endIndex, traversedNodes );
        }
    }
    
    
    @Deprecated
	public void traverseHanders() {
		List<Integer> list = new ArrayList<>( handlerBlocks.keySet() );
		
		for (Entry<Integer, Integer> thread: handlerThreads.entrySet()) {
			int beginPos = thread.getKey();
			int endPos = thread.getValue();
			
			System.out.println("JX - INFO - QueueCase: handlerThreas - " + hbg.getPrintedIdentity(list.get(beginPos)) );
			
			// for ca-6744, only focus on this thread.
			if ( !hbg.getNodeTID( list.get(beginPos) ).equals("29") ) continue;
			
			//debug
			for (int i = beginPos; i <= endPos; i++) {
				System.out.println( list.get(i) + " " );
			}
			System.out.println();
			/*
			System.out.println( "JX - DEBUG - QueueCase: (40836, 41123) = " + hbg.isConcurrent(40836, 41123) 
				+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(40836, 41123) );
			System.out.println( "JX - DEBUG - QueueCase: (40836, 41763) = " + hbg.isConcurrent(40836, 41763) 
			+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(40836, 41763) );
			System.out.println( "JX - DEBUG - QueueCase: (40836, 41394) = " + hbg.isConcurrent(40836, 41394) 
			+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(40836, 41394) );
			for (int i = beginPos; i <= endPos; i++) {
				System.out.println( "JX - DEBUG - QueueCase: (40836, " + list.get(i) + ") = " + hbg.isConcurrent(40836, list.get(i)) 
					+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(40836, list.get(i)) );
			}
			for (int i = beginPos; i <= endPos; i++) {
				System.out.println( "JX - DEBUG - QueueCase: (41123, " + list.get(i) + ") = " + hbg.isConcurrent(41123, list.get(i))
					+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(41123, list.get(i)) );
			}
			*/
			
			for (int i = beginPos; i <= endPos; i++) {
				System.out.println( "JX - DEBUG - QueueCase: (22359"
						+ ""
						+ ", " + list.get(i) + ") = " + hbg.isConcurrent(22359, list.get(i)) 
					+ " JobIDSame?=" + new JobTagger(this.hbg).isSameJobID(22359, list.get(i)) );
			}
			
			for (int i = beginPos; i <= endPos; i++) {
				
				if ( new JobTagger(this.hbg).isSameJobID(22359, list.get(i)) ) continue;
				//|| list.get(i)==41145 || list.get(i)==41157
				
				//for (int j = i+1; j <= endPos; j++) {				
					int beginIndex = list.get(i);
					//test
					//new JobTagger(this.hbg).findJobIdentity(beginIndex);
					int endIndex = handlerBlocks.get(beginIndex);
					findBugLoops(beginIndex, endIndex);
				//}
				System.out.println("JX - DEBUG - #bug nodes = " + bugPool.getBugInstanceNumber() + "  #bug static = " + bugPool.getBugStaticNumber() );
			}   
			
		}

	}
	
	
}



