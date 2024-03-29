// $Id $
// (C) cantamen/Paul Kramer 2020
package de.gitterrost4.botlib.helpers;

/** 
 * TODO documentation
 *
 * @author (C) cantamen/Paul Kramer 2020
 * @version $Id $
 */
public enum Emoji {
  ROBOT("U+1F916"),
  APPLE("U+1F34E"),
  EYES("U+1F440"),
  THUMBSUP("U+1F44D"),
  THUMBSDOWN("U+1F44E"),
  WASTEBIN("U+1F5D1"),
  ARROW_UP_SMALL("U+1F53C"),
  ARROW_DOWN_SMALL("U+1F53D"),
  ARROW_FORWARD("U+25B6"),
  ARROW_BACKWARD("U+25C0"),
  ARROW_DOUBLE_FORWARD("U+23E9"),
  ARROW_DOUBLE_BACKWARD("U+23EA"),
  WHITE_CHECK_MARK("U+2705"),
  NEGATIVE_SQUARED_CROSS_MARK("U+274E"),
  ARROW_RIGHT("U+27A1"),
  BLACK_LARGE_SQUARE("U+2B1B"),
  ;
  private final String representation;
  private Emoji(String representation) {
    this.representation=representation;
  }
  
  public String asRepresentation() {
    return representation;
  }
  
  public String asString() {
    return new StringBuilder().appendCodePoint(Integer.parseInt(representation.substring(2),16)).toString();
  }
  
  public net.dv8tion.jda.api.entities.Emoji asEmoji(){
    return net.dv8tion.jda.api.entities.Emoji.fromUnicode(representation);
  }
  
}


// end of file
