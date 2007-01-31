/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
 *  
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.jruby.compiler;

import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;

/**
 * Compiler represents the current state of a compiler and all appropriate
 * transitions and modifications that can be made within it. The methods here begin
 * and end a class for a given compile run, begin and end methods for the script being
 * compiled, set line number information, and generate code for all the basic
 * operations necessary for a script to run.
 * 
 * The intent of this interface is to provide a library-neutral set of functions for
 * compiling a given script using any backend or any output format.
 */
public interface Compiler {
    /**
     * Begin compilation for a script, preparing all necessary context and code
     * to support this script's compiled representation.
     */
    public void startScript();
    
    /**
     * End compilation for the current script, closing all context and structures
     * used for the compilation.
     */
    public void endScript();
    
    /**
     * Begin compilation for a method that has the specified number of local variables.
     * The returned value is a token that can be used to end the method later.
     * 
     * @param localVarCount The number of local variables that will be used by the method.
     * @return An Object that represents the method within this compiler. Used in calls to
     * endMethod once compilation for this method is completed.
     */
    public Object beginMethod(String friendlyName, int arity, int localVarCount);
    
    /**
     * End compilation for the method associated with the specified token. This should
     * close out all structures created for compilation of the method.
     */
    public void endMethod(Object token);
    
    /**
     * As code executes, values are assumed to be "generated", often by being pushed
     * on to some execution stack. Generally, these values are consumed by other
     * methods on the context, but occasionally a value must be "thrown out". This method
     * provides a way to discard the previous value generated by some other call(s).
     */
    public void consumeCurrentValue();
    
    /**
     * This method provides a way to specify a line number for the current piece of code
     * being compiled. The compiler may use this information to create debugging
     * information in a bytecode-format-dependent way.
     */
    public void lineNumber(ISourcePosition node);
    
    /**
     * Invoke the named method as a "function", i.e. as a method on the current "self"
     * object, using the specified argument count. It is expected that previous calls
     * to the compiler has prepared the exact number of argument values necessary for this
     * call. Those values will be consumed, and the result of the call will be generated.
     */
    public void invokeDynamic(String name, boolean hasReceiver, boolean hasArgs, ClosureCallback closureArg);
    
    /**
     * Invoke the block passed into this method, or throw an error if no block is present.
     * If arguments have been prepared for the block, specify true. Otherwise the default
     * empty args will be used.
     */
    public void yield(boolean hasArgs);
    
    /**
     * Assigns the previous value to a local variable at the specified index, consuming
     * that value in the process.
     * 
     * @param index The index of the local variable to which to assign the value.
     */
    public void assignLocalVariable(int index);
    
    public void retrieveLocalVariable(int index);
    
    public void retrieveSelf();
    
    /**
     * Generate a new "Fixnum" value.
     */
    public void createNewFixnum(long value);

    /**
     * Generate a new "Bignum" value.
     */
    public void createNewBignum(java.math.BigInteger value);
    
    /**
     * Generate a new "String" value.
     */
    public void createNewString(String value);

    /**
     * Generate a new "Symbol" value (or fetch the existing one).
     */
    public void createNewSymbol(String name);
    
    /**
     * Combine the top <pre>elementCount</pre> elements into a single element, generally
     * an array or similar construct. The specified number of elements are consumed and
     * an aggregate element remains.
     * 
     * @param elementCount The number of elements to consume
     */
    public void createObjectArray(Object[] elementArray, ArrayCallback callback);

    /**
     * Given an aggregated set of objects (likely created through a call to createObjectArray)
     * create a Ruby array object.
     */
    public void createNewArray();
    
    public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    public void performLogicalAnd(BranchCallback longBranch);
    
    public void performLogicalOr(BranchCallback longBranch);
    
    public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
    
    public void createNewClosure(StaticScope scope, int arity, ClosureCallback body);
    
    public void defineNewMethod(String name, int arity, int localVarCount, ClosureCallback body);
    
    public void defineAlias(String newName, String oldName);
    
    public void retrieveConstant(String name);
    
    public void loadFalse();
    
    public void loadTrue();
    
    public void loadNil();
    
    public void retrieveInstanceVariable(String name);
    
    public void assignInstanceVariable(String name);
    
    public void assignGlobalVariable(String name);
    
    public void retrieveGlobalVariable(String name);
    
    public void negateCurrentValue();
}
