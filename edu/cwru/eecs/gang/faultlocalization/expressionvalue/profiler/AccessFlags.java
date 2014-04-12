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

import org.objectweb.asm.Opcodes;

public class AccessFlags {

	public synchronized static boolean isStatic(int access) {
		return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
	}

	public synchronized static boolean isPublic(int access) {
		return (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
	}

	public synchronized static int changeIntoPublic(int access) {
		if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
			return access;
		} else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
			return access - Opcodes.ACC_PRIVATE + Opcodes.ACC_PUBLIC;
		} else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
			return access - Opcodes.ACC_PROTECTED + Opcodes.ACC_PUBLIC;
		} else {
			return access + Opcodes.ACC_PUBLIC;
		}
	}

}
