package  @project.package@.view.domain;

import org.romaframework.aspect.flow.annotation.FlowAction;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.message.Message;
import org.romaframework.frontend.domain.message.MessageResponseListener;
import org.romaframework.frontend.view.domain.RomaControlPanel;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.view.domain.ChangePassword;

public class Header implements MessageResponseListener {

	public Header() {
	}

	@ViewAction(label = "", render = ViewConstants.RENDER_BUTTON)
	public void home() {
		BaseAccount currAccount = Roma.session().getAccount();
		Roma.flow().forward(currAccount.getProfile().getHomePage(),"screen://body");
	}

	@ViewAction(label = "", render = ViewConstants.RENDER_BUTTON)
	public void changePassword() {
		BaseAccount currAccount = Roma.session().getAccount();
		Roma.flow().forward(new ChangePassword(currAccount, this), "screen:popup");
	}

	@ViewAction(label = "", render = ViewConstants.RENDER_BUTTON)
	@FlowAction(next = RomaControlPanel.class, position="screen://body")
	public void controlPanel() {
	}

	public void responseMessage(Message iMessage, Object iResponse) {
		home();
	}

}
