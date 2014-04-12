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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JavaClassLoader extends ClassLoader implements Opcodes {

	public void replace(final String inClassFile, String outClassFile)
			throws Exception {

		FileInputStream fi = new FileInputStream(inClassFile);
		ClassReader cr = new ClassReader(fi);
		final String className = cr.getClassName();
		ClassWriter cw;
		ClassAdapter ca;

		try {
			cw = new ClassWriter(0);
			ca = new ClassAdapter(cw) {
				@Override
				public void visit(final int version, final int access,
						final String name, final String signature,
						final String superName, final String[] interfaces) {
					int access2 = AccessFlags.changeIntoPublic(access);
					super.visit(version, access2, name, signature, superName,
							interfaces);
				}
				@Override
				public MethodVisitor visitMethod(int access,
						String functionName, String functionDesc,
						String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, functionName,
							functionDesc, signature, exceptions);
					return new JavaAdviceAdapter(mv, access, className,
							functionName, functionDesc, inClassFile);
				}
				@Override
				public FieldVisitor visitField(int access, String name,
						String desc, String signature, Object value) {
					access = AccessFlags.changeIntoPublic(access);
					return super.visitField(access, name, desc, signature,
							value);
				}
			};
			cr.accept(ca, 0);
		}
		catch (Exception e) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			ca = new ClassAdapter(cw) {
				@Override
				public void visit(final int version, final int access,
						final String name, final String signature,
						final String superName, final String[] interfaces) {
					int access2 = AccessFlags.changeIntoPublic(access);
					super.visit(Opcodes.V1_6, access2, name, signature,
							superName, interfaces);
				}
				@Override
				public MethodVisitor visitMethod(int access,
						String functionName, String functionDesc,
						String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, functionName,
							functionDesc, signature, exceptions);
					return new JavaAdviceAdapter(mv, access, className,
							functionName, functionDesc, inClassFile);
				}
				@Override
				public FieldVisitor visitField(int access, String name,
						String desc, String signature, Object value) {
					access = AccessFlags.changeIntoPublic(access);
					return super.visitField(access, name, desc, signature,
							value);
				}
			};
			cr.accept(ca, ClassReader.SKIP_FRAMES);
		}

		try {
			String tarfileDir = outClassFile.substring(0,
					outClassFile.lastIndexOf("/"));
			File f = new File(tarfileDir);
			if (!f.exists())
				f.mkdirs();

			FileOutputStream fo = new FileOutputStream(outClassFile);
			fo.write(cw.toByteArray());
			fo.close();

		} catch (Exception e) {
			e.getStackTrace();
		}

	}

}
