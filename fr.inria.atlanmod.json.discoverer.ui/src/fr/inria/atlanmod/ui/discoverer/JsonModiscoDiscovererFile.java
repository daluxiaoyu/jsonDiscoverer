package fr.inria.atlanmod.ui.discoverer;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
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
 * Discovers a metamodel from a json file
 * 
 * @author Javier Canovas (javier.canovas@inria.fr)
 *
 */
public class JsonModiscoDiscovererFile extends JsonModiscoDiscoverer implements IDiscoverer<org.eclipse.core.resources.IFile>  {
	
	public boolean isApplicableTo(final IFile source) {
		if(source.getFileExtension().toLowerCase().equals(EXTENSION)) return true;
		return false;
	}

	@Override
	public void discoverElement(IFile source, IProgressMonitor monitor)	throws DiscoveryException {
		JsonDiscoverer discoverer = new JsonDiscoverer();
		
		File sourceFile = new File(source.getRawLocationURI());
		File targetFile = new File(sourceFile.getAbsolutePath().substring(0, sourceFile.getAbsolutePath().lastIndexOf("."))+ ".ecore");

		EPackage ePackage = discoverer.discoverMetamodel(sourceFile);
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

//	@Override
//	protected void basicDiscoverElement(final IFile source, final IProgressMonitor monitor) throws DiscoveryException {
//		
//		JsonDiscoverer discoverer = new JsonDiscoverer();
//		EPackage ePackage = discoverer.discoverMetamodel(new File(source.getRawLocationURI()));
//		
//		createTargetModel();
//		Resource res = getTargetModel();
//		res.getContents().add(ePackage);
//		
//		try {
//			res.save(null);
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new DiscoveryException(e.getMessage());
//		}
//
//	}

}
