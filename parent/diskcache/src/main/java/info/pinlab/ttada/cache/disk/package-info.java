/**
 * 
 * Cache implementation using the local file system.
 * 


<pre> 

{@link info.pinlab.ttada.core.cache.LocalCache LocalCache} implements {@link info.pinlab.ttada.core.cache.Cache Cache}
  |
{@link info.pinlab.ttada.cache.disk.Ca DiskCache} implements {@link info.pinlab.ttada.core.cache.LocalCache LocalCache}
  | - wraps DiskCacheHandler
  | - initialized via {@link info.pinlab.ttada.cache.disk.DiskCache.DiskCacheBuilder DiskCacheBuilder}
  | - requires 
  |
{@link info.pinlab.ttada.cache.disk.DiskEnrollController DiskEnrollController}   
  | - manages local disk operations 
  | - can have special handles (e.g., saving different places at the same time, special format saves such as wav files) 
  | - all data is saved as Json

 
 </pre>


<b> Example code </b>
<pre>
DiskCache diskCache	= new DiskCacheBuilder()
	.setDiskCacheRootPath(new File(path))
	.setClassInterfaceMap(interfaceMap)
	.setTag2ClassMap(tag2ClazzMap)
	.setJsonAdapter(JSonAdapter )
	.build();
</pre>
 * 
 * 
 * @author Gabor Pinter
 */
package info.pinlab.ttada.cache.disk;
