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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.SubjectProgram;

/**
 * @author Gang Shu
 *
 */
public class JavaAdviceAdapter extends AdviceAdapter {

	private static int _access;
	private static int _methodId = -1, _lineNumber = -1;
	private String _className, _methodName, _methodSignature;
	private static boolean _append = false;

	public JavaAdviceAdapter(MethodVisitor mv, int access, String owner, String name, String desc, String inClassFile) {
		super(mv, access, name, desc);
		_access = access;
		_className = owner.replace("/", ".");
		_methodName = name;
		_methodSignature = desc;
	}

	@Override
	public void visitCode() {
		super.visitCode();
	}

	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
	}
	
	@Override
	public void visitIincInsn(int var, int increment){
//		System.out.println(var + "," + increment);
		
		//(1) visitExpression_Entry
		super.visitLdcInsn(new String("IINC"));
		super.visitLdcInsn(new Integer(_lineNumber));
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_Entry_MethodCall, 
				Profiler.visitExpression_Entry_Signatures);
		
		//(2) visitExpression_Inputs
		super.visitLdcInsn(new Integer(increment));
		super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_Input_MethodCall, 
				Profiler.visitExpression_Input_Signatures);
		super.visitVarInsn(ILOAD, var);
		super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_Input_MethodCall, 
				Profiler.visitExpression_Input_Signatures);
		
		super.visitIincInsn(var, increment);
		
		//(3) visitExpression_End
		super.visitVarInsn(ILOAD, var);
		super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_End_MethodCall, 
				Profiler.visitExpression_End_Signatures);
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		boolean bInvokeSuperVisitInsn = false;
		
		switch (opcode) {
			case IADD :
			case ISUB :
			case IMUL :
			case IDIV :
			case IREM :
			case IAND :
			case IOR :
			case IXOR :
			case ISHL :
			case ISHR :
			case IUSHR :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case FADD :
			case FSUB :
			case FMUL :
			case FDIV :
			case FREM :
			case FCMPL :
			case FCMPG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2);

				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);				
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case LADD :
			case LSUB :
			case LMUL :
			case LDIV :
			case LREM :
			case LAND :
			case LOR :
			case LXOR :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2_X2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_Long_MethodCall, 
						Profiler.visitExpression_Input_Long_Signatures);
				
				super.visitInsn(DUP2_X2);
				super.visitInsn(POP2);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case DADD :
			case DSUB :
			case DMUL :
			case DDIV :
			case DREM :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2_X2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_Double_MethodCall, 
						Profiler.visitExpression_Input_Double_Signatures);
				
				super.visitInsn(DUP2_X2);
				super.visitInsn(POP2);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case LSHL :
			case LSHR :	
			case LUSHR :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP_X2);				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				super.visitInsn(DUP2_X1);
				super.visitInsn(POP2);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);				
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case LCMP :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2_X2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_Long_MethodCall, 
						Profiler.visitExpression_Input_Long_Signatures);

				super.visitInsn(DUP2_X2);
				super.visitInsn(POP2);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case DCMPL :
			case DCMPG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2_X2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_Double_MethodCall, 
						Profiler.visitExpression_Input_Double_Signatures);

				super.visitInsn(DUP2_X2);
				super.visitInsn(POP2);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case INEG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case FNEG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);				
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case LNEG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				
				//(3) visitExpression_End
				super.visitInsn(opcode);				
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case DNEG :
				//(1) visitExpression_Entry
				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
				super.visitLdcInsn(new Integer(_lineNumber));
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Entry_MethodCall, 
						Profiler.visitExpression_Entry_Signatures);
				
				//(2) visitExpression_Inputs
				super.visitInsn(DUP2);
				
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
			
				//(3) visitExpression_End
				super.visitInsn(opcode);
				super.visitInsn(DUP2);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_End_MethodCall, 
						Profiler.visitExpression_End_Signatures);
				bInvokeSuperVisitInsn = true;
				break;
				///////////////////////////////////////////
				///////////////////////////////////////////
			case ICONST_M1 :
				break;
			case ICONST_0 :
				break;
			case ICONST_1 :
				break;
			case ICONST_2 :
				break;
			case ICONST_3 :
				break;
			case ICONST_4 :
				break;
			case ICONST_5 :
				break;
			case LCONST_0 :
				break;
			case LCONST_1 :
				break;
			case FCONST_0 :
				break;
			case FCONST_1 :
				break;
			case FCONST_2 :
				break;
			case DCONST_0 :
				break;
			case DCONST_1 :
				break;
			case IALOAD :
				break;
			case BALOAD :
				break;
			case CALOAD :
				break;
			case SALOAD :
				break;
			case FALOAD :
				break;
			case LALOAD :
				break;
			case DALOAD :
				break;
			case AALOAD :
				break;
			case IASTORE :
				break;
			case BASTORE :
				break;
			case CASTORE :
				break;
			case SASTORE :
				break;
			case FASTORE :
				break;
			case LASTORE :
				break;
			case DASTORE :
				break;
			case NOP :
				break;
			case ACONST_NULL :
				break;
			case AASTORE :
				break;
			case POP :
				break;
			case POP2 :
				break;
			case DUP :
				break;
			case DUP_X1 :
				break;
			case DUP_X2 :
				break;
			case DUP2 :
				break;
			case DUP2_X1 :
				break;
			case DUP2_X2 :
				break;
			case SWAP :
				break;
