package org.eclipse.uml.a2acsl.ui.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml.a2acsl.main.Activity2Acsl;
import org.eclipse.uml.a2acsl.ui.Activator;

public class GenerateAcslFromActivity implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public GenerateAcslFromActivity() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IRunnableWithProgress operation = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				IFile file = null;
				try {
					file = getFile();
					IPath path = file.getRawLocation().makeAbsolute();
					Activity2Acsl.generateACSLContracts(path.toFile());
				} catch (Exception e) {
					IStatus status = new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				} finally {
					try {
						file.getProject().refreshLocal(
								IResource.DEPTH_INFINITE, monitor);
					} catch (CoreException e) {
						IStatus status = new Status(IStatus.ERROR,
								Activator.PLUGIN_ID, e.getMessage(), e);
						Activator.getDefault().getLog().log(status);
					}
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService()
					.run(true, true, operation);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private IFile getFile() {
		return (IFile) ((IStructuredSelection) selection).getFirstElement();
	}

}
