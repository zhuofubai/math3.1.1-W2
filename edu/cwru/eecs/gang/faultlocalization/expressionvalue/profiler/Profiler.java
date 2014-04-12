/**
 Copyright (c) 2010 Gang Shu (gang.shu@case.edu)
 Software Engineering Laboratory (http://selserver.case.edu/),
 EECS, Case Western Reserve University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Stack;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.SubjectProgram;

public class Profiler {

	final public static String fp_AsmProfiler = Profiler.class.getCanonicalName().replace(".", "/");
	
	private static int _testId = -1;
	private static String _filename = null;
	private static Stack<Integer> _methodIdStack = new Stack<Integer>();
	
	static {
		_methodIdStack.push(-1);
	}
	
	private static BufferedWriter _out = null;
	
	/*******************************************************
	 * Execution Profile Manager
	 *******************************************************/
	public static void stopProfiling() {
		try {
			if (_out != null)
				_out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			_out = null;
		}
	}
	
	public static void visitNewTest(int nxtTestId) {
		_testId = nxtTestId;
		_filename = SubjectProgram.folder_out_executions + _testId + ".txt";
		
		try {
			if (_out != null)
				_out.close();
			
			String tarfileDir = _filename.substring(0, _filename.lastIndexOf("/"));
			File f = new File(tarfileDir);
			if (!f.exists())
				f.mkdirs();
			_out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_filename, false)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*******************************************************
	 * Method and Statement Information
	 *******************************************************/
	final public static String visitMethodEnter_MethodCall = "visitMethodEnter";
	final public static String visitMethodEnter_Signatures = "(I)V";
	public static void visitMethodEnter(int methodId) {
		_methodIdStack.push(methodId);		
//		try{
//			if (_out!=null)
//				_out.write("[Enter]\t" + methodId + "\r\n");
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}

	final public static String visitMethodExit_MethodCall = "visitMethodExit";
	final public static String visitMethodExit_Signatures = "()V";
	public static void visitMethodExit() {
		_methodIdStack.pop();
	}
	
	final public static String visitLineNumber_MethodCall = "visitLineNumber";
	final public static String visitLineNumber_Signatures = "(I)V";
	public static void visitLineNumber(int lineNumber) {
	}
	
	
	/*******************************************************
	 * Expression Numerical Values
	 *******************************************************/
	private static int lineNumber = -1;
	private static ArrayList<Object> expressionInputs = null;
	private static String expressionOperator = null;
	
	final public static String visitExpression_Entry_MethodCall = "visitExpression_Entry";
	final public static String visitExpression_Entry_Signatures = "(Ljava/lang/String;I)V";
	public static void visitExpression_Entry(String operator, int line){
		expressionInputs = new ArrayList<Object>();
		expressionOperator = operator;
		lineNumber = line;
	}
	
	final public static String visitExpression_Input_MethodCall = "visitExpression_Input";
	final public static String visitExpression_Input_Signatures = "(Ljava/lang/Object;)V";
	public static void visitExpression_Input(Object input){
		expressionInputs.add(input);
	}
	
	final public static String visitExpression_Input_Long_MethodCall = "visitExpression_Input_Long";
	final public static String visitExpression_Input_Long_Signatures = "(Ljava/lang/Object;)J";
	public static long visitExpression_Input_Long(Object input){
		expressionInputs.add(input);
		return (Long)input;
	}
	
	final public static String visitExpression_Input_Double_MethodCall = "visitExpression_Input_Double";
	final public static String visitExpression_Input_Double_Signatures = "(Ljava/lang/Object;)D";
	public static double visitExpression_Input_Double(Object input){
		expressionInputs.add(input);
		return (Double)input;
	}
	
	final public static String visitExpression_End_MethodCall = "visitExpression_End";
	final public static String visitExpression_End_Signatures = "(Ljava/lang/Object;)V";
	public static void visitExpression_End(Object output){
		output(expressionOperator, expressionInputs, output, false);
	}
	
	final public static String visitMethodCall_End_MethodCall = "visitMethodCall_End";
	final public static String visitMethodCall_End_Signatures = "(Ljava/lang/Object;)V";
	public static void visitMethodCall_End(Object output){
		output(expressionOperator, expressionInputs, output, true);
	}
	
	private static void output(String OperType, ArrayList<Object> inputs, Object output, boolean order_top2end){
		
		if (OperType!=null && inputs!=null){
			
			/*
			 * (1) Header
			 */
			String header = "#" + _testId + "_" + _methodIdStack.peek() + "_";
			header += lineNumber==-1 ? "X" : lineNumber;
			header += "\t" + "[" + OperType + "]" + "\t";
			
			
			/*
			 * (2) Content
			 */
			//Output
			String out = "<" + (output!=null? output.toString() : output) + ">" + "\t";
			
			//Inputs
			String in = "{";
			if (order_top2end){
				for (Object input : inputs)
					in += (input.toString() + ",");
			}
			else {
				for (int i=inputs.size()-1; i>=0; i--)
					in += (inputs.get(i).toString() + ",");
			}
			in += "}";
			
			/*
			 * (3) Replace
			 */
			String content = out + in;
			content = content.replace(",}", "}");
			content = content.replace("<>", "");
			content = content.replace("{}", "");
			
			/*
			 * (4) Write Out
			 */
			try{
				String outStr = header + content + "\r\n";
				outStr = outStr.replace("\t\t", "\t");
				if (_out!=null)
					_out.write(outStr);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		OperType = null;
		inputs = null;
	}
}