//			case I2L :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case I2F :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);				
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case I2D :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case I2B :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);				
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case I2S :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);				
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case I2C :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);				
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case L2I :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);				
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case L2F :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case L2D :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case F2I :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);		
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case F2L :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case F2D :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_End_MethodCall, 
//						Profiler.visitExpression_End_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case D2I :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case D2F :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
//			case D2L :
//				//(1) visitExpression_Entry
//				super.visitLdcInsn(new String(ByteOps.getOps(opcode)));
//				super.visitLdcInsn(new Integer(_lineNumber));
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Entry_MethodCall, 
//						Profiler.visitExpression_Entry_Signatures);
//				
//				//(2) visitExpression_Inputs
//				super.visitInsn(DUP2);
//				
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				
//				//(3) visitExpression_End
//				super.visitInsn(opcode);
//				super.visitInsn(DUP2);
//				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
//				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
//						Profiler.visitExpression_Input_MethodCall, 
//						Profiler.visitExpression_Input_Signatures);
//				bInvokeSuperVisitInsn = true;
//				break;
			case IRETURN :
				break;
			case LRETURN :
				break;
			case FRETURN :
				break;
			case DRETURN :
				break;
			case ARETURN :
				break;
			case RETURN :
				break;
			case ARRAYLENGTH :
				break;
			case ATHROW :
				break;
			case MONITORENTER :
				break;
			case MONITOREXIT :
				break;
		}

		if (!bInvokeSuperVisitInsn)
			super.visitInsn(opcode);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		super.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitTypeInsn(int opcode, String name) {
		super.visitTypeInsn(opcode, name);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		super.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		
		String className = owner.replace("/", ".");
		String methodName = name;
		String methodSignature = desc;
		String invokeMethod = className + "." + methodName + " " + methodSignature;
		
		if (invokeMethod.contains("edu.cwru.eecs.gang")){
			super.visitMethodInsn(opcode, owner, name, desc);
		}
		else{

			//(1) visitExpression_Entry
			super.visitLdcInsn(new String("CallSite-Enter~" + invokeMethod));
			super.visitLdcInsn(new Integer(_lineNumber));
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_Entry_MethodCall, 
					Profiler.visitExpression_Entry_Signatures);
			
			super.visitLdcInsn(new String());
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
			
			//////////////////////////////////////////////////
			super.visitMethodInsn(opcode, owner, name, desc);
			//////////////////////////////////////////////////
			
			super.visitLdcInsn(new String("CallSite-Exit~" + invokeMethod));
			super.visitLdcInsn(new Integer(_lineNumber));
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_Entry_MethodCall, 
					Profiler.visitExpression_Entry_Signatures);
			
			super.visitLdcInsn(new String());
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		
		
	}
	
	@Override
	protected void onMethodEnter() {

		BufferedWriter out = null;
		try {
			_methodId++;
			String filename = SubjectProgram.file_out_methods;
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, _append)));
			String function_ClassName_and_Signature = _className + "." + _methodName + " " + _methodSignature;
			String output = _methodId + "\t" + function_ClassName_and_Signature + "\r\n";
			out.write(output);
			_append = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		super.visitLdcInsn(_methodId);
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, Profiler.visitMethodEnter_MethodCall, Profiler.visitMethodEnter_Signatures);
		

		//////////////////////////////////////////////////////////////////////////////
		String[] tokens = _methodSignature.split("\\(|\\)");
		String args = tokens[1];
		ArrayList <String> argAL = getSignatureAL(args);
		
		String invokeMethod = _className + "." + _methodName + " " + _methodSignature;
		
		//(1) visitExpression_Entry
		super.visitLdcInsn(new String("Function-Enter~" + invokeMethod));
		super.visitLdcInsn(new Integer(-1));
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_Entry_MethodCall, 
				Profiler.visitExpression_Entry_Signatures);
				
		//(2) visitExpression_Inputs
		int index = AccessFlags.isStatic(_access) ? 0 : 1;
		
		for (String arg : argAL){
			if (arg.equals("I")){
				super.visitVarInsn(ILOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("B")){
				super.visitVarInsn(ILOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("S")){
				super.visitVarInsn(ILOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("C")){
				super.visitVarInsn(ILOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("Z")){
				super.visitVarInsn(ILOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("F")){
				super.visitVarInsn(FLOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index++;
			}
			else if (arg.equals("J")){
				super.visitVarInsn(LLOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index+=2;
			}
			else if (arg.equals("D")){
				super.visitVarInsn(DLOAD, index);
				super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
						Profiler.visitExpression_Input_MethodCall, 
						Profiler.visitExpression_Input_Signatures);
				index+=2;
			}
			else{
				index++;
			}
		}
		
		//(3) visitExpression_End
		super.visitLdcInsn(new String(""));
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitMethodCall_End_MethodCall, 
				Profiler.visitMethodCall_End_Signatures);
		
		super.onMethodEnter();
	}

	@Override
	protected void onMethodExit(int opcode) {
		
		String invokeMethod = _className + "." + _methodName + " " + _methodSignature;
		
		//(1) visitExpression_Entry
		super.visitLdcInsn(new String("Function-Exit~" + invokeMethod));
		super.visitLdcInsn(new Integer(-1));
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
				Profiler.visitExpression_Entry_MethodCall, 
				Profiler.visitExpression_Entry_Signatures);
		
		String[] tokens = _methodSignature.split("\\(|\\)");
		String ret = tokens[2];
		
		if (_methodName.equals("bobyqb")){
			ret = "V";
		}
		
		if (ret.equals("V")){
			super.visitLdcInsn(new String("Void"));
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("I")){
			//(3) visitExpression_End
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("B")){
			//(3) visitExpression_End
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("S")){
			//(3) visitExpression_End
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("C")){
			//(3) visitExpression_End
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("Z")){
			//(3) visitExpression_End
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("F")){
			//(3) visitExpression_End			
			super.visitInsn(DUP);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("J")){
			//(3) visitExpression_End		
			super.visitInsn(DUP2);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else if (ret.equals("D")){
			//(3) visitExpression_End
			super.visitInsn(DUP2);
			super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		else{
			super.visitLdcInsn(new String("NonPrimitiveType"));
			super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, 
					Profiler.visitExpression_End_MethodCall, 
					Profiler.visitExpression_End_Signatures);
		}
		
		super.visitMethodInsn(INVOKESTATIC, Profiler.fp_AsmProfiler, Profiler.visitMethodExit_MethodCall,  Profiler.visitMethodExit_Signatures);
		super.onMethodExit(opcode);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		_lineNumber = line;
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitMaxs(int stack, int locals) {
		super.visitMaxs(stack + 10, locals);
	}
	
	public static ArrayList <String> getSignatureAL(String args){
		
		ArrayList <String> variableTypeList = new ArrayList <String> ();
		
		if (args!=null){
			StringBuffer arrayType = new StringBuffer();
			String tmp;
			char c;
			
			args = args.trim();
			
//			//if the return value of ReturnDescriptor is void.
			if (args.length()==1 && args.equals("V"))
				return variableTypeList;
			
			for (int i=0; i<args.length(); i++){
				c = args.trim().charAt(i);
				if (c=='['){
					arrayType.append(args.charAt(i));
					continue;
				}
				else if (c=='L'){
					for (int j=i; j<args.length(); j++)
						if (args.charAt(j)==';'){
							tmp = args.substring(i, j+1).toString();
							variableTypeList.add(arrayType+tmp);
							i = j;
							arrayType = new StringBuffer("");
							break;
						}
				}
				else if (c=='B'||c=='S'||c=='I'||c=='J'||c=='F'||c=='D'||c=='Z'||c=='C'||c=='V'){
					variableTypeList.add(arrayType.toString()+c);
					arrayType = new StringBuffer("");
				}
			}
		}
		
		return variableTypeList;
	}

}
