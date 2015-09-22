package com;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.text.edits.TextEdit;

import com.generator.ICodeGenerator;
import com.model.JavaFile;
import com.model.JavaMethod;
import com.model.Project;
import com.model.Template;
import com.model.Workspace;

public class ScaffoldHandler {
	public static void mainHandler(Workspace workspace, IWorkspaceRoot root) {
		List<Project> projectList = workspace.getProject();
		for (Project project : projectList) {

			projectHandler(project, root);
		}
	}

	public static void projectHandler(Project project, IWorkspaceRoot root) {
		String projectName = project.getName();
		List<String> srcList = project.getSrc();
		IProject iProject = root.getProject(projectName);
		if (!iProject.exists()) {
			try {
				iProject.create(null);
				iProject.open(null);
			} catch (CoreException e) {
				System.out.println("unable to create or open " + projectName);
				e.printStackTrace();
			}
		}

		IJavaProject javaProject = createJavaProject(srcList, iProject);

		List<JavaFile> javaFiles = project.getJavaFile();
		for (JavaFile javaFile : javaFiles) {
			javaFileHandler(javaFile, projectName, javaProject, iProject);
		}
	}

	private static IJavaProject createJavaProject(List<String> srcList,
			IProject iProject) {
		// set the Java nature
		IProjectDescription description;
		try {
			description = iProject.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			iProject.setDescription(description, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// create the project
		IJavaProject javaProject = JavaCore.create(iProject);

		List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
		for (String src : srcList) {
			classpathEntries.add(JavaCore.newSourceEntry(iProject.getFullPath()
					.append(src)));
		}
		classpathEntries.add(JavaRuntime.getDefaultJREContainerEntry());
		// set the build path
		IClasspathEntry[] buildPath = classpathEntries
				.toArray(new IClasspathEntry[0]);

		try {
			javaProject.setRawClasspath(buildPath, iProject.getFullPath()
					.append("bin"), null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return javaProject;
	}

	public static void javaFileHandler(JavaFile javaFile, String projectName,
			IJavaProject javaProject, IProject iProject) {

		String javaFilename = javaFile.getName();
		String postPackage = javaFile.getPostPackage();
		String completePackage = postPackage;
		String src = javaFile.getSource();

		// create folder by using resources package
		IFolder folder = iProject.getFolder(src);
		try {
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		} catch (CoreException e) {
			System.out.println("unable to create source:" + src
					+ "for project:" + projectName);
			e.printStackTrace();
		}
		// Add folder to Java element
		IPackageFragmentRoot srcFolder = javaProject
				.getPackageFragmentRoot(folder);

		IPackageFragment fragment = srcFolder
				.getPackageFragment(completePackage);
		if (!fragment.exists()) {

			// create package fragment
			try {
				fragment = srcFolder.createPackageFragment(completePackage,
						true, null);
			} catch (JavaModelException e) {
				System.out.println("unable te create package "
						+ completePackage + " for project " + projectName);
				e.printStackTrace();
			}
		}

		ICompilationUnit cu = null;

		cu = fragment.getCompilationUnit(javaFilename + ".java");
		if (!cu.exists()) {
			try {
				cu = fragment.createCompilationUnit(javaFilename + ".java",
						"public class " + javaFilename + "{}", false, null);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// create a field
		IType itype = cu.getType(javaFilename);

		List<String> fields = javaFile.getFields();
		for (String field : fields) {
			generateField(itype, field);
		}
		List<JavaMethod> methods = javaFile.getMethods();
		for (JavaMethod method : methods) {
			if (method.getGenerator() != null) {
				String methodString = generateMethod(method);
				if (methodString != null) {
					generateMethod(itype, generateMethod(method));
				}
			} else {
				generateMethod(itype,Template.populateTemplate(method.getValue()));
			}
		}
		try {
			cu.createPackageDeclaration(completePackage, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		formatSource(cu);
	}

	private static String generateMethod(JavaMethod method) {
		String methodString = null;
		try {
			Class generatorClass =  Class.forName(method.getGenerator());
			Object generator = generatorClass.newInstance();
			Method generateMethod = generatorClass.getDeclaredMethod(
					"generate", new Class[] { Object.class });
			methodString = (String) generateMethod.invoke(generator,
					new Object());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return methodString;
	}

	private static void formatSource(ICompilationUnit cu) {
		try {
			CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
			String source = cu.getSource();
			TextEdit textEdit = codeFormatter.format(
					CodeFormatter.K_COMPILATION_UNIT, source, 0,
					source.length(), 0, null);
			cu.applyTextEdit(textEdit, null);
			cu.save(null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateMethod(IType type, String method) {
		try {
			type.createMethod(method, null, true, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public static void generateField(IType type, String field) {
		try {
			type.createField(field, null, true, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
