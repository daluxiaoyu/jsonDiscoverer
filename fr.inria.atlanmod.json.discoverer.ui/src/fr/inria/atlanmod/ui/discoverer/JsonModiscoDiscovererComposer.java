package fr.inria.atlanmod.ui.discoverer;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.modisco.infra.discovery.core.IDiscoverer;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.ui.PlatformUI;

import fr.inria.atlanmod.discoverer.JsonComposer;

public class JsonModiscoDiscovererComposer extends JsonModiscoDiscoverer implements IDiscoverer<IFile> {

	@Override
	public void discoverElement(IFile source, IProgressMonitor monitor) throws DiscoveryException {
		monitor.beginTask("Composing Ecore models", 1000);

		monitor.subTask("Selecting the Ecore file to compose");
		JsonDialog dialog = new JsonDialog("Select the Ecore model for composing", source);
		PlatformUI.getWorkbench().getDisplay().syncExec(dialog);	
		String path = dialog.getResult();
		monitor.worked(250);
		
		monitor.subTask("Composing models");
		File sourceFile1 = new File(source.getRawLocationURI());
		File sourceFile2 = new File(path);
		ArrayList<File> fileList = new ArrayList<File>();
		fileList.add(sourceFile1);
		fileList.add(sourceFile2);
		JsonComposer composer = new JsonComposer(fileList);
		File targetFile = new File(path.substring(0, path.lastIndexOf("."))+ "-composed.ecore");
		composer.compose(targetFile);
		monitor.worked(750);
		

		if (source.getParent() instanceof IResource) {
			try {
				((IResource) source.getParent()).refreshLocal(IResource.DEPTH_INFINITE,	new NullProgressMonitor());
			} catch (CoreException e) {
				monitor.done();
				throw new DiscoveryException(e);
			}
		}
		monitor.done();
		
	}

	@Override
	public boolean isApplicableTo(final IFile source) {
		if(source.getFileExtension().toLowerCase().equals(ECORE_EXTENSION)) return true;
		return false;
	}



}
