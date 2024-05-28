package com.scholarsync.server.constants;

import com.scholarsync.server.types.levelType;
import java.util.Map;

public class LevelMap {
  public static Map<Integer, levelType> levelMap =
      Map.ofEntries(
          Map.entry(0, levelType.Newbie),
          Map.entry(100, levelType.Learner),
          Map.entry(300, levelType.Initiate),
          Map.entry(600, levelType.Contender),
          Map.entry(1000, levelType.Skilled),
          Map.entry(1500, levelType.Veteran),
          Map.entry(2500, levelType.Master),
          Map.entry(3000, levelType.Grand_Master),
          Map.entry(4000, levelType.Champion),
          Map.entry(5000, levelType.Legend));

  public static Integer getNextLevelNumber(levelType levelType) {
    boolean returnNext = false;
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (returnNext) {
        return entry.getKey();
      }
      if (entry.getValue().equals(levelType)) {
        returnNext = true;
      }
    }
    return 0;
  }

  public static Integer getKeyFromLevel(levelType levelType) {
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (entry.getValue().equals(levelType)) {
        return entry.getKey();
      }
    }
    return 0;
  }

  public static levelType getLevelByXp(Integer xp) {
    levelType currentLevel = levelType.Newbie;
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (xp >= entry.getKey()) {
        currentLevel = entry.getValue();
      } else {
        break;
      }
    }
    return currentLevel;
  }
}
