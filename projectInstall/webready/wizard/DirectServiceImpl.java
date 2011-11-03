package @project.package@.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.romaframework.aspect.service.annotation.ServiceClass;
import org.romaframework.core.Roma;
import org.romaframework.frontend.view.domain.RomaControlPanel;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseProfile;
import org.romaframework.module.users.view.domain.ChangePassword;

@ServiceClass(serviceName = "direct", interfaceClass = DirectService.class)
public class DirectServiceImpl implements DirectService {

	public void home() {
		BaseProfile currProfile = ((BaseAccount) Roma.session().getAccount()).getProfile();

		String homePage = "HomePage";
		if (currProfile != null && currProfile.getHomePage() != null)
			homePage = currProfile.getHomePage();

		// RESET THE HISTORY
		Roma.flow().clearHistory();

		// FORWARD TO THE CONFIGURED HOME PAGE
		Roma.flow().forward(homePage, "screen://body");
	}

	public void controlPanel() {

		// RESET THE HISTORY
		Roma.flow().clearHistory();

		// FORWARD TO THE CONFIGURED HOME PAGE
		Roma.flow().forward(RomaControlPanel.class, "screen://body");
	}

	public void logout(HttpServletRequest iRequest, HttpServletResponse iResponse) {
		try {
			iResponse.sendRedirect(iRequest.getContextPath() + "/dynamic/common/logout.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changePassword() {
		BaseAccount currAccount = Roma.session().getAccount();
		Roma.flow().forward(new ChangePassword(currAccount, null));
	}
}