package simplecommand.handlers;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ScaffoldHandler;
import com.model.Context;
import com.model.MethodContext;
import com.model.Workspace;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(
//				window.getShell(),
//				"SimpleCommand",
//				"Hello, Eclipse world");
	/*	
		try {
			// create a project with name "TESTJDT"
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject("TESTJDT");
			project.create(null);
			project.open(null);
			
			//create folder by using resources package
			IFolder folder = project.getFolder("src");
			folder.create(true, true, null);
			 
			 
			//set the Java nature
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			 
			//create the project
			project.setDescription(description, null);
			IJavaProject javaProject = JavaCore.create(project);
			 
			//set the build path
			IClasspathEntry[] buildPath = {
					JavaCore.newSourceEntry(project.getFullPath().append("src")),
							JavaRuntime.getDefaultJREContainerEntry() };
			 
			javaProject.setRawClasspath(buildPath, project.getFullPath().append(
							"bin"), null);
			 

			//Add folder to Java element
			IPackageFragmentRoot srcFolder = javaProject
							.getPackageFragmentRoot(folder);
			 
			//create package fragment
			IPackageFragment fragment = srcFolder.createPackageFragment(
					"com.programcreek", true, null);
			 
			//init code string and create compilation unit
			String str = "package com.programcreek;" + "\n"
				+ "public class Test  {" + "\n" + " private String name;"
				+ "\n" + "}";
			 
					ICompilationUnit cu = fragment.createCompilationUnit("Test.java", str,
							false, null);
			 
			//create a field
			IType type = cu.getType("Test");
			
			
			
			type.createField("@Description(value = \"ds\")@Deprecated private String age;", null, true, null);
			IType findType = javaProject.findType("com.programcreek.Test");
		findType.createMethod("@Override public String toString(){return \"hello\";}", null, true, null);
		
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
		ICompilationUnit compilationUnit = findType.getCompilationUnit();
		String source = compilationUnit.getSource();
		TextEdit textEdit = codeFormatter.format(
				CodeFormatter.K_COMPILATION_UNIT, source, 0,
				source.length(), 0, null);
		compilationUnit.applyTextEdit(textEdit, null);
		compilationUnit.save(null, true);
		JavaUI.openInEditor(compilationUnit);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		Context.addAttribute("shell", HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
		
		
		InputStream fis = getClass()
			    .getResourceAsStream("../../scaffold.xml");
		//File file = new File("scaffold.xml");
		Workspace workspace =null;
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Workspace.class);
			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
			workspace =(Workspace)jaxbUnMarshaller.unmarshal(fis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MethodContext.addAttribute("method1", "m1");
		
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		ScaffoldHandler.mainHandler(workspace, iWorkspace.getRoot());
		return null;
	}
}
