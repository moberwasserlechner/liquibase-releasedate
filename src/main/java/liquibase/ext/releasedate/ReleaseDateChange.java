package liquibase.ext.releasedate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;


/**
 * Liquibase extension to be able to give a {@link ChangeSet} a release date. 
 * <p>
 * This could be used to give more information or to exclude/include only special ChangeSets in a upgrade,
 * if you use Liquibase wihin a installer module of your application.
 * </p>
 * 
 * @author m.oberwasserlechner@mum-software.com
 *
 */
public class ReleaseDateChange extends AbstractChange {
  
  /**
   * The default comment text, with the date place holder at its end.
   */
  private static final String DEFAULT_RELEASED_ON_TEXT = "released on: {}";
  /**
   * The default pattern for parsing the incoming release date {@link String} using {@link SimpleDateFormat} 
   */
  private static final String DEFAULT_PARSE_PATTERN = "yyyy-MM-dd";
  /**
   * The default pattern for formating the release date {@link Date} using {@link SimpleDateFormat} for the sql-comment. 
   */
  private static final String DEFAULT_FORMAT_PATTERN = "MMMMM dd, yyyy";
  
  /**
   * The release date.
   */
  private Date releasedOn;
  
  /**
   * {@link SimpleDateFormat} pattern for parsing the incoming date-String. Defaults to {@link #DEFAULT_PARSE_PATTERN}
   * <p>Note: The incoming string is leniently parsed.
   */
  private String parsePattern = DEFAULT_PARSE_PATTERN;
  
  /**
   * {@link SimpleDateFormat} pattern for formating the {@link #releasedOn} date. Defaults to {@link #DEFAULT_FORMAT_PATTERN} 
   */
  private String formatPattern = DEFAULT_FORMAT_PATTERN;
  /**
   * <p>The text, in which the releaseDate will be inserted. Within the text following placeholder is used: {}.
   * <p>Defaults to {@link #DEFAULT_RELEASED_ON_TEXT}
   * @param commentText the commentText to set
   */
  private String commentText = DEFAULT_RELEASED_ON_TEXT;
  
  public ReleaseDateChange() {
    super("releaseDate", "Enables the user to transparently control the release of a ChangeSet.", ChangeMetaData.PRIORITY_DEFAULT);
  }

  /**
   * @see liquibase.change.Change#getConfirmationMessage()
   */
  public String getConfirmationMessage() {
    return buildComment();
  }

  /**
   * @see liquibase.change.Change#generateStatements(liquibase.database.Database)
   */
  public SqlStatement[] generateStatements(Database database) {
    List<SqlStatement> list = new ArrayList<SqlStatement>();
    if (this.releasedOn != null) {
      CommentStatement commentStatement = new CommentStatement(buildComment());
      list.add(commentStatement);
    }
    return list.toArray(new SqlStatement[list.size()]);
  }
  
  /**
   * Builds the comment text with a formatted date.
   * @return comment text
   */
  private String buildComment() {
    SimpleDateFormat sdf = null;
    try {
      sdf = new SimpleDateFormat(this.formatPattern, Locale.ENGLISH);
    } catch (IllegalArgumentException e) {
      sdf = new SimpleDateFormat(DEFAULT_FORMAT_PATTERN, Locale.ENGLISH);
    }
    return this.commentText.replaceAll("\\{\\}", sdf.format(this.releasedOn));
  }

  
  /**
   * Gets the release date.
   * @return the releasedOn
   */
  public Date getReleasedOn() {
    return releasedOn;
  }

  
  /**
   * @param releasedOn the releasedOn to set
   */
  public void setReleasedOn(String releasedOn) {
    SimpleDateFormat sdf = null;
    try {
      sdf = new SimpleDateFormat(this.parsePattern);
      sdf.setLenient(true);
    } catch (IllegalArgumentException e) {
      sdf = new SimpleDateFormat(DEFAULT_PARSE_PATTERN);
    }
    
    try {
      this.releasedOn = sdf.parse(releasedOn);
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
  }

  
  public String getCommentText() {
    return commentText;
  }
  
  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }
  
  public String getParsePattern() {
    return parsePattern;
  }
  
  public void setParsePattern(String parsePattern) {
    this.parsePattern = parsePattern;
  }

  
  public String getFormatPattern() {
    return formatPattern;
  }
  
  public void setFormatPattern(String formatPattern) {
    this.formatPattern = formatPattern;
  }

}
