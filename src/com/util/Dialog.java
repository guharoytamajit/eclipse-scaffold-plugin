package com.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.model.Context;

public class Dialog {
public static boolean confirmDialog(String title,String content){
	return MessageDialog.openConfirm((Shell)Context.getAttribute("shell"), title, content);
}
public static void errorDialog(String title,String content){
	 MessageDialog.openError((Shell)Context.getAttribute("shell"), title, content);
}
public static void informationDialog(String title,String content){
	 MessageDialog.openInformation((Shell)Context.getAttribute("shell"), title, content);
}
public static boolean questionDialog(String title,String content){
	return MessageDialog.openQuestion((Shell)Context.getAttribute("shell"), title, content);
}
public static void warningDialog(String title,String content){
	 MessageDialog.openWarning((Shell)Context.getAttribute("shell"), title, content);
}



}
