package fr.inria.atlanmod.ui.discoverer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.modisco.infra.discovery.core.IDiscoverer;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;

import fr.inria.atlanmod.discoverer.JsonDiscoverer;

/**
 * Discovers a metamodel from a set of json files
 * 
 * @author Javier Canovas (javier.canovas@inria.fr)
 *
 */
public class JsonModiscoDiscovererFolder extends JsonModiscoDiscoverer implements IDiscoverer<IFolder>{
	@Override
	public boolean isApplicableTo(IFolder source) {
		try {
			for(IResource iResource : source.members()) 
				if (iResource instanceof IFile) {
					IFile iFile = (IFile) iResource;
					if(iFile.getFileExtension().toLowerCase().equals(EXTENSION))  
						return true;
				}
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}

	@Override
	public void discoverElement(IFolder source, IProgressMonitor monitor) throws DiscoveryException {
		List<File> jsonFiles = new ArrayList<File>();
		
		// Getting the files
		try {
			for(IResource iResource : source.members()) 
				if (iResource instanceof IFile) {
					IFile iFile = (IFile) iResource;
					if(iFile.getFileExtension().toLowerCase().equals(EXTENSION))  
						jsonFiles.add(new File(iFile.getRawLocationURI()));
				}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		

		JsonDiscoverer discoverer = new JsonDiscoverer();
		EPackage ePackage = null;
		for(int i = 0; i < jsonFiles.size(); i++) {
			File jsonFile = jsonFiles.get(i);
			if(i == 0) {
				ePackage = discoverer.discoverMetamodel(jsonFile);
			} else {
				ePackage = discoverer.refineMetamodel(jsonFile, ePackage);
			}
		}
		
		File targetFile = new File(jsonFiles.get(0).getAbsolutePath().substring(0, jsonFiles.get(0).getAbsolutePath().lastIndexOf("."))+ ".ecore");

		ResourceSet rset = new ResourceSetImpl();
		Resource res = rset.createResource(URI.createFileURI(targetFile.getAbsolutePath()));
		res.getContents().add(ePackage);
		
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DiscoveryException(e.getMessage());
		}
		
		if (source.getParent() instanceof IResource) {
			try {
				((IResource) source.getParent()).refreshLocal(IResource.DEPTH_INFINITE,	new NullProgressMonitor());
			} catch (CoreException e) {
				throw new DiscoveryException(e);
			}
		}
	}

}
