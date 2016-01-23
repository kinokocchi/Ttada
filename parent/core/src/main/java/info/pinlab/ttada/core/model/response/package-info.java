/**
 * @author Gabor Pinter
 *



- Response (Interface)
    |
	- ResponseHeader    # session id, usr, attempt... etc meta-info
    |
 	- ResponseContent (Interface)   
	     |
	     - ResponseContentEmpy (EmptyResponse)
	     |
	     - ResponseContentText
	     |
	     - ResponseContentAudio
	     |
	     - ...

 */
package info.pinlab.ttada.core.model.response;