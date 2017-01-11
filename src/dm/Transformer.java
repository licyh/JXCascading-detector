package dm;

import java.security.*;
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

  //default function in javassist
  public byte[] transform(ClassLoader loader, String className, Class redefiningClass,
    ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
    return transformClass(redefiningClass, bytes);
  }

  public byte[] transformClass(Class classToTrans, byte[] b) {
    ClassPool pool = ClassPool.getDefault();
    pool.importPackage("javax.xml.parsers.DocumentBuilderFactory"); //add for xml
    CtClass cl = null;
    try {
      cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
      CtBehavior[] methods = cl.getDeclaredBehaviors();
      //Added by JX
      //System.out.println("JX - CLASS - " + cl.getName() );
      /*
      for (CtBehavior method : methods) 
    	System.out.println( method.getName() + " @ " + method.getSignature() 
  	  		+ " - constr?" + method.getMethodInfo().isConstructor() + " - cl?" + method.getMethodInfo().isStaticInitializer() + " - meth?" + method.getMethodInfo().isMethod());
      */
      //end-Added
      for (CtBehavior method : methods) {
        if (method.isEmpty() == false) {
          //Added by JX
          /*
          if ( cl.getName().contains("BaseContainerTokenSecretManager")
        		  || cl.getName().contains("ContainerExecutor") ) {
        	  System.out.println( method.getName() + " @ " + method.getSignature() 
        	  	+ " - constr?" + method.getMethodInfo().isConstructor() + " - cl?" + method.getMethodInfo().isStaticInitializer() + " - meth?" + method.getMethodInfo().isMethod());
          }
          */
          //end-Added
          transformMethod(cl, method);
        }
      }
      
      //Added by JX
      //System.out.println("JX - will enter a class for instrumenting target codes");
      transformClassForCodeSnippets(cl, methods);
      //end-Added
      
      b = cl.toBytecode();
    }
    catch (Exception e) { e.printStackTrace();}
    finally {
      if (cl != null) {
        cl.detach();
      }
    }
    return b;
  }

  //Added by JX
  public void transformClassForCodeSnippets(CtClass cl, CtBehavior[] methods) {}
  //end-Added
  
  public void transformMethod(CtClass cl, CtBehavior method) {} //implement in difficult application
  
}


