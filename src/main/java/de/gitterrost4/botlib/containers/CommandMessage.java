// $Id $
// (C) cantamen/Paul Kramer 2019
package de.gitterrost4.botlib.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.gitterrost4.botlib.helpers.ArgumentTokenizer;

/**
 * TODO documentation
 */
public class CommandMessage {
  public final String realMessage;
  public final List<String> arguments;
  public final String commandSeparator;

  public CommandMessage(String realMessage, String commandSeparator) {
    super();
    this.realMessage = realMessage.trim().isEmpty() ? null : realMessage.trim();
    this.arguments = Optional.ofNullable(this.realMessage).map(ArgumentTokenizer::tokenize).orElse(new ArrayList<>());
    this.commandSeparator = commandSeparator;
  }

  public Optional<String> getTokenizedArg(int index){
    return Optional.ofNullable(arguments.get(index));
  }
  
  /**
   * get the argument provided by the zero-based index or throw IllegalArgument if none is present
   * 
   * @param index
   * @return
   */
  public String getTokenizedArgOrThrow(int index) {
    return getTokenizedArg(index).orElseThrow(() -> new IllegalArgumentException("too few parameters"));
  }

  public Optional<String> getArg(int index, boolean untilEnd) {
    try {
      return Optional.ofNullable(realMessage).map(s -> s.split(commandSeparator, untilEnd ? index + 1 : 0)[index]);
    } catch (@SuppressWarnings("unused") IndexOutOfBoundsException e) {
      return Optional.empty();
    }
  }

  /**
   * get the argument provided by the zero-based index.
   * 
   * @param index
   * @return
   */
  public Optional<String> getArg(int index) {
    return getArg(index, false);
  }

  /**
   * get the argument provided by the zero-based index or throw IllegalArgument if none is present
   * 
   * @param index
   * @return
   */
  public String getArgOrThrow(int index) {
    return getArgOrThrow(index,false);
  }

  /**
   * get the argument provided by the zero-based index or throw IllegalArgument if none is present
   * 
   * @param index
   * @return
   */
  public String getArgOrThrow(int index, boolean untilEnd) {
    return getArg(index, untilEnd).orElseThrow(() -> new IllegalArgumentException("too few parameters"));
  }

  public boolean hasContent() {
    return realMessage != null;
  }

  @Override
  public String toString() {
    return realMessage;
  }

}

// end of file
