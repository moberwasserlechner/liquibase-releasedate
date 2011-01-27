package liquibase.ext.releasedate.filter;

import java.util.Date;

import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.filter.ChangeSetFilter;
import liquibase.ext.releasedate.ReleaseDateChange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author m.oberwasserlechner@mum-software.com
 */
public class ReleaseDateChangeSetFilter implements ChangeSetFilter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Date installationDate;
  
  /**
   * If true this adives the filter to not accept ChangeSets
   */
  private boolean acceptIfNotExists = false;
  
  public ReleaseDateChangeSetFilter(Date installationDate) {
    this.installationDate = installationDate;
  }
  
  /**
   * @param installationDate
   * @param acceptIfNotExists
   */
  public ReleaseDateChangeSetFilter(Date installationDate, boolean acceptIfNotExists) {
    this.installationDate = installationDate;
    this.acceptIfNotExists = acceptIfNotExists;
  }

  /**
   * @see liquibase.changelog.filter.ChangeSetFilter#accepts(liquibase.changelog.ChangeSet)
   */
  @Override
  public boolean accepts(ChangeSet changeSet) {
    // ### Release Date ###
    ReleaseDateChange releaseDateChange = null;
    for (Change change : changeSet.getChanges()) {
      // only one releaseDateChange per ChangeSet allowed/processed
      if (change instanceof ReleaseDateChange) {
        releaseDateChange = (ReleaseDateChange) change;
        break;
      }
    } // for (changesets)
    
    boolean result = false;
    if (releaseDateChange != null) {
      Date releasedOn = releaseDateChange.getReleasedOn();
      if (releasedOn.after(installationDate)) {
        result = true;
      } else {
          logger.debug("Not accepted! ChangeSet '{}' was released before the current installation! ({} < {})", 
              new Object[] {changeSet.getId(), releasedOn, this.installationDate});
      }
    } else {
      if (this.acceptIfNotExists) {
        result = true;
      } else {
        logger.debug("ChangeSet '{}' does not contain a ReleaseDateChange. The Filter is advised to not accept such ChangeSets!", 
            changeSet.getId());
      }
    }
    return result;
  }

  
  /**
   * @return the acceptIfNotExists
   */
  public boolean isAcceptIfNotExists() {
    return acceptIfNotExists;
  }

  
  /**
   * @param acceptIfNotExists the acceptIfNotExists to set
   */
  public void setAcceptIfNotExists(boolean acceptIfNotExists) {
    this.acceptIfNotExists = acceptIfNotExists;
  }

}
