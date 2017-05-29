package dm;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.*;

import javassist.*;
import javassist.bytecode.*;

import dm.Util.DMOption;


public class Transformer implements ClassFileTransformer {

	DMOption option;

	public Transformer(String args) {
	    super();
	    option = new DMOption(args);    
	}
	
	
	/**
	 * default function in javassist
	 */
	@Override   
	public byte[] transform(ClassLoader loader, String className, Class<?> redefiningClass,
	    ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
	    return transformClass(redefiningClass, bytes);
	}

	
	public byte[] transformClass(Class<?> classToTrans, byte[] b) {
	    ClassPool pool = ClassPool.getDefault();
	    pool.importPackage("javax.xml.parsers.DocumentBuilderFactory"); //add for xml
	    CtClass cl = null;
	    try {
	    	cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
	    	//CtBehavior[] methods = cl.getDeclaredBehaviors();
	      
	    	System.out.println("JX - DEBUG - -1");
	    	transformClass( cl );
	       
	    	System.out.println("JX - DEBUG - 12");
	      	b = cl.toBytecode();
	      	System.out.println("JX - DEBUG - 13");
	    }
	    catch (Exception e) {
	    	System.out.println("JX - DEBUG - 14");
	    	e.printStackTrace();
	    }
	    finally {
	    	System.out.println("JX - DEBUG - 15");
	    	if (cl != null) {
	    		cl.detach();
	    	}
	    }
	    System.out.println("JX - DEBUG - 16");
	    return b;
	}
  
	
  	// modifiedd by JX for mr-4576 & ha-4584
  	public void transformClass(CtClass cl) {}
  	//public void transformMethod(CtClass cl, CtBehavior method) {} //implement in difficult application

}


