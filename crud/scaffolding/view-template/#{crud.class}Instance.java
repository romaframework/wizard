package #{class.package};

import #{domain.package}#{domain.class};

import org.romaframework.frontend.domain.crud.CRUDInstance;

/**
 * Class used to display #{domain.class} instances on CRUD reading, creation and updating. It uses
 * the Extension-by-composition pattern.
 * <br/><br/>
 * This class is generated by Roma Meta Framework CRUD wizard.       
 * <br/><br/>
 * @author #{author}     
 */ 
public class #{crud.class}Instance extends CRUDInstance<#{domain.class}> {

  @Override
  public void onCreate() {
  }

  @Override
  public void onRead() {
  }

  @Override
  public void onUpdate() {
  }
}
