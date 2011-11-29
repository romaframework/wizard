package @project.package@.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.romaframework.aspect.security.annotation.SecurityAction;

public interface DirectService {

	/*
	 * Return to the home page. It's bound to: /app/direct/home. The security constraints specified
   * require any authenticated account to access to this address.
	 */
	@SecurityAction(roles = "user:.*")
	public void home();

	/*
	 * Open the control panel. It's bound to: /app/direct/controlPanel. The security constraints specified require any authenticated
	 * account to access to this address.
	 */
	@SecurityAction(roles = "profile:Administrator")
	public void controlPanel();

	/*
	 * Change the password of current account. It's bound to: /app/direct/changePassword. The
   * security constraints specified require any authenticated account to access to this address.
	 */
	@SecurityAction(roles = "user:.*")
	public void changePassword();

	/*
	 * Logout the current session if any. It's bound to: /app/direct/logout.
	 */
	public void logout(HttpServletRequest iRequest, HttpServletResponse iResponse);

	// INSERT YOUR ENTRY POINTS HERE.
}