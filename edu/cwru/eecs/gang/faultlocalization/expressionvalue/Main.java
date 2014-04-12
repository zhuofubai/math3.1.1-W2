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
package edu.cwru.eecs.gang.faultlocalization.expressionvalue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.JavaClassLoader;

public class Main {

	public static void main(String[] args) throws Exception {

		/**
		 * (1) Instrument classes
		 */
		instrumentClasses(SubjectProgram.base_bin, SubjectProgram.base_bin2);

		/**
		 * (2) Copy necessary classes
		 */
		String[] items = Main.class.getCanonicalName().replace(".", "/")
				.split("/");
		String base_Inserter = items[0] + "/" + items[1] + "/";
		String folder_bin = SubjectProgram.base_bin + base_Inserter;
		String folder_bin2 = SubjectProgram.base_bin2 + base_Inserter;
		copyNecessaryClasses(folder_bin, folder_bin2);
	}

	private static void instrumentClasses(String folder_bin, String folder_bin2)
			throws Exception {

		try {
			File f = new File(folder_bin);
			if (f.isDirectory()) {
				Dir dir = new Dir();
				dir.getDir(folder_bin);
				ArrayList<String> classFiles = dir.FileList;
				if (classFiles != null) {

					for (String inClassFile : classFiles) {
						String outClassFile = inClassFile.replace(folder_bin,
								folder_bin2);

						boolean flag = false;

						// ----------------------------------------------------------------
						String[] ignoredClasses = new String[] {
								"edu/cwru/eecs/gang/faultlocalization/expressionvalue",
								"org/apache/commons/math3/util/ArithmeticUtils",
								//"org/apache/commons/math3/util/MathArrays",
								"org/apache/commons/math3/transform/TransformUtils",
								"org/" };
						for (String ignoredClass : ignoredClasses)
							if (inClassFile.contains(ignoredClass)) {
								// To be ignored
								flag = true;
							}
						// ----------------------------------------------------------------
						String[] profiledClasses = new String[] { //"BOBYQAOptimizer_bug"// ,
																"Rotation_bug"
						};
						for (String profiledClass : profiledClasses)
							if (inClassFile.contains(profiledClass)) {
								// To be profiled
								flag = false;
								System.out.println(inClassFile);
							}
						// ----------------------------------------------------------------

						if (flag) {
							FileChannel source = null;
							FileChannel destination = null;
							try {
								source = new FileInputStream(inClassFile)
										.getChannel();
								destination = new FileOutputStream(outClassFile)
										.getChannel();
								destination.transferFrom(source, 0,
										source.size());
							} finally {
								if (source != null) {
									source.close();
								}
								if (destination != null) {
									destination.close();
								}
							}
							continue;
						}

						// System.out.println(inClassFile);

						JavaClassLoader instr = new JavaClassLoader();
						instr.replace(inClassFile, outClassFile);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void copyNecessaryClasses(String srcPath, String tarPath) {

		try {
			File f = new File(srcPath);
			if (f.isDirectory()) {
				Dir dir = new Dir();
				dir.getDir(srcPath);
				ArrayList<String> classFiles = dir.FileList;
				if (classFiles != null)
					for (String inClassFile : classFiles) {
						String outClassFile = tarPath + "/"
								+ inClassFile.replace(srcPath, "");
						String tarfileDir = outClassFile.substring(0,
								outClassFile.lastIndexOf("/"));
						File f2 = new File(tarfileDir);
						if (!f2.exists())
							f2.mkdirs();

						try {
							File sourceFile = new File(inClassFile);
							File destFile = new File(outClassFile);

							if (!destFile.exists()) {
								destFile.createNewFile();
							}
							FileChannel source = null;
							FileChannel destination = null;
							try {
								source = new FileInputStream(sourceFile)
										.getChannel();
								destination = new FileOutputStream(destFile)
										.getChannel();
								destination.transferFrom(source, 0,
										source.size());
							} finally {
								if (source != null) {
									source.close();
								}
								if (destination != null) {
									destination.close();
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static class Dir {
		public ArrayList<String> FileList = new ArrayList<String>();

		public void getDir(String absPath) throws Exception {
			try {
				File f = new File(absPath);
				if (f.isDirectory()) {
					File[] fList = f.listFiles();
					for (int j = 0; j < fList.length; j++)
						if (fList[j].isDirectory()) {
							getDir(fList[j].getPath());
						}
					String str = "";
					for (int j = 0; j < fList.length; j++)
						if (fList[j].isFile()) {
							str = fList[j].getPath();
							if (str.endsWith(".class")) {
								str = str.replace("\\", "/");
								FileList.add(new String(str));
							}
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
