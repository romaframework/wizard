package @project.package@.view.domain;

import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.core.Roma;

@ViewClass(layout = "screen://body")
public class HomePage {

	public HomePage() {
		Roma.flow().forward(new HeaderMainPage(),"screen://header");
	}

}
