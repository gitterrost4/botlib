package de.gitterrost4.botlib.helpers;

import java.time.Duration;
import java.util.List;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Utilities {

  public static String formatDuration(Duration duration) {
    String dayString = duration.toDays() > 0 ? duration.toHours() + " day, " : "";
    Duration minusDays = duration.minusDays(duration.toDays());
    String hourString = duration.toHours() > 0 ? minusDays.toHours() + " hrs, " : "";
    Duration minusHours = minusDays.minusHours(minusDays.toHours());
    String minuteString = duration.toMinutes() > 0 ? minusHours.toMinutes() + " min, " : "";
    String secondString = minusHours.minusMinutes(minusHours.toMinutes()).getSeconds() + " sec";
    return dayString + hourString + minuteString + secondString;
  }

  public static void deleteMessages(TextChannel channel, List<Message> retrievedHistory) {
    // retrievedHistory.add(event.getMessage());
    channel.purgeMessages(retrievedHistory);
  }

  public static boolean isNumericInt(String s) {
    if (s == null) {
      return false;
    }
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;

  }

  public static TimerTask timerTask(Runnable r) {
    return new TimerTask() {

      @Override
      public void run() {
        r.run();
      }
    };
  }
}
