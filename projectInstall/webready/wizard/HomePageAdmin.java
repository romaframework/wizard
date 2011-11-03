package  @project.package@.view.domain;

import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.page.HomePageBasic;

public class HomePageAdmin extends HomePageBasic {

	public HomePageAdmin(){
		Roma.flow().forward(new HeaderMainPage(),"screen://header");
	}
	
	@Override
	protected void fillPages() {
		super.fillPages();
	}

}