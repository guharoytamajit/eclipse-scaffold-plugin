package com;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.text.edits.TextEdit;

import com.model.Context;
import com.model.JavaField;
import com.model.JavaFile;
import com.model.JavaMethod;
import com.model.Project;
import com.model.Template;
import com.model.Workspace;
import com.util.Dialog;

public class ScaffoldHandler {
	private static final String CODE_GENERATION_SKIPPED = ".Hence code generation skipped.";
	private static final String FIELD_ALREADY_EXISTS_DO_YOU_WANT_TO_OVERWRITE = " field already exists do you want to overwrite?";
	private static final String CLASS_ALREADY_EXISTS_DO_YOU_WANT_TO_OVERWRITE = " class already exists do you want to overwrite?";
	private static final String SCAFFOLD_PLUGIN = "scaffold plugin";
	

	public static void mainHandler(Workspace workspace, IWorkspaceRoot root) {
		Context.addAttribute(Keys.CURRENT_WORKSPACE, root);
		List<Project> projectList = workspace.getProject();
		for (Project project : projectList) {

			projectHandler(project);
		}
	}

	public static void projectHandler(Project project) {
		String projectName = project.getName();
		List<String> srcList = project.getSrc();
		IProject iProject = createProject(
				(IWorkspaceRoot) Context.getAttribute(Keys.CURRENT_WORKSPACE),
				projectName);

		IJavaProject javaProject = createJavaProject(srcList, iProject);

		List<JavaFile> javaFiles = project.getJavaFile();
		for (JavaFile javaFile : javaFiles) {
			javaFileHandler(javaFile, projectName, javaProject, iProject);
		}
	}

