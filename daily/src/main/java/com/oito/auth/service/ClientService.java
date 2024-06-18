/**
 *
 */
package com.oito.auth.service;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ClientTO;
import com.oito.auth.web.bean.ClientLoginResponse;
import com.oito.auth.web.bean.ClientResponse;

/**
 * Basic Client Services interface
 *
 * @author Jobin John
 *
 */
public interface ClientService {

	ClientResponse create(ClientTO clientTO);

	ClientLoginResponse login(ClientTO clientTO);

	ClientLoginResponse userLogin(ClientTO clientTO);

	ClientLoginResponse userNeverExpireTokenLogin(ClientTO clientTO);
	
	AppUserTO validateClientCredentials(final String clientId, final String secretKey) ;

}
