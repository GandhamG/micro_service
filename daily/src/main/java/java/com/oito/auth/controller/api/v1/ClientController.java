package com.oito.auth.controller.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.ClientUserLoginRequest;
import com.oito.auth.mapper.ClientLoginRequesttoClientTOMapper;
import com.oito.auth.mapper.ClientUserLoginRequesttoClientTOMapper;
import com.oito.auth.service.ClientService;
import com.oito.auth.web.bean.ClientLoginRequest;
import com.oito.auth.web.bean.ClientLoginResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "authentication")
@RestController
@RequestMapping("api/v1/client")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientUserLoginRequesttoClientTOMapper clientMapper;

	@Autowired
	private ClientLoginRequesttoClientTOMapper mapper;

	@ApiOperation(value = "This API will help authenticate the sellers by generating the access tokens", notes = "This API will authenticate the seller requests before accessing the NOCNOC APIs."
			+ "After a seller is registered, every seller will be assigned a Client Id and Client Secret. "
			+ "This will be used to generate the access tokens. "
			+ "Access tokens will help access the APIs. After receiving the access tokens they’re embedded as part of an authorization header. "
			+ "NOCNOC APIs use OAuth for token based authentication and authorization. "
			+ "All API requests must be made over HTTPS. Calls made over plain HTTP will fail. "
			+ "API requests without authentication will also fail. The responses are always JSON format. "
			+ "Your Client Id and Client secret carry many privileges, so be sure to keep them secure! "
			+ "Do not share your Client Id and Client secret in publicly accessible areas such as GitHub, "
			+ " client-side code, and so forth.", nickname = "clientTokenRequest", code = 200, httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = ClientLoginResponse.class),
			@ApiResponse(code = 400, message = "Bad Request. Response body would contain more details"),
			@ApiResponse(code = 500, message = "Internal Server Error. Response body would contain more details") })
	@PostMapping("/token")
	public ClientLoginResponse token(@RequestBody final ClientLoginRequest clientRequest) {
		final var clientTO = mapper.toVO(clientRequest);
		return clientService.login(clientTO);
	}

	@ApiOperation(value = "This API will help authenticate the client users by generating the access tokens", notes = "This API will authenticate the requests before accessing the APIs."
			+ "After a user is registered, user can login with their username and password to generate the access tokens. "
			+ "Access tokens will help access the APIs. After receiving the access tokens they’re embedded as part of an authorization header. "
			+ "APIs use OAuth for token based authentication and authorization. "
			+ "All API requests must be made over HTTPS. Calls made over plain HTTP will fail. "
			+ "API requests without authentication will also fail. The responses are always JSON format. "
			+ "Your Username and password combination carry many privileges, so be sure to keep them secure! "
			+ "Do not share your username and password in publicly accessible areas such as GitHub, "
			+ " client-side code, and so forth.", nickname = "userNameClientTokenRequest", code = 200, httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = ClientLoginResponse.class),
			@ApiResponse(code = 400, message = "Bad Request. Response body would contain more details"),
			@ApiResponse(code = 500, message = "Internal Server Error. Response body would contain more details") })
	@PostMapping("/token/user")
	public ClientLoginResponse userToken(@RequestBody final ClientUserLoginRequest clientRequest) {
		final var clientTO = clientMapper.toVO(clientRequest);
		return clientService.userLogin(clientTO);
	}

	@PostMapping("/token/neverexpire/user")
	public ClientLoginResponse userNeverExpireToken(@RequestBody final ClientUserLoginRequest clientRequest) {
		final var clientTO = clientMapper.toVO(clientRequest);
		return clientService.userNeverExpireTokenLogin(clientTO);
	}

}