	private static IProject createProject(IWorkspaceRoot root,
			String projectName) {
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
		return iProject;
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
		Context.addAttribute("JAVA_PROJECT", javaProject);

		String javaFilename = javaFile.getName();
		String postPackage = javaFile.getPostPackage();
		String completePackage = postPackage;
		String src = javaFile.getSource();
		String copyFromFile = javaFile.getCopy();
		Set<String> imports = javaFile.getImports();
		if(completePackage==null)completePackage="";
		String completeJavaFileName=completePackage==null?javaFilename:new StringBuffer().append(completePackage).append(".").append(javaFilename).toString();

		IFolder folder = createSourceDirectory(iProject, src);
		// Add folder to Java element
		IPackageFragmentRoot srcFolder = javaProject
				.getPackageFragmentRoot(folder);

		IPackageFragment fragment = createPackageDirectory(completePackage,
				srcFolder);
		ICompilationUnit cu = null;
		if (copyFromFile != null) {
			try {
				
				IType iType = javaProject.findType(completeJavaFileName);
				if(iType!=null && iType.exists()){
					boolean overwrite = Dialog.questionDialog(SCAFFOLD_PLUGIN, completeJavaFileName+CLASS_ALREADY_EXISTS_DO_YOU_WANT_TO_OVERWRITE);
				if(!overwrite)return;
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
			// project_file[0] is from project
			// project_file[1] is from java file
			String[] project_file = copyFromFile.split("::");

			IProject fromProject = ((IWorkspaceRoot) Context
					.getAttribute(Keys.CURRENT_WORKSPACE))
					.getProject(project_file[0]);
			IJavaProject fromJavaProject = JavaCore.create(fromProject);

			cu = copyCompilationUnit(project_file[1], fromJavaProject,
					javaFilename, fragment);
		} else {
			cu = generateCompilationUnit(javaFilename, fragment);

			declarePackage(completePackage, cu);
			
			IType itype = cu.getType(javaFilename);
			
			handleFieldGeneration(javaFile, itype);
			handleMethodGeneration(javaFile, itype);
			
			//itype.newSupertypeHie
			
			if(imports!=null){
				
				declareImports(cu,imports.toArray(new String[0]));
			}
		}

		formatSource(cu);
		
		/////////////////////////

		
		
		///////////////////////
		Context.removeAttribute("JAVA_PROJECT");
	}

	private static void declareImports(ICompilationUnit cu,String... imports) {
		for (String import_class : imports) {
			try {
				cu.createImport(import_class, null, null);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	private static ICompilationUnit copyCompilationUnit(String fromFileName,
			IJavaProject fromJavaProject, String toJavaFilename,
			IPackageFragment toJavaPackage) {
		IType classToCopy = null;
		
		try {
			classToCopy = fromJavaProject.findType(fromFileName);
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		try {
			classToCopy.getCompilationUnit().copy(toJavaPackage, null,
					toJavaFilename + ".java", false, null);
			return toJavaPackage.getCompilationUnit(toJavaFilename + ".java");
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private static void copyField(String copyFromString,IType itype){
		String[] coomponents = copyFromString.split("::");
		if(coomponents.length!=3)return;
		if(coomponents[0].isEmpty() || coomponents[1].isEmpty() || coomponents[2].isEmpty())return ;

		IProject fromProject = ((IWorkspaceRoot) Context
				.getAttribute(Keys.CURRENT_WORKSPACE))
				.getProject(coomponents[0]);
		IJavaProject fromJavaProject = JavaCore.create(fromProject);
	    IType classToCopy = null;
		
		try {
			classToCopy = fromJavaProject.findType(coomponents[1]);
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		IField fieldToCopy = classToCopy.getField(coomponents[2]);
		if(fieldToCopy==null)return;
		if(fieldToCopy.exists()){
			try {
				fieldToCopy.copy(itype, null, fieldToCopy.getElementName(), false, null);
				declareImports(itype.getCompilationUnit(), getFieldDeclarationFromICompilationUnit(classToCopy.getCompilationUnit(),fieldToCopy.getElementName()).getType().resolveBinding().getQualifiedName());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

	}
	
	private static IFolder createSourceDirectory(IProject iProject, String src) {
		// create folder by using resources package
		IFolder folder = iProject.getFolder(src);
		try {
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		} catch (CoreException e) {
			System.out.println("unable to create source:" + src
					+ "for project:");
			e.printStackTrace();
		}
		return folder;
	}

	private static IPackageFragment createPackageDirectory(
			String completePackage, IPackageFragmentRoot srcFolder) {
		IPackageFragment fragment = srcFolder
				.getPackageFragment(completePackage);
		if (!fragment.exists()) {

			// create package fragment
			try {
				fragment = srcFolder.createPackageFragment(completePackage,
						true, null);
			} catch (JavaModelException e) {
				System.out.println("unable te create package "
						+ completePackage + " for project ");
				e.printStackTrace();
			}
		}
		return fragment;
	}

	private static ICompilationUnit generateCompilationUnit(
			String javaFilename, IPackageFragment fragment) {
		ICompilationUnit cu = fragment.getCompilationUnit(javaFilename
				+ ".java");
		if (!cu.exists()) {
			try {
				cu = fragment.createCompilationUnit(javaFilename + ".java",
						"public class " + javaFilename + "{}", false, null);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return cu;
	}

	private static void declarePackage(String completePackage,
			ICompilationUnit cu) {
		try {
			cu.createPackageDeclaration(completePackage, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private static void handleFieldGeneration(JavaFile javaFile, IType itype) {
		List<JavaField> fields = javaFile.getFields();
		if(fields==null)return;
		for (JavaField field : fields) {
			if(field.getCopy()!=null && !field.equals("")){
				copyField(field.getCopy(), itype);
			}else{
				
				generateField(itype, field.getValue());
			}
		}
	}

	private static void handleMethodGeneration(JavaFile javaFile, IType itype) {

		List<JavaMethod> methods = javaFile.getMethods();
		if(methods==null)return;
		for (JavaMethod method : methods) {
			if (method.getGenerator() != null) {
				String methodString = generateMethod(method);
				if (methodString != null) {
					generateMethod(itype, generateMethod(method));
				}
			} else {
				generateMethod(itype,
						Template.populateTemplate(method.getValue()));
			}
		}
	}

	private static String generateMethod(JavaMethod method) {
		String methodString = null;

		try {
			Class generatorClass = Class.forName(method.getGenerator());
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

	public static IMethod generateMethod(IType type, String method) {
		IMethod iMethod = null;
		try {
			//contents,sibling,force,monitor
			iMethod = type.createMethod(method, null, false, null);
		} catch (JavaModelException e) {
			Dialog.errorDialog(SCAFFOLD_PLUGIN,"Method "+ e.getMessage()+" :"+type.getFullyQualifiedName()+CODE_GENERATION_SKIPPED);
			e.printStackTrace();
		}

		return iMethod;
	}

	public static void generateField(IType type, String field) {
		try {
			
			//contents,sibling,force,monitor
			type.createField(field, null, false, null);
		} catch (JavaModelException e) {
			Dialog.errorDialog(SCAFFOLD_PLUGIN,"Field "+ e.getMessage()+" :"+type.getFullyQualifiedName()+CODE_GENERATION_SKIPPED);
			e.printStackTrace();
		}
	}
	
	public void test(){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		ICompilationUnit cu=null;
		parser.setSource(cu); // set source
		parser.setResolveBindings(true); // we need bindings later on
		final CompilationUnit compilationUnit = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);

		List<AbstractTypeDeclaration> types = compilationUnit.types();
		
		for (AbstractTypeDeclaration type2 : types) {
			if (type2.getNodeType() == ASTNode.TYPE_DECLARATION) {
				List<BodyDeclaration> bodies = type2.bodyDeclarations();
				for (BodyDeclaration body : bodies) {
					if (body.getNodeType() == ASTNode.FIELD_DECLARATION) {
						FieldDeclaration field = (FieldDeclaration) body;
					}}
			}
	}
	}
	
	private static List<BodyDeclaration> bodyDeclarationFromICompilationUnit(ICompilationUnit cu){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(cu); // set source
		parser.setResolveBindings(true); // we need bindings later on
		final CompilationUnit compilationUnit = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */);

		List<AbstractTypeDeclaration> types = compilationUnit.types();
		List<BodyDeclaration> bodies=null;
		for (AbstractTypeDeclaration type2 : types) {
			if (type2.getNodeType() == ASTNode.TYPE_DECLARATION) {
				 bodies = type2.bodyDeclarations();

			}
	}
		return bodies;
	}
	private static MethodDeclaration getMethodDeclarationFromICompilationUnit(ICompilationUnit cu,String methodName){
		for (BodyDeclaration body : bodyDeclarationFromICompilationUnit( cu)) {
			if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
				MethodDeclaration method = (MethodDeclaration) body;
				if(method.getName().toString().equals(methodName)){
					return method;
				}
			}}
		return null;
	}
	private static FieldDeclaration getFieldDeclarationFromICompilationUnit(ICompilationUnit cu,String fieldName){
		for (BodyDeclaration body : bodyDeclarationFromICompilationUnit( cu)) {
			if (body.getNodeType() == ASTNode.FIELD_DECLARATION) {
				FieldDeclaration field = (FieldDeclaration) body;
				if(((VariableDeclarationFragment)field.fragments().get(0)).getName().toString().equals(fieldName)){
					return field;
				}
			}}
		return null;
	}
}
